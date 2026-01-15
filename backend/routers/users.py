from fastapi import APIRouter, HTTPException
from pydantic import BaseModel
from ..models.accounts import create_account, get_account_by_email, create_account_settings
from ..db import pwd_context
from ..settings import SECRET_KEY, ALGORITHM, DATABASE_URL
from jose import jwt
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
    accessMessage: str
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
    print("Password length:", len(request.password), "Password bytes:", len(request.password.encode('utf-8')))
    password_bytes = request.password.encode('utf-8')[:72]
    safe_password = password_bytes.decode('utf-8', 'ignore')
    password_hash = pwd_context.hash(safe_password)
    account_id = create_account({
        "username": request.username,
        "email": request.email,
        "password_hash": password_hash,
        "account_type": "user",
        "is_active": True
    })

    create_account_settings({"account_id": account_id})

    return RegisterResponse(accessMessage=f"Account created with id {account_id}", userId=account_id)

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
