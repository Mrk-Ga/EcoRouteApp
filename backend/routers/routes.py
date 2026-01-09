from fastapi import APIRouter, HTTPException, Query
from pydantic import BaseModel
from ..models.routes import create_waypoint, create_route, update_route, get_max_route_id, get_route_by_id, get_latest_waypoint_for_route, update_waypoint
from ..models.reports import generate_route_pollution_summary
from ..algorithms.route_station_selector import select_measurement_stations
from ..algorithms.alert import calculate_alert
from ..models.sensors import get_latest_readings_for_station
from typing import Optional
from datetime import datetime, UTC

router = APIRouter(prefix="/routes", tags=["routes"])


class LocationData(BaseModel):
    latitude: float
    longitude: float
    altitude: float = None
    timestamp: str

class RouteDataResponse:
    def __init__(self, PM25, PM10, AQI, alert, time):
        self.PM25 = PM25
        self.PM10 = PM10
        self.AQI = AQI
        self.alert = alert
        self.time = time

class StartTrackingResponse(BaseModel):
    route_id: int

class StartTrackingRequest(BaseModel):
    user_id: int

class StopTrackingRequest(BaseModel):
    user_id: int

class StopTrackingResponse(BaseModel):
    message: str

class PollutionSummaryRequest(BaseModel):
    report_id: int

@router.post("/start_tracking", response_model=StartTrackingResponse)
def start_tracking(request: StartTrackingRequest):
    now = datetime.now(UTC)
    route_id = create_route({
        "account_id": request.user_id,
        "start_time": now
    })
    return StartTrackingResponse(route_id=route_id)


@router.post("/stop_tracking", response_model=StopTrackingResponse)
def stop_tracking(request: StopTrackingRequest):
    now = datetime.now(UTC)
    last_id = get_max_route_id()
    route = get_route_by_id(last_id)
    if not route or route['account_id'] != request.user_id:
        return StopTrackingResponse(message="No active route found for this user.")
    update_route(last_id, {"end_time": now, "status": "completed"})
    return StopTrackingResponse(message=f"Tracking stopped for user {request.user_id}, route {last_id}")



@router.post("/{route_id}/location")
def post_location_data(route_id: int, location: LocationData):
    waypoint_id = create_waypoint({
        "route_id": route_id,
        "latitude": location.latitude,
        "longitude": location.longitude,
        "altitude": location.altitude,
        "timestamp": location.timestamp
    })
    return {"waypoint_id": waypoint_id}


@router.get("/{route_id}")
def get_route_info(route_id: int, time_check: Optional[bool] = Query(False, description="Weryfikuj czas odczytu (do 15 min)")):
    route = get_route_by_id(route_id)

    if not route:
        raise HTTPException(status_code=404, detail="Route not found")
    waypoints = []
    latest = get_latest_waypoint_for_route(route_id)

    if latest:
        waypoints.append(latest)
    stations = select_measurement_stations(waypoints)
    pm25_vals, pm10_vals, aqi_vals, times = [], [], [], []

    for st in stations:
        station_id = int(st["station_id"])
        readings = get_latest_readings_for_station(station_id)
                # readings = get_latest_readings_for_station(station_id, time_check_enabled=time_check)

        print(f"DEBUG readings for station {station_id}: {readings}")
        if readings["PM25"] is not None:
            pm25_vals.append(readings["PM25"])
        if readings["PM10"] is not None:
            pm10_vals.append(readings["PM10"])
        if readings["AQI"] is not None:
            aqi_vals.append(readings["AQI"])
        if readings["time"] is not None:
            times.append(readings["time"])

    PM25 = round(sum(pm25_vals)/len(pm25_vals), 2) if pm25_vals else None
    PM10 = round(sum(pm10_vals)/len(pm10_vals), 2) if pm10_vals else None
    AQI = round(sum(aqi_vals)/len(aqi_vals)) if aqi_vals else None
    time = max(times) if times else None # <- nie rozumiem po co ten czas ale spoko
    alert = calculate_alert(PM25, PM10, AQI) # <- to imo też imo mogłoby być we frontendzie

    if latest:
        update_waypoint(latest.get('waypoint_id'), {"pm25": PM25, "pm10": PM10, "aqi": AQI})
        
    return {
        "PM25": PM25,
        "PM10": PM10,
        "AQI": AQI,
        "alert": alert,
        "time": time
    }

@router.post("/{route_id}/pollution_summary")
def create_pollution_summary(route_id: int, req: PollutionSummaryRequest):
    summary = generate_route_pollution_summary(req.report_id, route_id)
    return {"summary": summary}