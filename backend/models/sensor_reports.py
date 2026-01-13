import psycopg2
from psycopg2.extras import RealDictCursor
from ..settings import DATABASE_URL
import re
import math
from backend.db import get_pg_conn
from backend.algorithms.arithmetic_functions import calculate_distance


def get_nearest_stations(latitude, longitude, limit=5):
    conn = get_db_connection()
    cur = conn.cursor()

    cur.execute("""
        SELECT station_id, station_name, latitude, longitude
        FROM public.pollution_measurement_stations
        WHERE is_active = true
    """)
    stations = cur.fetchall()

    stations_with_distance = []
    for station in stations:
        distance = haversine(
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
    conn = get_db_connection()
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