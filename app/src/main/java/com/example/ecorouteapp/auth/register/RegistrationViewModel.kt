package com.example.ecorouteapp.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecorouteapp.auth.AuthRepository
import com.example.ecorouteapp.auth.RegisterRequest
import com.example.ecorouteapp.auth.SessionManager
import com.example.ecorouteapp.auth.UserSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegistrationViewModel(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
): ViewModel() {

    private val _registrationState = MutableStateFlow<RegistrationState>(RegistrationState.Idle)
    val registrationState: StateFlow<RegistrationState> = _registrationState

    fun register(username:String, email: String, password: String, confirmPassword:String) {

        if (password != confirmPassword) {
            _registrationState.value = RegistrationState.Error("Passwords do not match")
            return
        }


        viewModelScope.launch {
            _registrationState.value = RegistrationState.Loading
            try {
                val response = authRepository.register(RegisterRequest(username, email, password))
                sessionManager.saveSession(UserSession(response.userId, response.accessToken))
                _registrationState.value = RegistrationState.Success(response.userId,response.accessToken)
            } catch (e: Exception) {
                _registrationState.value = RegistrationState.Error("Invalid credentials")
            }
        }
    }

}

sealed class RegistrationState {
    object Idle : RegistrationState()
    object Loading : RegistrationState()
    data class Success(val userId:Int, val accessMessage: String) : RegistrationState()
    data class Error(val message: String) : RegistrationState()
}
