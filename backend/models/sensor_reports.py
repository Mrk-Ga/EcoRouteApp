import psycopg2
from psycopg2.extras import RealDictCursor
from ..settings import DATABASE_URL
import re
import math

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

def calculate_distance(lat1, lon1, lat2, lon2):
    """Haversine formula - odległość między punktami GPS w km"""
    R = 6371
    lat1_rad = math.radians(lat1)
    lat2_rad = math.radians(lat2)
    delta_lat = math.radians(lat2 - lat1)
    delta_lon = math.radians(lon2 - lon1)

    a = math.sin(delta_lat/2)**2 + math.cos(lat1_rad) * math.cos(lat2_rad) * math.sin(delta_lon/2)**2
    c = 2 * math.atan2(math.sqrt(a), math.sqrt(1-a))

    return R * c

def get_nearest_stations(latitude, longitude, limit=5):
    conn = get_pg_conn()
    cur = conn.cursor()

    cur.execute("""
        SELECT station_id, station_name, latitude, longitude
        FROM public.pollution_measurement_stations
        WHERE is_active = true
    """)
    stations = cur.fetchall()

    stations_with_distance = []
    for station in stations:
        distance = calculate_distance(
            latitude, longitude,
            float(station['latitude']), float(station['longitude'])
        )
        stations_with_distance.append({
            'station_id': station['station_id'],
            'station_name': station['station_name'],
            'distance': round(distance, 2)
        })

    stations_with_distance.sort(key=lambda x: x['distance'])
    nearest_stations = stations_with_distance[:limit]

    result = []
    for station in nearest_stations:
        station_id = station['station_id']

        cur.execute("""
            SELECT s.sensor_id, s.pollutant_type, s.unit, s.last_reading_timestamp
            FROM public.sensors s
            WHERE s.station_id = %s AND s.is_active = true
        """, (station_id,))
        sensors = cur.fetchall()

        sensors_data = []
        for sensor in sensors:
            sensor_id = sensor['sensor_id']

            cur.execute("""
                SELECT value, reading_timestamp
                FROM public.sensor_readings
                WHERE sensor_id = %s
                ORDER BY reading_timestamp DESC
                LIMIT 1
            """, (sensor_id,))
            last_reading = cur.fetchone()

            sensors_data.append({
                'sensorId': sensor['sensor_id'],
                'pollutionType': sensor['pollutant_type'],
                'lastReading': float(last_reading['value']) if last_reading else 0.0,
                'unit': sensor['unit']
            })

        result.append({
            'stationId': station['station_id'],
            'name': station['station_name'],
            'distance': station['distance'],
            'sensors': sensors_data
        })

    cur.close()
    conn.close()

    return result

def create_sensor_report(sensor_id, account_id, description):
    conn = get_pg_conn()
    cur = conn.cursor()

    cur.execute("""
        INSERT INTO public.sensor_reports
        (sensor_id, account_id, report_type, description, status, report_timestamp)
        VALUES (%s, %s, %s, %s, %s::public.enum_report_status, CURRENT_TIMESTAMP)
        RETURNING report_id;
    """, (sensor_id, account_id, 'user_report', description, 'pending'))

    report_id = cur.fetchone()['report_id']
    conn.commit()
    cur.close()
    conn.close()

    return report_id