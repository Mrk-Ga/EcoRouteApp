package com.example.ecorouteapp.network.testServer

import android.util.Log
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest

class MockDispatcher : Dispatcher() {

    override fun dispatch(request: RecordedRequest): MockResponse {
        Log.d("MOCK_DISPATCH", "➡️ ${request.method} ${request.path}")
        Log.d("MOCK_DISPATCH", "Body: ${request.body.readUtf8()}")

        val response = when (request.path) {

            "/auth/login" -> MockResponse()
                .setResponseCode(200)
                .setBody(
                    """
                    {
                      "accessToken": "mock-access-token",
                      "refreshToken": "mock-refresh-token"
                    }
                    """
                )

            "/routes/routeId" -> MockResponse()
                .setResponseCode(200)
                .setBody(
                    """
                        {
                          "PM25": 38.2,
                          "PM10": 18.7,
                          "AQI": 120,
                          "currentLocation": [52.2297, 21.0122],
                          "alert": "High",
                          "time": "20.12.2025"
                        }
                    """
                )

            else -> MockResponse().setResponseCode(404)
        }

        Log.d(
            "MOCK_RESPONSE",
            "⬅️ ${response.status} ${response.getBody()?.readUtf8()}"
        )

        return response
    }
}