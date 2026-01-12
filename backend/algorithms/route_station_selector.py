from typing import List, Dict
from backend.models.stations import get_all_stations
from backend.algorithms.util import haversine

def select_measurement_stations(waypoints: List[Dict]) -> List[Dict]: # to działa
    stations_db = get_all_stations()
    selected = set()
    for wp in waypoints:
        dists = [
            (st["station_id"], haversine(
                float(wp["latitude"]),
                float(wp["longitude"]),
                float(st["lat"]),
                float(st["lon"])
            ))
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
            # selected.update([d[0] for d in dists[:4]])
            continue
    return [st for st in stations_db if st["station_id"] in selected]
