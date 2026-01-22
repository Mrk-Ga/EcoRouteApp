from fastapi import APIRouter, HTTPException
from pydantic import BaseModel
from ..models.accounts import create_account, get_account_by_email, create_account_settings
from ..db import pwd_context
from ..settings import SECRET_KEY, ALGORITHM, DATABASE_URL
from jose import jwt
from fastapi import HTTPException
from datetime import datetime, timedelta
import psycopg2
import re

router = APIRouter(prefix="/auth", tags=["auth"])

class LoginRequest(BaseModel):
    email: str
    password: str

class RegisterRequest(BaseModel):
    username: str
    email: str
    password: str


class LoginResponse(BaseModel):
    accessToken: str
    userId: int


class RegisterResponse(BaseModel):
    accessToken: str
    userId: int

class LocationData(BaseModel):
    latitude: float
    longitude: float
    altitude: float = None
    timestamp: str

def create_access_token(data: dict, expires_delta: int = 60):
    to_encode = data.copy()
    expire = datetime.utcnow() + timedelta(minutes=expires_delta)
    to_encode.update({"exp": expire})
    encoded_jwt = jwt.encode(to_encode, SECRET_KEY, algorithm=ALGORITHM)
    return encoded_jwt


@router.post("/login", response_model=LoginResponse)
def login(request: LoginRequest):
    user = get_account_by_email(request.email)
    if not user or not pwd_context.verify(request.password, user['password_hash']):
        raise HTTPException(status_code=401, detail="Invalid credentials")
    token_data = {"sub": str(user['account_id']), "email": user['email']}
    access_token = create_access_token(token_data)
    return LoginResponse(accessToken=access_token, userId=user['account_id'])


@router.post("/register", response_model=RegisterResponse)
def register(request: RegisterRequest):
    username = (request.username or "").strip()
    email = (request.email or "").strip().lower()
    password = request.password or ""

    if not username:
        raise HTTPException(status_code=422, detail="Username is required.")
    if not email:
        raise HTTPException(status_code=422, detail="Email is required.")
    if not password:
        raise HTTPException(status_code=422, detail="Password is required.")


    # unikalnosc maila
    existing = get_account_by_email(email)
    if existing:
        raise HTTPException(status_code=409, detail="Account with this email already exists.")

    # limit na haslo
    if len(password.encode("utf-8")) > 72:
        raise HTTPException(
            status_code=422,
            detail="Password is too long (bcrypt supports up to 72 bytes)."
        )

    password_hash = pwd_context.hash(password)

    account_id = create_account({
        "username": username,
        "email": email,
        "password_hash": password_hash,
        "account_type": "user",
        "is_active": True
    })

    create_account_settings({"account_id": account_id})

    access_token = create_access_token(data={"sub": str(account_id), "email": email})

    return RegisterResponse(userId=account_id, accessToken=access_token)

# class UserIdRequest(BaseModel):
#     email: str

# class UserIdResponse(BaseModel):
#     user_id: int

# @router.post("/user_id", response_model=UserIdResponse)
# def get_user_id(request: UserIdRequest):
#     user = get_account_by_email(request.email)
#     if not user:
#         raise HTTPException(status_code=404, detail="User not found")
#     return UserIdResponse(user_id=user['account_id'])


# To do przeżuczenia gdzie trzeba
