package com.example.ecorouteapp.network.testServer

import android.util.Log
import com.example.ecorouteapp.network.testServer.MockDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.mockwebserver.MockWebServer

object MockBackend {

    private var started = false
    val server = MockWebServer()




    @Synchronized
    fun start() {
        if (started) return
        server.start(8081)
        started = true
        Log.d("MOCK_DEBUG", "MockWebServer started")

        server.dispatcher = MockDispatcher()
    }

    @Synchronized
    fun stop() {
        if (!started) return

        try {
            server.shutdown()
            Log.d("MOCK_DEBUG", "MockWebServer SHUTDOWN")
        } catch (e: Exception) {
            Log.e("MOCK_DEBUG", "Shutdown error", e)
        } finally {
            started = false
        }
    }
}