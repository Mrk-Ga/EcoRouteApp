from fastapi import APIRouter, HTTPException
from pydantic import BaseModel
from typing import Optional
from datetime import datetime
from ..db import get_pg_conn

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
    conn = get_pg_conn()
    cur = conn.cursor()
    cur.execute("""
        SELECT marketing_communications, air_quality_data_collection, notifications_enabled, location_tracking_enabled
        FROM gdpr_settings WHERE user_id = %s;
    """, (user_id,))
    row = cur.fetchone()
    cur.close()
    conn.close()
    if not row:
        raise HTTPException(status_code=404, detail="GDPR settings not found for user")
    return GDPRSettingsResponse(
        marketing_communications=row[0],
        air_quality_data_collection=row[1],
        location_tracking_enabled=row[2]
    )

@router.post("/update")
def update_gdpr_settings(request: GDPRSettingsUpdateRequest):
    conn = get_pg_conn()
    cur = conn.cursor()
    fields = []
    values = []
    if request.marketing_communications is not None:
        fields.append("marketing_communications = %s")
        values.append(request.marketing_communications)
        if request.marketing_consent_date:
            fields.append("marketing_consent_date = %s")
            values.append(request.marketing_consent_date)
    if request.air_quality_data_collection is not None:
        fields.append("air_quality_data_collection = %s")
        values.append(request.air_quality_data_collection)
        if request.air_quality_data_collection_consent_date:
            fields.append("air_quality_data_collection_consent_date = %s")
            values.append(request.air_quality_data_collection_consent_date)
    if request.location_tracking_enabled is not None:
        fields.append("location_tracking_enabled = %s")
        values.append(request.location_tracking_enabled)
        if request.location_consent_date:
            fields.append("location_consent_date = %s")
            values.append(request.location_consent_date)
    if not fields:
        raise HTTPException(status_code=400, detail="No fields to update")
    values.append(request.user_id)
    cur.execute(f"""
        UPDATE gdpr_settings SET {', '.join(fields)} WHERE user_id = %s RETURNING user_id;
    """, tuple(values))
    updated = cur.fetchone()
    conn.commit()
    cur.close()
    conn.close()
    if not updated:
        raise HTTPException(status_code=404, detail="GDPR settings not found for user")
    return {"message": "GDPR settings updated", "user_id": updated[0]}
