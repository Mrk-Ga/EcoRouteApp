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
