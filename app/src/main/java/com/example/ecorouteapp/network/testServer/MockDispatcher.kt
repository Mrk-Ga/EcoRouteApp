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

            "/routes/next_id" -> MockResponse()
                .setResponseCode(200)
                .setBody(
                    """
                    {
                        "routeId": "12345"
                    }
                    """
                )

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

            "/routes/12345/location"->MockResponse()
                .setResponseCode(200)

            "/history/routes"->MockResponse()
                .setResponseCode(200)
                .setBody(
                    """
                    [
                        {
                        "id": "1",
                        "date": "20.12.2025",
                        "status": "completed",
                        "time": "13:12",
                        "duration": "25m",
                        "points": 5,
                        "avgPm25": "10.5",
                        "maxPm25": "15.2"
                        },
                        {
                        "id": "2",
                        "date": "20.12.2025",
                        "status": "completed",
                        "time": "13:12",
                        "duration": "25m",
                        "points": 5,
                        "avgPm25": "10.5",
                        "maxPm25": "15.2"
                        }
                    ]    
                    """
                )

            "/routes/12345" -> {
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

            "/details/1"-> MockResponse()
                .setResponseCode(200)
                .setBody(
                    """
                        {
                            "id" : "1",
                            "date": "20.12.2025",
                            "time": "13:12",
                            "status": "completed",
                            "duration": "25m",
                            "dataPoints": 5,
                            "avgPm25": 38.2,
                            "maxPm25": 52.1,
                            "avgPm10": 58.7,
                            "maxPm10": 78.3,
                            "exposureLevel": "High",
                            "exposureAssessment": "Your exposure was high. Consider choosing alternative routes with better air quality.",
                            "startTime": "20.12.2025, 13:12:53",
                            "endTime": "20.12.2025, 13:37:53",
                            "reportGeneratedTime": "20.12.2025, 13:37:53",
                            "healthRecommendations": [
                                "Consider using a mask (N95 or better) on similar routes",
                                "Monitor air quality forecasts before planning outdoor activities",
                                "Choose routes with more green spaces when possible"
                                ]
                            
                        }
                        
                    """.trimIndent()
                )

            "/details/12345" -> MockResponse()
                .setResponseCode(200)
                .setBody(
                    """
                        {
                            "id" : "1",
                            "date": "20.12.2025",
                            "time": "13:12",
                            "status": "completed",
                            "duration": "25m",
                            "dataPoints": 5,
                            "avgPm25": 38.2,
                            "maxPm25": 52.1,
                            "avgPm10": 58.7,
                            "maxPm10": 78.3,
                            "exposureLevel": "High",
                            "exposureAssessment": "Your exposure was high. Consider choosing alternative routes with better air quality.",
                            "startTime": "20.12.2025, 13:12:53",
                            "endTime": "20.12.2025, 13:37:53",
                            "reportGeneratedTime": "20.12.2025, 13:37:53",
                            "healthRecommendations": [
                                "Consider using a mask (N95 or better) on similar routes",
                                "Monitor air quality forecasts before planning outdoor activities",
                                "Choose routes with more green spaces when possible"
                                ]
                            
                        }
                        
                    """.trimIndent()
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