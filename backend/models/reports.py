from backend.db import get_pg_conn

def create_sensor_report(data):
    conn = get_pg_conn()
    cur = conn.cursor()
    cur.execute("""
        INSERT INTO sensor_reports (sensor_id, account_id, report_type, description, reported_value, report_timestamp, status, admin_notes, resolved_at)
        VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s)
        RETURNING report_id;
    """, (
        data['sensor_id'], data['account_id'], data['report_type'], data.get('description'),
        data.get('reported_value'), data.get('report_timestamp'), data.get('status', 'pending'),
        data.get('admin_notes'), data.get('resolved_at')
    ))
    report_id = cur.fetchone()['report_id']
    conn.commit()
    cur.close()
    conn.close()
    return report_id

def get_sensor_report_by_id(report_id):
    conn = get_pg_conn()
    cur = conn.cursor()
    cur.execute("SELECT * FROM sensor_reports WHERE report_id = %s;", (report_id,))
    result = cur.fetchone()
    cur.close()
    conn.close()
    return result

def update_sensor_report(report_id, data):
    conn = get_pg_conn()
    cur = conn.cursor()
    fields = []
    values = []
    for k, v in data.items():
        fields.append(f"{k} = %s")
        values.append(v)
    values.append(report_id)
    cur.execute(f"""
        UPDATE sensor_reports SET {', '.join(fields)} WHERE report_id = %s RETURNING report_id;
    """, tuple(values))
    updated = cur.fetchone()
    conn.commit()
    cur.close()
    conn.close()
    return updated

def delete_sensor_report(report_id):
    conn = get_pg_conn()
    cur = conn.cursor()
    cur.execute("DELETE FROM sensor_reports WHERE report_id = %s RETURNING report_id;", (report_id,))
    deleted = cur.fetchone()
    conn.commit()
    cur.close()
    conn.close()
    return deleted

def create_route_report(data):
    conn = get_pg_conn()
    cur = conn.cursor()
    cur.execute("""
        INSERT INTO route_reports (route_id, total_duration_seconds, avg_speed, created_at)
        VALUES (%s, %s, %s, %s)
        RETURNING report_id;
    """, (
        data['route_id'], data['total_duration_seconds'], data['avg_speed'], data['created_at']
    ))
    report_id = cur.fetchone()['report_id']
    conn.commit()
    cur.close()
    conn.close()
    return report_id

def get_route_report_by_id(report_id):
    conn = get_pg_conn()
    cur = conn.cursor()
    cur.execute("SELECT * FROM route_reports WHERE report_id = %s;", (report_id,))
    result = cur.fetchone()
    cur.close()
    conn.close()
    return result

def update_route_report(report_id, data):
    conn = get_pg_conn()
    cur = conn.cursor()
    fields = []
    values = []
    for k, v in data.items():
        fields.append(f"{k} = %s")
        values.append(v)
    values.append(report_id)
    cur.execute(f"""
        UPDATE route_reports SET {', '.join(fields)} WHERE report_id = %s RETURNING report_id;
    """, tuple(values))
    updated = cur.fetchone()
    conn.commit()
    cur.close()
    conn.close()
    return updated

def delete_route_report(report_id):
    conn = get_pg_conn()
    cur = conn.cursor()
    cur.execute("DELETE FROM route_reports WHERE report_id = %s RETURNING report_id;", (report_id,))
    deleted = cur.fetchone()
    conn.commit()
    cur.close()
    conn.close()
    return deleted

def create_route_pollution_summary(data):
    conn = get_pg_conn()
    cur = conn.cursor()
    cur.execute("""
        INSERT INTO route_pollution_summary (report_id, pollutant_type, avg_value, peak_value)
        VALUES (%s, %s, %s, %s)
        RETURNING summary_id;
    """, (
        data['report_id'], data['pollutant_type'], data['avg_value'], data['peak_value']
    ))
    summary_id = cur.fetchone()['summary_id']
    conn.commit()
    cur.close()
    conn.close()
    return summary_id

def get_route_pollution_summary_by_id(summary_id):
    conn = get_pg_conn()
    cur = conn.cursor()
    cur.execute("SELECT * FROM route_pollution_summary WHERE summary_id = %s;", (summary_id,))
    result = cur.fetchone()
    cur.close()
    conn.close()
    return result

def update_route_pollution_summary(summary_id, data):
    conn = get_pg_conn()
    cur = conn.cursor()
    fields = []
    values = []
    for k, v in data.items():
        fields.append(f"{k} = %s")
        values.append(v)
    values.append(summary_id)
    cur.execute(f"""
        UPDATE route_pollution_summary SET {', '.join(fields)} WHERE summary_id = %s RETURNING summary_id;
    """, tuple(values))
    updated = cur.fetchone()
    conn.commit()
    cur.close()
    conn.close()
    return updated

def delete_route_pollution_summary(summary_id):
    conn = get_pg_conn()
    cur = conn.cursor()
    cur.execute("DELETE FROM route_pollution_summary WHERE summary_id = %s RETURNING summary_id;", (summary_id,))
    deleted = cur.fetchone()
    conn.commit()
    cur.close()
    conn.close()
    return deleted

def generate_route_pollution_summary(report_id, route_id):
    from backend.db import get_pg_conn
    conn = get_pg_conn()
    cur = conn.cursor()
    cur.execute("SELECT pm25 FROM route_waypoints WHERE route_id = %s AND pm25 IS NOT NULL;", (route_id,))
    pm25_vals = [row[0] if not isinstance(row, dict) else row.get('pm25') for row in cur.fetchall()]
    cur.execute("SELECT pm10 FROM route_waypoints WHERE route_id = %s AND pm10 IS NOT NULL;", (route_id,))
    pm10_vals = [row[0] if not isinstance(row, dict) else row.get('pm10') for row in cur.fetchall()]
    cur.execute("SELECT aqi FROM route_waypoints WHERE route_id = %s AND aqi IS NOT NULL;", (route_id,))
    aqi_vals = [row[0] if not isinstance(row, dict) else row.get('aqi') for row in cur.fetchall()]
    conn.close()
    summary_data = []
    for pollutant, values in [('PM25', pm25_vals), ('PM10', pm10_vals), ('AQI', aqi_vals)]:
        if values:
            avg_value = round(sum(values)/len(values), 2)
            peak_value = round(max(values), 2)
            summary_data.append({
                'report_id': report_id,
                'pollutant_type': pollutant,
                'avg_value': avg_value,
                'peak_value': peak_value
            })
    for data in summary_data:
        create_route_pollution_summary(data)
    return summary_data