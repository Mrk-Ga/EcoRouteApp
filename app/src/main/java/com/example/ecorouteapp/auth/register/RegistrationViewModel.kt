package com.example.ecorouteapp.auth.register

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecorouteapp.auth.AuthRepository
import com.example.ecorouteapp.auth.LoginRequest
import com.example.ecorouteapp.auth.RegisterRequest
import com.example.ecorouteapp.auth.SessionManager
import com.example.ecorouteapp.auth.UserSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.HttpException

class RegistrationViewModel(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _registrationState = MutableStateFlow<RegistrationState>(RegistrationState.Idle)
    val registrationState: StateFlow<RegistrationState> = _registrationState

    private val _fieldErrors = MutableStateFlow(FieldErrors())
    val fieldErrors: StateFlow<FieldErrors> = _fieldErrors

    /**
     * Flow:
     *  1) Client-side validation (UX)
     *  2) Backend /register (creates account)
     *  3) Backend /login (returns JWT) -> save session
     */
    fun register(username: String, email: String, password: String, confirmPassword: String) {
        _fieldErrors.value = FieldErrors()

        val cleanedUsername = username.trim()
        val cleanedEmail = email.trim().lowercase()

        val errors = validateInputData(cleanedUsername, cleanedEmail, password, confirmPassword)
        if (errors.hasErrors()) {
            _fieldErrors.value = errors
            _registrationState.value = RegistrationState.Error("Please fix the errors in the form")
            return
        }

        viewModelScope.launch {
            _registrationState.value = RegistrationState.Loading
            try {
                val registerResponse = authRepository.register(
                    RegisterRequest(cleanedUsername, cleanedEmail, password)
                )

                sessionManager.saveSession(
                    UserSession(registerResponse.userId, registerResponse.accessToken)
                )

                _registrationState.value = RegistrationState.Success(
                    userId = registerResponse.userId,
                    accessToken = registerResponse.accessToken,
                    infoMessage = "Account created"
                )
            } catch (e: HttpException) {
                handleHttpException(e)

            } catch (e: Exception) {
                _registrationState.value = RegistrationState.Error(
                    e.message ?: "Registration failed. Please try again."
                )
            }
        }
    }

    private fun handleHttpException(e: HttpException) {
        val backendDetail = parseFastApiDetail(e)

        when (e.code()) {
            409 -> {
                // mail zajety - pokaz pod polem email
                val msg = backendDetail ?: "This email is already taken."
                val current = _fieldErrors.value
                current.emailError = msg
                _fieldErrors.value = current

                _registrationState.value = RegistrationState.Error("Please fix the errors in the form")
            }
            422 -> {
                val msg = backendDetail ?: "Invalid input."
                _registrationState.value = RegistrationState.Error(msg)
            }
            else -> {
                val msg = backendDetail ?: "Registration failed (HTTP ${e.code()})."
                _registrationState.value = RegistrationState.Error(msg)
            }
        }
    }

    private fun parseFastApiDetail(e: HttpException): String? {
        return try {
            val body = e.response()?.errorBody()?.string() ?: return null
            val json = JSONObject(body)
            json.optString("detail", null)
        } catch (_: Exception) {
            null
        }
    }

    private fun validateInputData(
        username: String,
        email: String,
        password: String,
        confirmPassword: String
    ): FieldErrors {
        val errors = FieldErrors()

        // Username
        if (username.isBlank()) {
            errors.usernameError = "Username is required"
        } else if (username.length < 3) {
            errors.usernameError = "Username must be at least 3 characters"
        } else if (username.length > 50) {
            errors.usernameError = "Username must be less than 50 characters"
        }

        // Email
        if (email.isBlank()) {
            errors.emailError = "Email is required"
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errors.emailError = "Invalid email format"
        }

        // Password policy (UX check; backend should also enforce policy)
        if (password.isBlank()) {
            errors.passwordError = "Password is required"
        } else if (!isPasswordStrong(password)) {
            errors.passwordError = getPasswordPolicyMessage(password)
        }

        // Confirm password
        if (confirmPassword.isBlank()) {
            errors.confirmPasswordError = "Please confirm your password"
        } else if (password != confirmPassword) {
            errors.confirmPasswordError = "Passwords do not match"
        }

        return errors
    }

    private fun isPasswordStrong(password: String): Boolean {
        return password.length >= 8 &&
                password.any { it.isUpperCase() } &&
                password.any { it.isLowerCase() } &&
                password.any { it.isDigit() } &&
                password.any { !it.isLetterOrDigit() }
    }

    private fun getPasswordPolicyMessage(password: String): String {
        val issues = mutableListOf<String>()

        if (password.length < 8) issues.add("at least 8 characters")
        if (!password.any { it.isUpperCase() }) issues.add("one uppercase letter")
        if (!password.any { it.isLowerCase() }) issues.add("one lowercase letter")
        if (!password.any { it.isDigit() }) issues.add("one number")
        if (!password.any { !it.isLetterOrDigit() }) issues.add("one special character")

        return "Password must contain: ${issues.joinToString(", ")}"
    }

    fun getPasswordStrength(password: String): PasswordStrength {
        if (password.isEmpty()) return PasswordStrength.WEAK

        var score = 0
        if (password.length >= 8) score++
        if (password.length >= 12) score++
        if (password.any { it.isUpperCase() }) score++
        if (password.any { it.isLowerCase() }) score++
        if (password.any { it.isDigit() }) score++
        if (password.any { !it.isLetterOrDigit() }) score++

        return when (score) {
            in 0..2 -> PasswordStrength.WEAK
            in 3..4 -> PasswordStrength.MEDIUM
            else -> PasswordStrength.STRONG
        }
    }
}

data class FieldErrors(
    var usernameError: String? = null,
    var emailError: String? = null,
    var passwordError: String? = null,
    var confirmPasswordError: String? = null
) {
    fun hasErrors() = usernameError != null || emailError != null ||
            passwordError != null || confirmPasswordError != null
}

enum class PasswordStrength {
    WEAK, MEDIUM, STRONG
}

sealed class RegistrationState {
    object Idle : RegistrationState()
    object Loading : RegistrationState()
    data class Success(
        val userId: Int,
        val accessToken: String,
        val infoMessage: String? = null
    ) : RegistrationState()
    data class Error(val message: String) : RegistrationState()
}