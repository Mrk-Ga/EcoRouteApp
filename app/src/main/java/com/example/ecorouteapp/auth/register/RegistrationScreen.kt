package com.example.ecorouteapp.auth.register

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun RegistrationScreen(
    viewModel: RegistrationViewModel,
    goToLoginPage: () -> Unit,
    goToHomeScreen: () -> Unit
) {
    val registrationState by viewModel.registrationState.collectAsState()
    val fieldErrors by viewModel.fieldErrors.collectAsState()

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // Navigate once when success appears
    LaunchedEffect(registrationState) {
        if (registrationState is RegistrationState.Success) {
            goToHomeScreen()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Register", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Create a new account to start tracking air quality")
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            placeholder = { Text("johndoe") },
            modifier = Modifier.fillMaxWidth(),
            isError = fieldErrors.usernameError != null,
            supportingText = {
                fieldErrors.usernameError?.let {
                    Text(it, color = MaterialTheme.colorScheme.error)
                }
            }
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            placeholder = { Text("your@email.com") },
            modifier = Modifier.fillMaxWidth(),
            isError = fieldErrors.emailError != null,
            supportingText = {
                fieldErrors.emailError?.let {
                    Text(it, color = MaterialTheme.colorScheme.error)
                }
            }
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            placeholder = { Text("**********") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            isError = fieldErrors.passwordError != null,
            supportingText = {
                fieldErrors.passwordError?.let {
                    Text(it, color = MaterialTheme.colorScheme.error)
                }
            }
        )

        if (password.isNotEmpty()) {
            val strength = viewModel.getPasswordStrength(password)
            val (strengthText, strengthColor) = when (strength) {
                PasswordStrength.WEAK -> "Weak" to Color.Red
                PasswordStrength.MEDIUM -> "Medium" to Color(0xFFFFA500) // Orange
                PasswordStrength.STRONG -> "Strong" to Color.Green
            }
            Text(
                text = "Password strength: $strengthText",
                color = strengthColor,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            placeholder = { Text("**********") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            isError = fieldErrors.confirmPasswordError != null,
            supportingText = {
                fieldErrors.confirmPasswordError?.let {
                    Text(it, color = MaterialTheme.colorScheme.error)
                }
            }
        )
        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { viewModel.register(username, email, password, confirmPassword) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
            enabled = registrationState !is RegistrationState.Loading
        ) {
            if (registrationState is RegistrationState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White
                )
            } else {
                Text(text = "Register", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        ClickableText(
            text = AnnotatedString("Already have an account? Login"),
            onClick = { goToLoginPage() }
        )

        when (val state = registrationState) {
            is RegistrationState.Error -> {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = state.message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            else -> {}
        }
    }
}