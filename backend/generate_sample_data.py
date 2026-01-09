import random
from datetime import datetime, timedelta
from backend.db import get_pg_conn
conn = get_pg_conn()
cur = conn.cursor()

stations = []
for i in range(1, 101):
    station_name = f"Station_{i}"
    address_city = f"City_{i}"
    lat = round(random.uniform(49.0, 54.0), 8)
    lon = round(random.uniform(14.0, 23.0), 8)
    address_country = "Poland"
    station_type = "urban"
    is_active = True
    cur.execute(
        "INSERT INTO pollution_measurement_stations (station_name, latitude, longitude, address_city, address_country, station_type, is_active) VALUES (%s, %s, %s, %s, %s, %s, %s) RETURNING station_id;",
        (station_name, lat, lon, address_city, address_country, station_type, is_active)
    )
    station_id = cur.fetchone()[0]
    stations.append(station_id)

sensor_types = ["PM25", "PM10", "AQI"]
sensor_ids = []
for station_id in stations:
    for sensor_type in sensor_types:
        unit = "ug/m3" if sensor_type in ["PM25", "PM10"] else "index"
        is_active = True
        cur.execute(
            "INSERT INTO sensors (station_id, pollutant_type, unit, is_active) VALUES (%s, %s, %s, %s) RETURNING sensor_id;",
            (station_id, sensor_type, unit, is_active)
        )
        sensor_id = cur.fetchone()[0]
        sensor_ids.append((sensor_id, sensor_type))

now = datetime.now()
for sensor_id, sensor_type in sensor_ids:
    for k in range(1000):
        reading_timestamp = now - timedelta(minutes=k)
        if sensor_type == "PM25":
            value = round(random.uniform(5, 150), 2)
        elif sensor_type == "PM10":
            value = round(random.uniform(10, 200), 2)
        elif sensor_type == "AQI":
            value = random.randint(50, 300)
        cur.execute(
            "INSERT INTO sensor_readings (sensor_id, value, reading_timestamp) VALUES (%s, %s, %s);",
            (sensor_id, value, reading_timestamp)
        )

conn.commit()
cur.close()
conn.close()
print("Dane wygenerowane!")
