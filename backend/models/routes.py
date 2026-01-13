from backend.db import get_pg_conn

def get_max_route_id():
    conn = get_pg_conn()
    cur = conn.cursor()
    cur.execute("SELECT MAX(route_id) FROM routes;")
    row = cur.fetchone()
    cur.close()
    conn.close()
    if row is None:
        return None
    if isinstance(row, dict):
        return row.get('max') or row.get('MAX(route_id)') or row.get('route_id')
    if isinstance(row, (list, tuple)):
        return row[0]
    return row

def create_route(data):
    conn = get_pg_conn()
    cur = conn.cursor()
    cur.execute("""
        INSERT INTO routes (account_id, route_name, status, start_time, end_time, distance)
        VALUES (%s, %s, %s, %s, %s, %s)
        RETURNING route_id;
    """, (
        data['account_id'], data.get('route_name'), data.get('status', 'started'),
        data['start_time'], data.get('end_time'), data.get('distance')
    ))
    route_id = cur.fetchone()['route_id']
    conn.commit()
    cur.close()
    conn.close()
    return route_id

def get_route_by_id(route_id):
    conn = get_pg_conn()
    cur = conn.cursor()
    cur.execute("SELECT * FROM routes WHERE route_id = %s;", (route_id,))
    result = cur.fetchone()
    cur.close()
    conn.close()
    return result

def update_route(route_id, data):
    conn = get_pg_conn()
    cur = conn.cursor()
    fields = []
    values = []
    for k, v in data.items():
        fields.append(f"{k} = %s")
        values.append(v)
    values.append(route_id)
    cur.execute(f"""
        UPDATE routes SET {', '.join(fields)} WHERE route_id = %s RETURNING route_id;
    """, tuple(values))
    updated = cur.fetchone()
    conn.commit()
    cur.close()
    conn.close()
    return updated

def delete_route(route_id):
    conn = get_pg_conn()
    cur = conn.cursor()
    cur.execute("DELETE FROM routes WHERE route_id = %s RETURNING route_id;", (route_id,))
    deleted = cur.fetchone()
    conn.commit()
    cur.close()
    conn.close()
    return deleted

def create_waypoint(data):
    conn = get_pg_conn()
    cur = conn.cursor()
    fields = ["route_id", "latitude", "longitude", "altitude", "timestamp"]
    values = [data['route_id'], data['latitude'], data['longitude'], data.get('altitude'), data['timestamp']]

    for field in ["pm25", "pm10", "aqi"]:
        if field in data:
            fields.append(field)
            values.append(data[field])
    sql = f"INSERT INTO route_waypoints ({', '.join(fields)}) VALUES ({', '.join(['%s']*len(fields))}) RETURNING waypoint_id;"
    cur.execute(sql, tuple(values))
    waypoint_id = cur.fetchone()['waypoint_id']
    conn.commit()
    cur.close()
    conn.close()
    return waypoint_id

def get_waypoint_by_id(waypoint_id):
    conn = get_pg_conn()
    cur = conn.cursor()
    cur.execute("SELECT * FROM route_waypoints WHERE waypoint_id = %s;", (waypoint_id,))
    result = cur.fetchone()
    cur.close()
    conn.close()
    return result

def update_waypoint(waypoint_id, data): # to działa
    conn = get_pg_conn()
    cur = conn.cursor()
    fields = []
    values = []
    for k, v in data.items():
        fields.append(f"{k} = %s")
        values.append(v)
    values.append(waypoint_id)
    cur.execute(f"""
        UPDATE route_waypoints SET {', '.join(fields)} WHERE waypoint_id = %s RETURNING waypoint_id;
    """, tuple(values))
    updated = cur.fetchone()
    conn.commit()
    cur.close()
    conn.close()
    return updated

def delete_waypoint(waypoint_id):
    conn = get_pg_conn()
    cur = conn.cursor()
    cur.execute("DELETE FROM route_waypoints WHERE waypoint_id = %s RETURNING waypoint_id;", (waypoint_id,))
    deleted = cur.fetchone()
    conn.commit()
    cur.close()
    conn.close()
    return deleted

def get_waypoints_for_route(route_id):
    conn = get_pg_conn()
    cur = conn.cursor()
    cur.execute("""
        SELECT waypoint_id, latitude, longitude, altitude, timestamp, pm25, pm10, aqi
        FROM route_waypoints
        WHERE route_id = %s
        ORDER BY timestamp ASC;
    """, (route_id,))
    rows = cur.fetchall()
    cur.close()
    conn.close()
    waypoints = []
    for row in rows:
        if isinstance(row, dict):
            waypoints.append({
                "waypoint_id": row.get("waypoint_id"),
                "latitude": row.get("latitude"),
                "longitude": row.get("longitude"),
                "altitude": row.get("altitude"),
                "timestamp": row.get("timestamp"),
                "pm25": row.get("pm25"),
                "pm10": row.get("pm10"),
                "aqi": row.get("aqi")
            })
        else:
            waypoints.append({
                "waypoint_id": row[0],
                "latitude": row[1],
                "longitude": row[2],
                "altitude": row[3],
                "timestamp": row[4],
                "pm25": row[5],
                "pm10": row[6],
                "aqi": row[7]
            })
    return waypoints

def get_latest_waypoint_for_route(route_id):
    conn = get_pg_conn()
    cur = conn.cursor()
    cur.execute("""
        SELECT waypoint_id, latitude, longitude, altitude, timestamp, pm25, pm10, aqi
        FROM route_waypoints
        WHERE route_id = %s
        ORDER BY timestamp DESC
        LIMIT 1;
    """, (route_id,))
    row = cur.fetchone()
    cur.close()
    conn.close()
    if row:
        if isinstance(row, dict):
            return {
                "waypoint_id": row.get("waypoint_id"),
                "latitude": row.get("latitude"),
                "longitude": row.get("longitude"),
                "altitude": row.get("altitude"),
                "timestamp": row.get("timestamp"),
                "pm25": row.get("pm25"),
                "pm10": row.get("pm10"),
                "aqi": row.get("aqi")
            }
        else:
            return {
                "waypoint_id": row[0],
                "latitude": row[1],
                "longitude": row[2],
                "altitude": row[3],
                "timestamp": row[4],
                "pm25": row[5],
                "pm10": row[6],
                "aqi": row[7]
            }
    return None