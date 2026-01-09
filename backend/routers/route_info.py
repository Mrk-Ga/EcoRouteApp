from fastapi import APIRouter, HTTPException
from ..models.routes import get_route_by_id, get_latest_waypoint_for_route
from ..algorithms.route_station_selector import select_measurement_stations
from ..algorithms.alert import calculate_alert
from ..models.sensors import get_latest_readings_for_station
from fastapi import Query
from typing import Optional

router = APIRouter(prefix="/routes", tags=["routes"])

class RouteDataResponse:
    def __init__(self, PM25, PM10, AQI, alert, time):
        self.PM25 = PM25
        self.PM10 = PM10
        self.AQI = AQI
        self.alert = alert
        self.time = time


@router.get("/{route_id}")
def get_route_info(route_id: int, time_check: Optional[bool] = Query(True, description="Weryfikuj czas odczytu (do 15 min)")):
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
        readings = get_latest_readings_for_station(st["station_id"], time_check_enabled=time_check)
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
    return {
        "PM25": PM25,
        "PM10": PM10,
        "AQI": AQI,
        "alert": alert,
        "time": time
    }
