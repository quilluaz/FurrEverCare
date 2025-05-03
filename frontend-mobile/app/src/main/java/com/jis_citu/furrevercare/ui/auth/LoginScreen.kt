package com.jis_citu.furrevercare.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jis_citu.furrevercare.R
import com.jis_citu.furrevercare.navigation.Routes
import com.jis_citu.furrevercare.theme.FurrEverCareTheme
import com.jis_citu.furrevercare.ui.auth.viewmodel.LoginViewModel
import com.jis_citu.furrevercare.ui.auth.viewmodel.LoginNavigationEvent
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collectLatest { event ->
            when (event) {
                is LoginNavigationEvent.NavigateToHome -> {
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.WELCOME_AUTH) { inclusive = true }
                    }
                }
            }
        }
    }

    // Determine the correct logo based on the theme
    val logoDrawable = if (isSystemInDarkTheme()) {
        R.drawable.logo_icontext_light
    } else {
        R.drawable.logo_icontext_dark
    }

    // Screen content using theme background
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background // Theme background applied here
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Back Button
            if (!uiState.isLoading) {
                IconButton(
                    onClick = { navController.navigateUp() },
                    modifier = Modifier.align(Alignment.Start)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        "Back",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(48.dp)) // Maintain space when loading
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .imePadding(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(Modifier.weight(0.5f))

                Image(
                    painter = painterResource(id = logoDrawable), // Use theme-aware logo
                    contentDescription = "FurrEverCare Logo",
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .aspectRatio(2f)
                        .padding(bottom = 24.dp),
                    contentScale = ContentScale.Fit
                )

                OutlinedTextField(
                    value = uiState.email,
                    onValueChange = viewModel::updateEmail,
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
                    onValueChange = viewModel::updatePassword,
                    label = { Text("Password") },
                    visualTransformation = if (uiState.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    enabled = !uiState.isLoading,
                    trailingIcon = {
                        IconButton(onClick = viewModel::togglePasswordVisibility) {
                            Icon(
                                imageVector = if (uiState.passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (uiState.passwordVisible) "Hide password" else "Show password"
                                // Tint will be handled by IconButton defaults
                            )
                        }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp, vertical = 8.dp)
                )

                uiState.errorMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 32.dp, vertical = 8.dp)
                    )
                } ?: Spacer(Modifier.height(MaterialTheme.typography.bodyMedium.lineHeight.value.dp + 16.dp))

                TextButton(
                    onClick = {
                        if (!uiState.isLoading) {
                            navController.navigate(Routes.FORGOT_PASSWORD_EMAIL)
                        }
                    },
                    enabled = !uiState.isLoading,
                    modifier = Modifier.align(Alignment.End).padding(horizontal = 32.dp)
                ) {
                    // Use theme's primary color for the text button
                    Text("Forgot Password?", color = MaterialTheme.colorScheme.primary)
                }

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = viewModel::loginUser,
                    enabled = !uiState.isLoading,
                    colors = ButtonDefaults.buttonColors(
                        // Use theme's primary color
                        containerColor = MaterialTheme.colorScheme.primary,
                        // Adjust disabled color based on theme's primary
                        disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .height(48.dp)
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary, // Color for indicator on primary background
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(text = "Log In") // Text color (onPrimary) is handled by defaults
                    }
                }
                Spacer(Modifier.weight(1f))
            }
        }
    }
}

// Previews for LoginScreen
@Preview(showBackground = true, name = "Login Screen Light")
@Composable
fun LoginScreenLightPreview() {
    FurrEverCareTheme(darkTheme = false) {
        Surface { // Surface needed for background color in preview
            // A simple preview might just show the layout structure without full VM interaction
            LoginScreen(rememberNavController(), hiltViewModel()) // This might crash preview if VM needs real dependencies
            // Consider creating a version like LoginScreenContent(uiState = ..., onEmailChange = ...)
            // for easier previewing without Hilt.
        }
    }
}

@Preview(showBackground = true, name = "Login Screen Dark")
@Composable
fun LoginScreenDarkPreview() {
    FurrEverCareTheme(darkTheme = true) {
        Surface {
            LoginScreen(rememberNavController(), hiltViewModel()) // Same Hilt concern as above
        }
    }
}