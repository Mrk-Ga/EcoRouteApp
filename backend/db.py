import psycopg2
from psycopg2.extras import RealDictCursor
from passlib.context import CryptContext
from .settings import DATABASE_URL
import re

pwd_context = CryptContext(schemes=["bcrypt"], deprecated="auto")

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

def get_db():
    conn = get_pg_conn()
    try:
        yield conn
    finally:
        conn.close()
