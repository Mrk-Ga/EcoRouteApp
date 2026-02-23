package com.example.ecorouteapp.auth.login

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
fun LoginScreen(
    viewModel: LoginViewModel,
    goToHomePage: () -> Unit,
    goToRegistrationPage: () -> Unit
) {
/*    val viewModel: LoginViewModel = viewModel(
        factory = LoginViewModelFactory(AuthRepository(RetrofitInstance.api))
    )
*/
    val loginState by viewModel.loginState.collectAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Login", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Enter your credentials to access your account")
        Spacer(modifier = Modifier.height(32.dp))
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
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = { viewModel.login(email, password) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
            enabled = loginState !is LoginState.Loading
        ) {
            if (loginState is LoginState.Loading) {
                CircularProgressIndicator(color = Color.White)
            } else {
                Text(text = "Login", color = Color.White)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        ClickableText(
            text = AnnotatedString("Don't have an account? Register"),
            onClick = { goToRegistrationPage() }
        )

        when (val state = loginState) {
            is LoginState.Success -> {
                goToHomePage()
            }
            is LoginState.Error -> {
                Text(state.message, color = Color.Red)
                //goToHomePage()
            }
            else -> {}
        }
    }
}
/*
@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(viewModel = ,goToHomePage = {}, goToRegistrationPage = {})
}

 */
