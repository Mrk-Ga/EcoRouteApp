package com.example.ecorouteapp.network.testServer

import android.util.Log
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest

class MockDispatcher : Dispatcher() {

    private var requestCount = 0

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

            "/routes/routeId/location"->MockResponse()
                .setResponseCode(200)

            "/routes/routeId" -> {
                requestCount++
                val body = when (requestCount % 3) {
                    1 -> """
                        {
                          "PM25": 45.8,
                          "PM10": 22.1,
                          "AQI": 125,
                          "alert": "High",
                          "time": "20.12.2025"
                        }
                    """
                    2 -> """
                        {
                          "PM25": 25.3,
                          "PM10": 15.6,
                          "AQI": 80,
                          "alert": "Moderate",
                          "time": "20.12.2025"
                        }
                    """
                    else -> """
                        {
                          "PM25": 10.2,
                          "PM10": 5.7,
                          "AQI": 45,
                          "alert": "Low",
                          "time": "20.12.2025"
                        }
                    """
                }
                MockResponse()
                    .setResponseCode(200)
                    .setBody(body)
            }


            else -> MockResponse().setResponseCode(404)
        }

        Log.d(
            "MOCK_RESPONSE",
            "⬅️ ${response.status} ${response.getBody()?.readUtf8()}"
        )

        return response
    }
}