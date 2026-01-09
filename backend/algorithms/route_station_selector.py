from typing import List, Dict
import math
from backend.models.stations import get_all_stations

def haversine(lat1, lon1, lat2, lon2):
    R = 6371000 
    phi1 = math.radians(lat1)
    phi2 = math.radians(lat2)
    dphi = math.radians(lat2 - lat1)
    dlambda = math.radians(lon2 - lon1)
    a = math.sin(dphi/2)**2 + math.cos(phi1)*math.cos(phi2)*math.sin(dlambda/2)**2
    c = 2 * math.atan2(math.sqrt(a), math.sqrt(1 - a))
    return R * c

def select_measurement_stations(waypoints: List[Dict]) -> List[Dict]:
    stations_db = get_all_stations()
    selected = set()
    for wp in waypoints:
        dists = [
            (st["station_id"], haversine(wp["latitude"], wp["longitude"], st["lat"], st["lon"]))
            for st in stations_db
        ]
        dists.sort(key=lambda x: x[1])
        if not dists:
            continue
        if dists[0][1] <= 500:
            selected.add(dists[0][0])
        elif len(dists) > 1 and dists[1][1] <= 1500:
            selected.update([dists[0][0], dists[1][0]])
        elif len(dists) > 2 and dists[2][1] <= 3000:
            selected.update([dists[0][0], dists[1][0], dists[2][0]])
        else:
            selected.update([d[0] for d in dists[:4]])
    return [st for st in stations_db if st["station_id"] in selected]
