from fastapi import APIRouter, HTTPException
from pydantic import BaseModel
from ..models.accounts import create_account, get_account_by_email
from ..models.routes import create_waypoint
from ..db import pwd_context
from ..settings import SECRET_KEY, ALGORITHM
from jose import jwt
from datetime import datetime, timedelta

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

class RegisterResponse(BaseModel):
    accessMessage: str

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
    return LoginResponse(accessToken=access_token)

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
    return RegisterResponse(accessMessage=f"Account created with id {account_id}")

@router.post("/routes/{route_id}/location")
def post_location_data(route_id: int, location: LocationData):
    waypoint_id = create_waypoint({
        "route_id": route_id,
        "latitude": location.latitude,
        "longitude": location.longitude,
        "altitude": location.altitude,
        "timestamp": location.timestamp
    })
    return {"waypoint_id": waypoint_id}
