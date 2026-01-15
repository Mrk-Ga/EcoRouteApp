from fastapi import APIRouter, HTTPException, Query, Body
from pydantic import BaseModel
from sqlalchemy import String
# from ..models.reports import create_route_report
from ..models.routes import create_waypoint, create_route, update_route, get_max_route_id, get_route_by_id, get_latest_waypoint_for_route, update_waypoint, get_waypoints_for_route
from ..algorithms.report import calculate_route_distance, count_route_waypoints
# from ..models.reports import generate_route_pollution_summary, create_route_report, create_route_pollution_summary, save_route_report_with_stats
from ..models.reports import create_route_report, create_route_pollution_summary, get_route_report_by_id
from ..algorithms.route_station_selector import select_measurement_stations
from ..algorithms.alert import calculate_alert
from ..models.sensors import get_latest_readings_for_station
from typing import Optional
from datetime import datetime, UTC
from ..algorithms.report import calculate_pollution_summary


router = APIRouter(prefix="/routes", tags=["routes"])


class LocationData(BaseModel):
    latitude: float
    longitude: float
    altitude: Optional[float] = None
    timestamp: int

class RouteDataResponse:
    def __init__(self, PM25, PM10, AQI, alert, time):
        self.PM25 = PM25
        self.PM10 = PM10
        self.AQI = AQI
        # self.alert = alert
        # self.time = time

class StartTrackingResponse(BaseModel):
    routeId: str

class StartTrackingRequest(BaseModel):
    userId: int

class StopTrackingRequest(BaseModel):
    userId: int
    routeId: str

from typing import List, Dict, Any

class PollutionSummaryItem(BaseModel):
    pollutant_type: str
    avg_value: float
    peak_value: float

class StopTrackingResponse(BaseModel):
    message: str
    # route_id: str
    # total_duration_seconds: int = None
    # avg_speed: float = None
    # distance: float = None
    # waypoint_count: int = None
    # start_time: str = None
    # end_time: str = None
    # pollution_summary: List[PollutionSummaryItem] = []

class PollutionSummaryRequest(BaseModel):
    report_id: int

@router.post("/start_tracking", response_model=StartTrackingResponse)
def start_tracking(userId: int = Body(...)):
    now = datetime.now(UTC)
    route_id = create_route({
        "account_id": userId,
        "start_time": now
    })
    return StartTrackingResponse(routeId=str(route_id))

@router.post("/stop_tracking", response_model=StopTrackingResponse)
def stop_tracking(request: StopTrackingRequest):
    now = datetime.now(UTC)
    route = get_route_by_id(request.routeId)
    if not route or route['account_id'] != request.userId:
        return StopTrackingResponse(message="No active route found for this user.")
    update_route(request.routeId, {"end_time": now, "status": "completed"})

    waypoints = get_waypoints_for_route(request.routeId)
    distance = calculate_route_distance(waypoints)
    waypoint_count = count_route_waypoints(waypoints)

    if route.get('start_time') and now:
        total_duration_seconds = int((now - route['start_time']).total_seconds())
    else:
        total_duration_seconds = None

    avg_speed = None
    if distance is not None and total_duration_seconds and total_duration_seconds > 0:
        avg_speed = round((distance / 1000) / (total_duration_seconds / 3600), 2) 

    report_id = create_route_report({
        "route_id": request.routeId,
        "total_duration_seconds": total_duration_seconds,
        "avg_speed": avg_speed,
        "distance": distance,
        "waypoint_count": waypoint_count,
        "created_at": now
    })

    pollution_summary = calculate_pollution_summary(waypoints)
    pollution_summary_list = []
    if report_id:
        for pollutant_type, vals in pollution_summary.items():
            create_route_pollution_summary({
                "report_id": report_id,
                "pollutant_type": pollutant_type.upper(),
                "avg_value": vals["avg"],
                "peak_value": vals["peak"]
            })
            pollution_summary_list.append({
                "pollutant_type": pollutant_type.upper(),
                "avg_value": vals["avg"],
                "peak_value": vals["peak"]
            })

    return StopTrackingResponse(
        message=f"Tracking stopped for user {request.userId}, route {request.routeId}",
        # route_id=request.route_id,
        # total_duration_seconds=total_duration_seconds,
        # avg_speed=avg_speed,
        # distance=distance,
        # waypoint_count=waypoint_count,
        # start_time=str(route.get('start_time')) if route.get('start_time') else None,
        # end_time=str(now),
        # pollution_summary=pollution_summary_list
    )



@router.post("/{route_id}/location")
def post_location_data(route_id: int, location: LocationData):
    ts_val = location.timestamp
    if ts_val is not None:
        if ts_val > 1e12:
            ts_val = ts_val // 1000
        ts = datetime.fromtimestamp(ts_val)
    else:
        ts = None
    waypoint_id = create_waypoint({
        "route_id": route_id,
        "latitude": location.latitude,
        "longitude": location.longitude,
        "altitude": location.altitude,
        "timestamp": ts
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
    # time = max(times) if times else None # <- nie rozumiem po co ten czas ale spoko
    # alert = calculate_alert(PM25, PM10, AQI) # <- to imo też imo mogłoby być we frontendzie

    if latest:
        update_waypoint(latest.get('waypoint_id'), {"pm25": PM25, "pm10": PM10, "aqi": AQI})
        
    return {
        "PM25": PM25,
        "PM10": PM10,
        "AQI": AQI
        # "alert": alert,
        # "time": time
    }

# @router.post("/{route_id}/pollution_summary")
# def create_route_pollution_summary(route_id: int, req: PollutionSummaryRequest):
#     summary = generate_route_pollution_summary(req.report_id, route_id)
#     return {"summary": summary}

# @router.post("/{route_id}/generate_report")
# def generate_route_report(route_id: int):
#     result = save_route_report_with_stats(route_id)
#     if result is None:
#         raise HTTPException(status_code=400, detail="Not enough waypoints to generate report.")
#     return result