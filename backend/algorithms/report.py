from typing import List, Optional
from backend.algorithms.util import haversine

def calculate_route_distance(waypoints: List[dict]) -> Optional[float]:
    if not waypoints or len(waypoints) < 2:
        return 0.0
    distance = 0.0
    prev = None
    for wp in waypoints:
        if prev:
            lat1, lon1 = prev.get('latitude'), prev.get('longitude')
            lat2, lon2 = wp.get('latitude'), wp.get('longitude')
            if None not in (lat1, lon1, lat2, lon2):
                distance += haversine(lat1, lon1, lat2, lon2)
        prev = wp
    return round(distance, 2)

def count_route_waypoints(waypoints: List[dict]) -> int:
    return len(waypoints) if waypoints else 0

def calculate_pollution_summary(waypoints: list) -> dict:
    pollutants = ['pm25', 'pm10', 'aqi']
    result = {}
    for pol in pollutants:
        values = [wp.get(pol) for wp in waypoints if wp.get(pol) is not None]
        if values:
            avg = round(sum(values) / len(values), 2)
            peak = round(max(values), 2)
            result[pol] = {"avg": avg, "peak": peak}
    return result