package com.example.ecorouteapp.register

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ecorouteapp.login.LoginState

@Composable
fun RegistrationScreen(
    viewModel: RegistrationViewModel,
    goToLoginPage: () -> Unit
) {

    val registrationState by viewModel.registrationState.collectAsState()
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

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
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            placeholder = { Text("your@email.com") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            placeholder = { Text("**********") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        // TODO: Implement password strength indicator
        Text(text = "Password strength: Weak", color = Color.Red)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            placeholder = { Text("**********") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = { viewModel.register(username, email,password, confirmPassword) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
        ) {
            Text(text = "Register", color = Color.White)
        }
        Spacer(modifier = Modifier.height(16.dp))
        ClickableText(
            text = AnnotatedString("Already have an account? Login"),
            onClick = { goToLoginPage() }
        )

        when (val state = registrationState) {
            is RegistrationState.Success -> {
                goToLoginPage()
            }
            is RegistrationState.Error -> {
                Text(state.message, color = Color.Red)
            }
            else -> {}
        }
    }
}
/*
@Preview(showBackground = true)
@Composable
fun RegistrationScreenPreview() {
    RegistrationScreen(goToLoginPage = {})
}

 */