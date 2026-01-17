from fastapi import APIRouter, HTTPException, Response
from pydantic import BaseModel
from typing import List, Optional
from backend.db import get_pg_conn

router = APIRouter(tags=["history"])

class RouteHistory(BaseModel):
    id: str
    date: str
    status: str
    time: str
    duration: str
    points: int
    avgPm25: str
    maxPm25: str

class RouteDetails(BaseModel):
    id: str
    date: str
    time: str
    status: str
    duration: str
    dataPoints: int
    avgPm25: float
    maxPm25: float
    avgPm10: float
    maxPm10: float
    exposureLevel: str
    exposureAssessment: str
    startTime: str
    endTime: str
    reportGeneratedTime: str
    healthRecommendations: List[str]

def format_duration(seconds):
    if seconds is None:
        return "0m"
    minutes = seconds // 60
    return f"{minutes}m"

def get_exposure_level(avg_pm25):
    if avg_pm25 is None:
        return "Unknown"
    if avg_pm25 <= 12:
        return "Low"
    elif avg_pm25 <= 35:
        return "Moderate"
    elif avg_pm25 <= 55:
        return "High"
    else:
        return "Very High"

def get_exposure_assessment(level):
    assessments = {
        "Low": "Your exposure was low. Great job choosing a clean route!",
        "Moderate": "Your exposure was moderate. Consider monitoring air quality on future routes.",
        "High": "Your exposure was high. Consider choosing alternative routes with better air quality.",
        "Very High": "Your exposure was very high. Try to avoid this route when air quality is poor.",
        "Unknown": "Unable to assess exposure level due to missing data."
    }
    return assessments.get(level, assessments["Unknown"])

def get_health_recommendations(level):
    if level == "Low":
        return ["Keep up the good work!", "Continue monitoring air quality"]
    elif level == "Moderate":
        return ["Consider using a mask on high pollution days", "Monitor air quality forecasts"]
    elif level in ["High", "Very High"]:
        return ["Consider using a mask (N95 or better)", "Monitor air quality forecasts", "Choose greener routes"]
    return ["Monitor air quality regularly"]

@router.get("/history/routes", response_model=List[RouteHistory])
def get_history():
    conn = get_pg_conn()
    cur = conn.cursor()

    cur.execute("""
        SELECT r.route_id, r.start_time, r.status,
               rr.total_duration_seconds, rr.waypoint_count, rr.created_at
        FROM public.routes r
        LEFT JOIN public.route_reports rr ON r.route_id = rr.route_id
        WHERE r.status = 'completed'
        ORDER BY r.start_time DESC
    """)
    routes = cur.fetchall()

    result = []
    for route in routes:
        route_id = route['route_id']

        cur.execute("""
            SELECT pollutant_type, avg_value, peak_value
            FROM public.route_pollution_summary rps
            JOIN public.route_reports rr ON rps.report_id = rr.report_id
            WHERE rr.route_id = %s AND pollutant_type = 'PM25'
        """, (route_id,))
        pm25_data = cur.fetchone()

        start_time = route['start_time']
        date_str = start_time.strftime("%d.%m.%Y") if start_time else ""
        time_str = start_time.strftime("%H:%M") if start_time else ""

        result.append(RouteHistory(
            id=str(route_id),
            date=date_str,
            status=route['status'] or "completed",
            time=time_str,
            duration=format_duration(route['total_duration_seconds']),
            points=route['waypoint_count'] or 0,
            avgPm25=str(round(float(pm25_data['avg_value']), 1)) if pm25_data and pm25_data['avg_value'] else "0",
            maxPm25=str(round(float(pm25_data['peak_value']), 1)) if pm25_data and pm25_data['peak_value'] else "0"
        ))

    cur.close()
    conn.close()
    return result

@router.get("/details/{route_id}", response_model=RouteDetails)
def get_route_details(route_id: str):
    conn = get_pg_conn()
    cur = conn.cursor()

    cur.execute("""
        SELECT r.route_id, r.start_time, r.end_time, r.status,
               rr.total_duration_seconds, rr.waypoint_count, rr.created_at
        FROM public.routes r
        LEFT JOIN public.route_reports rr ON r.route_id = rr.route_id
        WHERE r.route_id = %s
    """, (route_id,))
    route = cur.fetchone()

    if not route:
        cur.close()
        conn.close()
        raise HTTPException(status_code=404, detail="Route not found")

    cur.execute("""
        SELECT pollutant_type, avg_value, peak_value
        FROM public.route_pollution_summary rps
        JOIN public.route_reports rr ON rps.report_id = rr.report_id
        WHERE rr.route_id = %s
    """, (route_id,))
    pollution_data = cur.fetchall()

    cur.close()
    conn.close()

    pm25_avg, pm25_max = 0.0, 0.0
    pm10_avg, pm10_max = 0.0, 0.0

    for p in pollution_data:
        if p['pollutant_type'] == 'PM25':
            pm25_avg = float(p['avg_value']) if p['avg_value'] else 0.0
            pm25_max = float(p['peak_value']) if p['peak_value'] else 0.0
        elif p['pollutant_type'] == 'PM10':
            pm10_avg = float(p['avg_value']) if p['avg_value'] else 0.0
            pm10_max = float(p['peak_value']) if p['peak_value'] else 0.0

    start_time = route['start_time']
    end_time = route['end_time']
    created_at = route['created_at']

    date_str = start_time.strftime("%d.%m.%Y") if start_time else ""
    time_str = start_time.strftime("%H:%M") if start_time else ""
    start_time_str = start_time.strftime("%d.%m.%Y, %H:%M:%S") if start_time else ""
    end_time_str = end_time.strftime("%d.%m.%Y, %H:%M:%S") if end_time else ""
    report_time_str = created_at.strftime("%d.%m.%Y, %H:%M:%S") if created_at else end_time_str

    exposure_level = get_exposure_level(pm25_avg)

    return RouteDetails(
        id=str(route['route_id']),
        date=date_str,
        time=time_str,
        status=route['status'] or "completed",
        duration=format_duration(route['total_duration_seconds']),
        dataPoints=route['waypoint_count'] or 0,
        avgPm25=round(pm25_avg, 1),
        maxPm25=round(pm25_max, 1),
        avgPm10=round(pm10_avg, 1),
        maxPm10=round(pm10_max, 1),
        exposureLevel=exposure_level,
        exposureAssessment=get_exposure_assessment(exposure_level),
        startTime=start_time_str,
        endTime=end_time_str,
        reportGeneratedTime=report_time_str,
        healthRecommendations=get_health_recommendations(exposure_level)
    )

@router.delete("/details/{route_id}")
def delete_route(route_id: str):
    conn = get_pg_conn()
    cur = conn.cursor()

    cur.execute("SELECT route_id FROM public.routes WHERE route_id = %s", (route_id,))
    if not cur.fetchone():
        cur.close()
        conn.close()
        raise HTTPException(status_code=404, detail="Route not found")

    cur.execute("DELETE FROM public.routes WHERE route_id = %s", (route_id,))
    conn.commit()
    cur.close()
    conn.close()

    return Response(status_code=200)
