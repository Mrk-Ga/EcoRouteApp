from fastapi import APIRouter, HTTPException
from pydantic import BaseModel
from typing import Optional
from datetime import datetime
from ..models.accounts import get_account_settings, update_account_settings

router = APIRouter(prefix="/settings/gdpr", tags=["gdpr"])

class GDPRSettingsResponse(BaseModel):
    marketing_communications: bool
    air_quality_data_collection: bool
    location_tracking_enabled: bool

class GDPRSettingsUpdateRequest(BaseModel):
    user_id: int
    marketing_communications: Optional[bool] = None
    marketing_consent_date: Optional[datetime] = None
    air_quality_data_collection: Optional[bool] = None
    air_quality_data_collection_consent_date: Optional[datetime] = None
    location_tracking_enabled: Optional[bool] = None
    location_consent_date: Optional[datetime] = None

@router.get("/{user_id}", response_model=GDPRSettingsResponse)
def get_gdpr_settings(user_id: int):
    row = get_account_settings(user_id)
    if not row:
        raise HTTPException(status_code=404, detail="GDPR settings not found for user")
    return GDPRSettingsResponse(
        marketing_communications=row['marketing_communications'],
        air_quality_data_collection=row['air_quality_data_collection'],
        location_tracking_enabled=row['location_tracking_enabled']
    )

@router.post("/update")
def update_gdpr_settings(request: GDPRSettingsUpdateRequest):
    data = {}
    if request.marketing_communications is not None:
        data['marketing_communications'] = request.marketing_communications
        if request.marketing_consent_date:
            data['marketing_consent_date'] = request.marketing_consent_date
    if request.air_quality_data_collection is not None:
        data['air_quality_data_collection'] = request.air_quality_data_collection
        if request.air_quality_data_collection_consent_date:
            data['air_quality_data_collection_consent_date'] = request.air_quality_data_collection_consent_date
    if request.location_tracking_enabled is not None:
        data['location_tracking_enabled'] = request.location_tracking_enabled
        if request.location_consent_date:
            data['location_consent_date'] = request.location_consent_date
    if not data:
        raise HTTPException(status_code=400, detail="No fields to update")
    updated = update_account_settings(request.user_id, data)
    if not updated:
        raise HTTPException(status_code=404, detail="GDPR settings not found for user")
    return {"message": "GDPR settings updated", "user_id": updated['settings_id']}
