from backend.db import get_pg_conn

def create_account(data):
    conn = get_pg_conn()
    cur = conn.cursor()
    cur.execute("""
        INSERT INTO accounts (username, email, password_hash, account_type, first_name, last_name, phone_number, is_active)
        VALUES (%s, %s, %s, %s, %s, %s, %s, %s)
        RETURNING account_id;
    """, (
        data['username'], data['email'], data['password_hash'], data.get('account_type', 'user'),
        data.get('first_name'), data.get('last_name'), data.get('phone_number'), data.get('is_active', True)
    ))
    account_id = cur.fetchone()['account_id']
    conn.commit()
    cur.close()
    conn.close()
    return account_id

def get_account_by_id(account_id):
    conn = get_pg_conn()
    cur = conn.cursor()
    cur.execute("SELECT * FROM accounts WHERE account_id = %s;", (account_id,))
    result = cur.fetchone()
    cur.close()
    conn.close()
    return result

def get_account_by_email(email):
    conn = get_pg_conn()
    cur = conn.cursor()
    cur.execute("SELECT * FROM accounts WHERE email = %s;", (email,))
    result = cur.fetchone()
    cur.close()
    conn.close()
    return result

def update_account(account_id, data):
    conn = get_pg_conn()
    cur = conn.cursor()
    fields = []
    values = []
    for k, v in data.items():
        fields.append(f"{k} = %s")
        values.append(v)
    values.append(account_id)
    cur.execute(f"""
        UPDATE accounts SET {', '.join(fields)} WHERE account_id = %s RETURNING account_id;
    """, tuple(values))
    updated = cur.fetchone()
    conn.commit()
    cur.close()
    conn.close()
    return updated

def delete_account(account_id):
    conn = get_pg_conn()
    cur = conn.cursor()
    cur.execute("DELETE FROM accounts WHERE account_id = %s RETURNING account_id;", (account_id,))
    deleted = cur.fetchone()
    conn.commit()
    cur.close()
    conn.close()
    return deleted

def create_account_settings(data):
    conn = get_pg_conn()
    cur = conn.cursor()
    cur.execute("""
        INSERT INTO account_settings (account_id, is_special, marketing_communications, air_quality_data_collection, notifications_enabled, location_tracking_enabled)
        VALUES (%s, %s, %s, %s, %s, %s)
        RETURNING settings_id;
    """, (
        data['account_id'], data.get('is_special', False), data.get('marketing_communications', True),
        data.get('air_quality_data_collection', True), data.get('notifications_enabled', True), data.get('location_tracking_enabled', True)
    ))
    settings_id = cur.fetchone()['settings_id']
    conn.commit()
    cur.close()
    conn.close()
    return settings_id

def get_account_settings(account_id):
    conn = get_pg_conn()
    cur = conn.cursor()
    cur.execute("SELECT * FROM account_settings WHERE account_id = %s;", (account_id,))
    result = cur.fetchone()
    cur.close()
    conn.close()
    return result

def update_account_settings(account_id, data):
    conn = get_pg_conn()
    cur = conn.cursor()
    fields = []
    values = []
    for k, v in data.items():
        fields.append(f"{k} = %s")
        values.append(v)
    values.append(account_id)
    cur.execute(f"""
        UPDATE account_settings SET {', '.join(fields)} WHERE account_id = %s RETURNING settings_id;
    """, tuple(values))
    updated = cur.fetchone()
    conn.commit()
    cur.close()
    conn.close()
    return updated

def delete_account_settings(account_id):
    conn = get_pg_conn()
    cur = conn.cursor()
    cur.execute("DELETE FROM account_settings WHERE account_id = %s RETURNING settings_id;", (account_id,))
    deleted = cur.fetchone()
    conn.commit()
    cur.close()
    conn.close()
    return deleted
