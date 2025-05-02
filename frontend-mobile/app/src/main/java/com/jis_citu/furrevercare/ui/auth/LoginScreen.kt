package com.jis_citu.furrevercare.ui.auth

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jis_citu.furrevercare.R
import com.jis_citu.furrevercare.navigation.Routes
import com.jis_citu.furrevercare.theme.FurrEverCareTheme
import com.jis_citu.furrevercare.theme.PrimaryGreen
import com.jis_citu.furrevercare.ui.auth.viewmodel.LoginViewModel
import com.jis_citu.furrevercare.ui.auth.viewmodel.LoginUiState
import com.jis_citu.furrevercare.ui.auth.viewmodel.LoginNavigationEvent
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel() // Inject ViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    // Handle navigation events
    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collectLatest { event ->
            when (event) {
                is LoginNavigationEvent.NavigateToHome -> {
                    // Navigate to main app screen, clearing the auth stack
                    navController.navigate(Routes.MAIN) { // Changed from HOME to MAIN
                        popUpTo(Routes.WELCOME_AUTH) { inclusive = true }
                    }
                }
            }
        }
    }

    FurrEverCareTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Back Button - Show only if not loading
                if (!uiState.isLoading) {
                    IconButton(
                        onClick = { navController.navigateUp() },
                        modifier = Modifier.align(Alignment.Start)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = MaterialTheme.colorScheme.onBackground)
                    }
                } else {
                    Spacer(modifier = Modifier.height(48.dp)) // Placeholder
                }


                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f) // Takes remaining space
                        .imePadding(), // Handles keyboard overlap
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center // Center content vertically
                ) {
                    Spacer(Modifier.weight(0.5f)) // Pushes content down a bit

                    Image(
                        painter = painterResource(id = R.drawable.logo_icontext_colored),
                        contentDescription = "FurrEverCare Logo",
                        modifier = Modifier
                            .fillMaxWidth(0.7f) // Adjust size as needed
                            .aspectRatio(2f) // Adjust aspect ratio as needed
                            .padding(bottom = 24.dp),
                        contentScale = ContentScale.Fit
                    )

                    OutlinedTextField(
                        value = uiState.email,
                        onValueChange = viewModel::updateEmail, // Use ViewModel function
                        label = { Text("Email") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        enabled = !uiState.isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp, vertical = 8.dp)
                    )

                    OutlinedTextField(
                        value = uiState.password,
                        onValueChange = viewModel::updatePassword, // Use ViewModel function
                        label = { Text("Password") },
                        visualTransformation = if (uiState.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        enabled = !uiState.isLoading,
                        trailingIcon = {
                            IconButton(onClick = viewModel::togglePasswordVisibility) { // Use ViewModel function
                                Icon(
                                    imageVector = if (uiState.passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (uiState.passwordVisible) "Hide password" else "Show password"
                                )
                            }
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp, vertical = 8.dp)
                    )

                    // Display Error Message
                    uiState.errorMessage?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 32.dp, vertical = 8.dp)
                        )
                    } ?: Spacer(Modifier.height(MaterialTheme.typography.bodyMedium.lineHeight.value.dp + 16.dp)) // Placeholder space

                    // Forgot Password Button (Optional - Add if needed)
                    TextButton(
                        onClick = {
                            if (!uiState.isLoading) {
                                navController.navigate(Routes.FORGOT_PASSWORD_EMAIL)
                            }
                        },
                        enabled = !uiState.isLoading,
                        modifier = Modifier.align(Alignment.End).padding(horizontal = 32.dp)
                    ) {
                        Text("Forgot Password?", color = PrimaryGreen)
                    }

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = viewModel::loginUser, // Use ViewModel function
                        enabled = !uiState.isLoading, // Disable button when loading
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryGreen,
                            disabledContainerColor = PrimaryGreen.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp)
                            .height(48.dp)
                    ) {
                        // Show loading indicator inside button or text
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(text = "Log In")
                        }
                    }
                    Spacer(Modifier.weight(1f)) // Pushes content towards center
                }
            }
        }
    }
}