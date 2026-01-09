from datetime import datetime, timedelta, UTC
from backend.db import get_pg_conn

def create_sensor(data):
    conn = get_pg_conn()
    cur = conn.cursor()
    cur.execute("""
        INSERT INTO sensors (station_id, pollutant_type, unit, is_active, last_reading_timestamp)
        VALUES (%s, %s, %s, %s, %s)
        RETURNING sensor_id;
    """, (
        data['station_id'], data['pollutant_type'], data['unit'], data.get('is_active', True), data.get('last_reading_timestamp')
    ))
    sensor_id = cur.fetchone()['sensor_id']
    conn.commit()
    cur.close()
    conn.close()
    return sensor_id

def get_sensor_by_id(sensor_id):
    conn = get_pg_conn()
    cur = conn.cursor()
    cur.execute("SELECT * FROM sensors WHERE sensor_id = %s;", (sensor_id,))
    result = cur.fetchone()
    cur.close()
    conn.close()
    return result

def update_sensor(sensor_id, data):
    conn = get_pg_conn()
    cur = conn.cursor()
    fields = []
    values = []
    for k, v in data.items():
        fields.append(f"{k} = %s")
        values.append(v)
    values.append(sensor_id)
    cur.execute(f"""
        UPDATE sensors SET {', '.join(fields)} WHERE sensor_id = %s RETURNING sensor_id;
    """, tuple(values))
    updated = cur.fetchone()
    conn.commit()
    cur.close()
    conn.close()
    return updated

def delete_sensor(sensor_id):
    conn = get_pg_conn()
    cur = conn.cursor()
    cur.execute("DELETE FROM sensors WHERE sensor_id = %s RETURNING sensor_id;", (sensor_id,))
    deleted = cur.fetchone()
    conn.commit()
    cur.close()
    conn.close()
    return deleted

def create_sensor_reading(data):
    conn = get_pg_conn()
    cur = conn.cursor()
    cur.execute("""
        INSERT INTO sensor_readings (sensor_id, value, reading_timestamp)
        VALUES (%s, %s, %s)
        RETURNING reading_id;
    """, (
        data['sensor_id'], data['value'], data['reading_timestamp']
    ))
    reading_id = cur.fetchone()['reading_id']
    conn.commit()
    cur.close()
    conn.close()
    return reading_id

def get_sensor_reading_by_id(reading_id):
    conn = get_pg_conn()
    cur = conn.cursor()
    cur.execute("SELECT * FROM sensor_readings WHERE reading_id = %s;", (reading_id,))
    result = cur.fetchone()
    cur.close()
    conn.close()
    return result

def update_sensor_reading(reading_id, data):
    conn = get_pg_conn()
    cur = conn.cursor()
    fields = []
    values = []
    for k, v in data.items():
        fields.append(f"{k} = %s")
        values.append(v)
    values.append(reading_id)
    cur.execute(f"""
        UPDATE sensor_readings SET {', '.join(fields)} WHERE reading_id = %s RETURNING reading_id;
    """, tuple(values))
    updated = cur.fetchone()
    conn.commit()
    cur.close()
    conn.close()
    return updated

def delete_sensor_reading(reading_id):
    conn = get_pg_conn()
    cur = conn.cursor()
    cur.execute("DELETE FROM sensor_readings WHERE reading_id = %s RETURNING reading_id;", (reading_id,))
    deleted = cur.fetchone()
    conn.commit()
    cur.close()
    conn.close()
    return deleted

# def get_latest_readings_for_station(station_id, time_limit_minutes=15, time_check_enabled=False): # to działa

def get_latest_readings_for_station(station_id): # to działa

    conn = get_pg_conn()
    cur = conn.cursor()
    cur.execute("""
        SELECT sensor_id, pollutant_type FROM sensors
        WHERE station_id = %s AND is_active = TRUE;
    """, (int(station_id),))
    sensors = cur.fetchall()
    print(f"DEBUG sensors for station {station_id}: {sensors}")
    result = {"PM25": None, "PM10": None, "AQI": None, "time": None}
    now = datetime.now(UTC)
    for sensor in sensors:
        if isinstance(sensor, dict):
            sensor_id = sensor["sensor_id"]
            pollutant_type = sensor["pollutant_type"]
        else:
            sensor_id, pollutant_type = sensor
        if pollutant_type not in ("PM25", "PM10", "AQI"):
            continue
        cur.execute("""
            SELECT value, reading_timestamp FROM sensor_readings
            WHERE sensor_id = %s
            ORDER BY reading_timestamp DESC
            LIMIT 1;
        """, (sensor_id,))
        row = cur.fetchone()
        if row:
            if isinstance(row, dict):
                value = row["value"]
                reading_timestamp = row["reading_timestamp"]
            else:
                value, reading_timestamp = row
            result[pollutant_type] = float(value) if pollutant_type != "AQI" else int(value)
            result["time"] = reading_timestamp.isoformat()
    cur.close()
    conn.close()
    return result