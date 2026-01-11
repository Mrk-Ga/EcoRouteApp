package com.example.ecorouteapp.report

/*data class Sensor(
    val pollutionType:String = "",
    val lastReading:Float = 0.0f,
    val sensorId:Int = 0
)*/

data class Sensor(
    val sensorId: Int = 0,
    val type: String = "",
    val value: Float = 0.0f,
    val unit: String = "",
    val lastUpdate: String = ""
)


//Station name must be unique!!
data class AvailableStationReport(
    val sensors:List<Sensor> = emptyList<Sensor>(),
    val name: String = "",
    val distance: Double = 0.0
)


data class SensorReportData(
    var sensorId: Int = 0,
    var report:String = ""
)