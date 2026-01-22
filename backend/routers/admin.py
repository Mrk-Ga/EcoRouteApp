from fastapi import APIRouter, HTTPException, Body
from typing import Any, Dict, List

from ..models.stations import (
    get_all_stations,
    update_station_status,
    get_station_measurements,
)

router = APIRouter(prefix="/admin", tags=["admin"])


@router.get("/stations")
def list_all_stations() -> List[Dict[str, Any]]:
    return get_all_stations()


@router.get("/stations/measurements/{stationId}")
def station_measurements(stationId: str):
    try:
        station_id = int(stationId)
    except ValueError:
        raise HTTPException(status_code=422, detail="stationId must be an integer")

    data = get_station_measurements(station_id)
    if data is None:
        raise HTTPException(status_code=404, detail="Station not found")
    return data


@router.post("/stations/{stationId}/status")
def change_station_status(stationId: str, status: bool = Body(...)):
    try:
        station_id = int(stationId)
    except ValueError:
        raise HTTPException(status_code=422, detail="stationId must be an integer")

    updated = update_station_status(station_id, status)
    if not updated:
        raise HTTPException(status_code=404, detail="Station not found")

    return {"ok": True}

