package com.example.ecorouteapp.network.testServer

import android.util.Log
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest

class MockDispatcher : Dispatcher() {

    val regex = Regex("^/report/sensors/.+_.+$")

    override fun dispatch(request: RecordedRequest): MockResponse {
        Log.d("MOCK_DISPATCH", "➡️ ${request.method} ${request.path}")
        Log.d("MOCK_DISPATCH", "Body: ${request.body.readUtf8()}")

        val path = request.path!!

        val response = when {

            path == "/routes/next_id" ->
                loadNextRouteIdResponse()

            path == "/auth/login" ->
                loadLoginResponse()

            path == "/auth/register" -> loadRegisterResponse()


            path == "/route/stop_tracking" -> MockResponse()
                .setResponseCode(200)

            path == "/route/start_tracking" -> MockResponse()
                .setResponseCode(200)
                .setBody("""
                    {
                      "routeId": "12345"
                    }
                """.trimIndent())


            path == "/routes/12345/location" ->
                loadPostLocationResponse()

            path == "/history/routes" ->
                loadHistoryRoutesResponse()

            //report/sensors/..._...
            regex.matches(path) ->
                loadSensorsResponse(path)

            path.startsWith("/report/sensors/")->
                loadPostSensorReportResponse()

            path == "/routes/12345" ->
                loadRouteLiveDataResponse()

            path == "/details/1" ->
                loadDetailsResponse("1")

            path == "/details/12345" ->
                loadDetailsResponse("12345")

            path == "/settings/gdpr/12345" -> loadSettingsResponse()

            path == "/settings/gdpr/update" -> loadSettingsResponseUpdate()

            path == "/admin/stations" -> loadMinitoringStations()

            path == "/admin/stations/measurements/st1" -> loadStationMeasurements()

            path == "/admin/stations/measurements/st2" -> loadStationMeasurements()

            path == "/admin/stations/st1/status" -> postStationStatusResponse()

            path == "/admin/stations/st2/status" -> postStationStatusResponse()



            else ->
                MockResponse().setResponseCode(404)
        }

        Log.d(
            "MOCK_RESPONSE",
            "⬅️ ${response.status} ${response.getBody()?.readUtf8()}"
        )

        return response
    }

    fun loadRegisterResponse() = MockResponse()
        .setResponseCode(200)
        .setBody("""
            {
               "userId": 12345,
                "accessToken": "mock-refresh-token" 
            }
        """.trimIndent())

    fun loadSettingsResponseUpdate() = MockResponse()
        .setResponseCode(200)
    fun loadSettingsResponse() = MockResponse()
        .setResponseCode(200)
        .setBody("""
            {
              "locationDataCollection": true,
              "airQualityDataCollection": false,
              "marketingCommunications": true
            }
        """.trimIndent())

    fun loadNextRouteIdResponse() = MockResponse()
        .setResponseCode(200)
        .setBody(
            """
            {
              "routeId": "12345"
            }
            """.trimIndent()
        )

    fun loadLoginResponse() = MockResponse()
        .setResponseCode(200)
        .setBody(
            """
        {
          "userId": 12345,
          "accessToken": "mock-refresh-token"
        }
        """.trimIndent()
        )

    fun loadPostLocationResponse() = MockResponse()
        .setResponseCode(200)

    fun loadPostSensorReportResponse() = MockResponse()
        .setResponseCode(200)


    fun loadHistoryRoutesResponse() = MockResponse()
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
        """.trimIndent()
        )

    fun loadSensorsResponse(path: String): MockResponse {
        val location = path.substringAfterLast("/")

        return MockResponse()
            .setResponseCode(200)
            .setBody(
                """
            [
              {
                "name": "Wroclaw Old Town",
                "distance": 10.5,
                "sensors": [
                  { "sensorId":1, "type": "PM2.5", "value": 15.2, "unit": "µg/m³", "lastUpdate": "12:38:18"},
                  { "sensorId":2, "type": "PM10", "value": 43.3, "unit": "µg/m³", "lastUpdate": "12:40:12"}
                ]
              },
              {
                "name": "Wroclaw Biskupin",
                "distance": 55.5,
                "sensors": [
                  { "sensorId":3, "type": "PM2.5", "value": 23.1, "unit": "µg/m³", "lastUpdate": "10:35:08"},
                  { "sensorId":4, "type": "PM10", "value": 41.3, "unit": "µg/m³", "lastUpdate": "14:40:55"}
                ]
              }
            ]
            """.trimIndent()
            )



    }

    private var requestCount = 0

    fun loadRouteLiveDataResponse(): MockResponse {
        requestCount++

        val body = when (requestCount % 3) {
            1 -> routeBodyHigh()
            2 -> routeBodyMedium()
            else -> routeBodyLow()
        }

        return MockResponse()
            .setResponseCode(200)
            .setBody(body)
    }
    fun routeBodyHigh() = """
        {
          "PM25": 45.8,
          "PM10": 22.1,
          "AQI": 125
        }
        """.trimIndent()
    fun routeBodyMedium() = """
        {
          "PM25": 25.3,
          "PM10": 15.6,
          "AQI": 80
        }
        """.trimIndent()
    fun routeBodyLow() = """
        {
          "PM25": 10.2,
          "PM10": 5.7,
          "AQI": 45
        }
        """.trimIndent()
    fun loadDetailsResponse(id: String) = MockResponse()
        .setResponseCode(200)
        .setBody(
            """
        {
          "id": "$id",
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
            "Consider using a mask (N95 or better)",
            "Monitor air quality forecasts",
            "Choose greener routes"
          ]
        }
        """.trimIndent()
        )

    fun loadMinitoringStations()= MockResponse()
        .setResponseCode(200)
        .setBody("""
            
            [
                {
                    "id": "st1",
                    "name": "Wroclaw Old Town",
                    "status": true,
                    "type": "stationary",
                    "latitude": "51.1099",
                    "longitude": "17.0325",
                    "created": "20.12.2025"
                },
                {
                    "id": "st2",
                    "name": "Wroclaw Biskupin",
                    "status": false,
                    "type": "stationary",
                    "latitude": "51.1099",
                    "longitude": "17.0335",
                    "created": "12.06.2025"
                }
            ]
        """.trimIndent())

    fun loadStationMeasurements() = MockResponse()
        .setResponseCode(200)
        .setBody("""
            [
                {
                    "sensorId": 11,
                    "date": "20.12.2025",
                    "time": "12:38:18",
                    "PM25": 13.22,
                    "PM10": 21.01,
                    "AQI": 45,
                    "unit": "µg/m³"
                },
                {
                    "sensorId": 12,
                    "date": "11.10.2025",
                    "time": "20:12:03",
                    "PM25": 17.21,
                    "PM10": 30.55,
                    "AQI": 47,
                    "unit": "µg/m³"
                }
            ]
        """.trimIndent())

    fun postStationStatusResponse() = MockResponse()
        .setResponseCode(200)
}
