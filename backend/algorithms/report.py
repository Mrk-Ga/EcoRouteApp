from typing import List, Optional
import math

def calculate_route_distance(waypoints: List[dict]) -> Optional[float]:
    """
    Oblicza całkowity dystans trasy na podstawie waypointów (w metrach).
    Jeśli brakuje współrzędnych, pomija dany odcinek.
    """
    def haversine(lat1, lon1, lat2, lon2):
        R = 6371000  # promień Ziemi w metrach
        phi1 = math.radians(lat1)
        phi2 = math.radians(lat2)
        dphi = math.radians(lat2 - lat1)
        dlambda = math.radians(lon2 - lon1)
        a = math.sin(dphi/2)**2 + math.cos(phi1)*math.cos(phi2)*math.sin(dlambda/2)**2
        c = 2 * math.atan2(math.sqrt(a), math.sqrt(1-a))
        return R * c

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
    """
    Zwraca liczbę waypointów na trasie.
    """
    return len(waypoints) if waypoints else 0