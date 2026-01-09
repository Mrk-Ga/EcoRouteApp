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
