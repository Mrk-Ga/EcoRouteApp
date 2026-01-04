package com.example.ecorouteapp.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecorouteapp.network.AuthRepository
import com.example.ecorouteapp.network.RegisterRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegistrationViewModel(private val authRepository: AuthRepository): ViewModel() {

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
                _registrationState.value = RegistrationState.Success(response.accessMessage)
            } catch (e: Exception) {
                _registrationState.value = RegistrationState.Error("Invalid credentials")
            }
        }
    }

}

sealed class RegistrationState {
    object Idle : RegistrationState()
    object Loading : RegistrationState()
    data class Success(val accessMessage: String) : RegistrationState()
    data class Error(val message: String) : RegistrationState()
}
