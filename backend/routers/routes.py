from fastapi import APIRouter, HTTPException
from pydantic import BaseModel
from ..models.routes import create_waypoint, get_max_route_id
# from ..models.routes import get_route_by_id

router = APIRouter(prefix="/routes", tags=["routes"])


class LocationData(BaseModel):
    latitude: float
    longitude: float
    altitude: float = None
    timestamp: str

class StartTrackingRequest(BaseModel):
    user_id: int

class StartTrackingResponse(BaseModel):
    next_route_id: int

class StopTrackingRequest(BaseModel):
    user_id: int

class StopTrackingResponse(BaseModel):
    message: str


@router.post("/start_tracking", response_model=StartTrackingResponse)
def start_tracking(request: StartTrackingRequest):
    last_id = get_max_route_id()
    next_id = (last_id or 0) + 1
    return StartTrackingResponse(next_route_id=next_id)

@router.post("/stop_tracking", response_model=StopTrackingResponse)
def stop_tracking(request: StopTrackingRequest):
    return StopTrackingResponse(message=f"Tracking stopped for user {request.user_id}")

@router.post("/{route_id}/location")
def post_location_data(route_id: int, location: LocationData):
    waypoint_id = create_waypoint({
        "route_id": route_id,
        "latitude": location.latitude,
        "longitude": location.longitude,
        "altitude": location.altitude,
        "timestamp": location.timestamp
    })
    return {"waypoint_id": waypoint_id}


