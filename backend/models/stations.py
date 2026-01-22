from backend.db import get_pg_conn

def get_all_stations():
    conn = get_pg_conn()
    cur = conn.cursor()
    cur.execute("""
    SELECT station_id, station_name, is_active, station_type, latitude, longitude, created_at
    FROM pollution_measurement_stations;
    """)
    rows = cur.fetchall()
    cur.close()
    conn.close()
    result = []
    for row in rows:
        if isinstance(row, dict):
            result.append({
                "station_id": row.get("station_id"),
                "lat": row.get("latitude"),
                "lon": row.get("longitude")
            })
        else:
             result.append({
                "id": str(row["station_id"]),
                "name": row["station_name"],
                "status": row["is_active"],
                "type": row["station_type"],
                "latitude": str(row["latitude"]),
                "longitude": str(row["longitude"]),
                "created": row["created_at"].date().isoformat() if row["created_at"] else None,
            })
    return result

def create_station(data):
    conn = get_pg_conn()
    cur = conn.cursor()
    cur.execute("""
        INSERT INTO pollution_measurement_stations (
            station_name, latitude, longitude, address_street, address_city, address_postal_code,
            address_country, station_type, description, is_active
        ) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
        RETURNING station_id;
    """, (
        data['station_name'], data['latitude'], data['longitude'], data.get('address_street'),
        data['address_city'], data.get('address_postal_code'), data.get('address_country', 'Poland'),
        data['station_type'], data.get('description'), data.get('is_active', True)
    ))

    station_id = cur.fetchone()['station_id']
    conn.commit()
    cur.close()
    conn.close()
    return station_id

def get_station_by_id(station_id):
    conn = get_pg_conn()
    cur = conn.cursor()
    cur.execute("SELECT * FROM pollution_measurement_stations WHERE station_id = %s;", (station_id,))
    result = cur.fetchone()
    cur.close()
    conn.close()
    return result

def update_station(station_id, data):
    conn = get_pg_conn()
    cur = conn.cursor()
    fields = []
    values = []
    for k, v in data.items():
        fields.append(f"{k} = %s")
        values.append(v)
    values.append(station_id)
    cur.execute(f"""
        UPDATE pollution_measurement_stations SET {', '.join(fields)} WHERE station_id = %s RETURNING station_id;
    """, tuple(values))
    updated = cur.fetchone()
    conn.commit()
    cur.close()
    conn.close()
    return updated

def delete_station(station_id):
    conn = get_pg_conn()
    cur = conn.cursor()
    cur.execute("DELETE FROM pollution_measurement_stations WHERE station_id = %s RETURNING station_id;", (station_id,))
    deleted = cur.fetchone()
    conn.commit()
    cur.close()
    conn.close()
    return deleted

def update_station_status(station_id: int, is_active: bool):
    conn = get_pg_conn()
    cur = conn.cursor()
    cur.execute(
        "UPDATE pollution_measurement_stations SET is_active = %s WHERE station_id = %s RETURNING station_id, is_active;",
        (is_active, station_id)
    )
    row = cur.fetchone()
    conn.commit()
    cur.close()
    conn.close()

    if not row:
        return None

    if isinstance(row, dict):
        return {"station_id": row.get("station_id"), "is_active": row.get("is_active")}
    return {"station_id": row[0], "is_active": row[1]}

from typing import Any, Dict, List, Optional
from ..db import get_pg_conn


def get_station_measurements(station_id: int, limit: int = 50) -> Optional[List[Dict[str, Any]]]:
    conn = get_pg_conn()
    cur = conn.cursor()

    cur.execute(
        "SELECT 1 FROM pollution_measurement_stations WHERE station_id = %s;",
        (station_id,)
    )
    exists = cur.fetchone()
    if not exists:
        cur.close()
        conn.close()
        return None

    cur.execute(
        """
        SELECT sr.sensor_id,
               sr.value,
               sr.reading_timestamp,
               s.pollutant_type,
               s.unit
        FROM sensor_readings sr
        JOIN sensors s ON s.sensor_id = sr.sensor_id
        WHERE s.station_id = %s
        ORDER BY sr.reading_timestamp DESC
        LIMIT %s;
        """,
        (station_id, limit)
    )
    rows = cur.fetchall()
    cur.close()
    conn.close()

    result: List[Dict[str, Any]] = []
    for sensor_id, value, ts, pollutant_type, unit in rows:
        pm25 = None
        pm10 = None
        aqi = None

        pt = (pollutant_type or "").upper()
        if pt in ("PM2.5", "PM25", "PM_25"):
            pm25 = float(value)
        elif pt in ("PM10", "PM_10"):
            pm10 = float(value)
        elif pt == "AQI":
            aqi = float(value)

        result.append({
            "sensorId": int(sensor_id),
            "date": ts.date().isoformat(),
            "time": ts.time().strftime("%H:%M"),
            "PM25": pm25,
            "PM10": pm10,
            "AQI": aqi,
            "unit": unit,
        })

    return result

