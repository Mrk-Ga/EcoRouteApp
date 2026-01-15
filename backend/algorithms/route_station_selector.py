from typing import List, Dict
from backend.models.stations import get_all_stations
from backend.algorithms.util import haversine

def select_measurement_stations(waypoints: List[Dict]) -> List[Dict]: # to działa
    stations_db = get_all_stations()
    selected = set()
    station_density = {st["station_id"]: 0 for st in stations_db}
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
            station_density[dists[0][0]] += 1
        close_stations = [d[0] for d in dists if d[1] <= 1000][:2]
        for sid in close_stations:
            selected.add(sid)
            station_density[sid] += 1
        mid_stations = [d[0] for d in dists if 1000 < d[1] <= 2000 and d[0] not in selected][:3]
        for sid in mid_stations:
            selected.add(sid)
            station_density[sid] += 1
        if not close_stations and not mid_stations:
            far_stations = [d[0] for d in dists if d[1] <= 4000][:4]
            for sid in far_stations:
                selected.add(sid)
                station_density[sid] += 1
    prioritized = sorted(selected, key=lambda sid: -station_density[sid])
    return [st for sid in prioritized for st in stations_db if st["station_id"] == sid]
