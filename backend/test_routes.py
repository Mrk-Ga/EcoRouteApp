def test_route_pollution_aggregation():
    # Start tracking
    response = client.post("/routes/start_tracking", json={"user_id": USER_ID})
    assert response.status_code == 200
    route_id = response.json()["route_id"]

    # Dodaj kilka punktów lokalizacji
    locations = [
        {"latitude": 52.2297, "longitude": 21.0122, "altitude": 100.0, "timestamp": datetime.utcnow().isoformat()},
        {"latitude": 52.2300, "longitude": 21.0130, "altitude": 101.0, "timestamp": datetime.utcnow().isoformat()},
    ]
    for loc in locations:
        resp = client.post(f"/routes/{route_id}/location", json=loc)
        assert resp.status_code == 200

    # Wywołaj endpoint agregujący zanieczyszczenia
    response = client.get(f"/routes/{route_id}")
    assert response.status_code == 200
    data = response.json()
    # Sprawdź, czy są klucze i wartości (mogą być None jeśli nie ma stacji/odczytów)
    assert set(data.keys()) == {"PM25", "PM10", "AQI", "alert", "time"}
    # PM25, PM10, AQI mogą być None jeśli nie ma danych, ale klucze muszą być
    # alert zawsze string
    assert isinstance(data["alert"], str)
import pytest
from fastapi.testclient import TestClient
from backend.main import app
from datetime import datetime

client = TestClient(app)

USER_ID = 2


def test_start_and_stop_tracking():
    # Start tracking
    response = client.post("/routes/start_tracking", json={"user_id": USER_ID})
    assert response.status_code == 200
    data = response.json()
    assert "route_id" in data
    route_id = data["route_id"]
    assert isinstance(route_id, int)


    # Add multiple location data points
    locations = [
        {"latitude": 52.2297, "longitude": 21.0122, "altitude": 100.0, "timestamp": datetime.utcnow().isoformat()},
        {"latitude": 52.2300, "longitude": 21.0130, "altitude": 101.0, "timestamp": datetime.utcnow().isoformat()},
        {"latitude": 52.2310, "longitude": 21.0140, "altitude": 102.0, "timestamp": datetime.utcnow().isoformat()}
    ]
    for loc in locations:
        response = client.post(f"/routes/{route_id}/location", json=loc)
        assert response.status_code == 200
        data = response.json()
        assert "waypoint_id" in data

    # Stop tracking
    response = client.post("/routes/stop_tracking", json={"user_id": USER_ID})
    assert response.status_code == 200
    data = response.json()
    assert "message" in data
