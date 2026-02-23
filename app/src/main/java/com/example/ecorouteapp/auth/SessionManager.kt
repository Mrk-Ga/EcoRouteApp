package com.example.ecorouteapp.auth

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SessionManager {
    private val _session = MutableStateFlow<UserSession?>(null)
    val session: StateFlow<UserSession?> = _session

    fun saveSession(session: UserSession) {
        _session.value = session
    }

    fun clearSession() {
        _session.value = null
    }
}

data class UserSession(
    val userId: Int,
    val token: String
)
