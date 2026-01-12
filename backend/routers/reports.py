from fastapi import APIRouter, HTTPException
from pydantic import BaseModel
from typing import List
from ..models.sensor_reports import get_nearest_stations, create_sensor_report

router = APIRouter(prefix="/report", tags=["reports"])

class LocationData(BaseModel):
    latitude: float
    longitude: float
    altitude: float = None
    timestamp: str = None

class Sensor(BaseModel):
    sensorId: int
    pollutionType: str
    lastReading: float
    unit: str

class StationReport(BaseModel):
    stationId: int
    name: str
    distance: float
    sensors: List[Sensor]

class ReportData(BaseModel):
    report: str

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
def post_sensor_report(sensor_id: int, report_data: ReportData):
    try:
        from ..models.sensors import get_sensor_by_id
        sensor = get_sensor_by_id(sensor_id)
        if not sensor:
            raise HTTPException(status_code=404, detail=f"Sensor with id {sensor_id} not found")

        report_id = create_sensor_report(sensor_id, account_id=1, description=report_data.report)
        return {"message": "Report created successfully", "report_id": report_id}
    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error creating report: {str(e)}")