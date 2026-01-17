from fastapi import APIRouter, HTTPException, Body, Response
from pydantic import BaseModel
from typing import List
from ..models.sensor_reports import get_nearest_stations, create_sensor_report

router = APIRouter(prefix="/report", tags=["reports"])

class Sensor(BaseModel):
    sensorId: int
    type: str
    value: float
    unit: str
    lastUpdate: str

class StationReport(BaseModel):
    name: str
    distance: float
    sensors: List[Sensor]

@router.get("/sensors/{location}", response_model=List[StationReport])
def get_sensor_report(location: str):
    try:
        lat_str, lon_str = location.split("_")
        latitude = float(lat_str)
        longitude = float(lon_str)
        stations = get_nearest_stations(latitude, longitude, limit=5)
        return stations
    except ValueError:
        raise HTTPException(status_code=400, detail="Invalid location format. Use lat_lon")
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error fetching stations: {str(e)}")

@router.post("/sensors/{sensor_id}")
def post_sensor_report(sensor_id: int, report: str = Body(...)):
    try:
        from ..models.sensors import get_sensor_by_id
        sensor = get_sensor_by_id(sensor_id)
        if not sensor:
            raise HTTPException(status_code=404, detail=f"Sensor with id {sensor_id} not found")

        create_sensor_report(sensor_id, account_id=1, description=report)
        return Response(status_code=200)
    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error creating report: {str(e)}")