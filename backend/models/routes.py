import psycopg2
from psycopg2.extras import RealDictCursor
from ..settings import DATABASE_URL
import re

def get_pg_conn():
    match = re.match(r"postgresql\+psycopg2://(.*?):(.*?)@(.*?):(\d+)/(.*)", DATABASE_URL)
    if not match:
        raise ValueError("Invalid DATABASE_URL format")
    user, password, host, port, dbname = match.groups()
    return psycopg2.connect(
        dbname=dbname,
        user=user,
        password=password,
        host=host,
        port=port,
        cursor_factory=RealDictCursor
    )

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
    cur.execute("""
        INSERT INTO route_waypoints (route_id, latitude, longitude, altitude, timestamp)
        VALUES (%s, %s, %s, %s, %s)
        RETURNING waypoint_id;
    """, (
        data['route_id'], data['latitude'], data['longitude'], data.get('altitude'), data['timestamp']
    ))
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

def update_waypoint(waypoint_id, data):
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
