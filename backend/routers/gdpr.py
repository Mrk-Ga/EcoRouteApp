from fastapi import APIRouter, HTTPException
from pydantic import BaseModel
from typing import Optional
from datetime import datetime
from ..models.accounts import get_account_settings, update_account_settings

router = APIRouter(prefix="/settings/gdpr", tags=["gdpr"])

class GDPRSettingsResponse(BaseModel):
    marketingCommunications: bool
    airQualityDataCollection: bool
    locationTrackingEnabled: bool

class GDPRSettingsUpdateRequest(BaseModel):
    userId: int
    marketingCommunications: Optional[bool] = None
    airQualityDataCollection: Optional[bool] = None
    locationTrackingEnabled: Optional[bool] = None
    timestamp: Optional[int] = None

@router.get("/{user_id}", response_model=GDPRSettingsResponse)
def get_gdpr_settings(user_id: int):
    row = get_account_settings(user_id)
    if not row:
        raise HTTPException(status_code=404, detail="GDPR settings not found for user")
    return GDPRSettingsResponse(
        marketingCommunications=row['marketing_communications'],
        airQualityDataCollection=row['air_quality_data_collection'],
        locationTrackingEnabled=row['location_tracking_enabled']
    )

@router.post("/update")
def update_gdpr_settings(request: GDPRSettingsUpdateRequest):
    data = {}
    ts = request.timestamp
    ts_dt = None
    if ts is not None and ts > 0:
        if ts > 1e12:
            ts = ts // 1000
        try:
            ts_dt = datetime.fromtimestamp(ts)
        except Exception:
            ts_dt = None
    if request.marketingCommunications is not None:
        data['marketing_communications'] = request.marketingCommunications
        if request.marketingCommunications is False:
            if ts_dt:
                data['marketing_consent_date'] = ts_dt
    if request.airQualityDataCollection is not None:
        data['air_quality_data_collection'] = request.airQualityDataCollection
        if request.airQualityDataCollection is False:
            if ts_dt:
                data['air_quality_data_collection_consent_date'] = ts_dt
    if request.locationTrackingEnabled is not None:
        data['location_tracking_enabled'] = request.locationTrackingEnabled
        if request.locationTrackingEnabled is False:
            if ts_dt:
                data['location_consent_date'] = ts_dt
    if ts is not None and ts > 0:
        ts_dt = datetime.fromtimestamp(ts)
    else:
        ts_dt = None
    if not data:
        raise HTTPException(status_code=400, detail="No fields to update")
    updated = update_account_settings(request.userId, data)
    if not updated:
        raise HTTPException(status_code=404, detail="GDPR settings not found for user")
        return {"message": "GDPR settings updated", "userId": updated['settings_id']}
