import pytest
from fastapi.testclient import TestClient
from backend.main import app

client = TestClient(app)

def test_route_start_and_stop():
    start_payload = {"user_id": 2}
    start_response = client.post("/routes/start_tracking", json=start_payload)
    assert start_response.status_code == 200
    route_id = start_response.json()["route_id"]
    assert isinstance(route_id, int)

    waypoints = [
        {"latitude": 52.2297, "longitude": 21.0122, "timestamp": "2026-01-13T14:00:00Z"},
        {"latitude": 52.2300, "longitude": 21.0130, "timestamp": "2026-01-13T14:05:00Z"},
        {"latitude": 52.2305, "longitude": 21.0160, "timestamp": "2026-01-13T14:10:00Z"}
    ]
    for wp in waypoints:
        resp = client.post(f"/routes/{route_id}/location", json=wp)
        assert resp.status_code == 200
        assert "waypoint_id" in resp.json()
        info_resp = client.get(f"/routes/{route_id}")
        assert info_resp.status_code == 200
        info = info_resp.json()
        assert "PM25" in info
        assert "PM10" in info
        assert "AQI" in info
        assert "alert" in info
        assert "time" in info

    stop_payload = {"user_id": 2, "route_id": route_id}
    stop_response = client.post("/routes/stop_tracking", json=stop_payload)
    assert stop_response.status_code == 200
    data = stop_response.json()
    print("StopTrackingResponse:", data)
    assert data["route_id"] == route_id
    assert "message" in data
    assert "total_duration_seconds" in data
    assert "avg_speed" in data
    assert "distance" in data
    assert "waypoint_count" in data
    assert "start_time" in data
    assert "end_time" in data
    assert "pollution_summary" in data
