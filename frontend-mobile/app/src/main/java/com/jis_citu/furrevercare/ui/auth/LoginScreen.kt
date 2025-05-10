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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isUnspecified
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jis_citu.furrevercare.R
import com.jis_citu.furrevercare.navigation.Routes
import com.jis_citu.furrevercare.theme.FurrEverCareTheme
import com.jis_citu.furrevercare.ui.auth.viewmodel.LoginViewModel
import com.jis_citu.furrevercare.ui.auth.viewmodel.LoginUiState
import com.jis_citu.furrevercare.ui.auth.viewmodel.LoginNavigationEvent
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState: LoginUiState by viewModel.uiState.collectAsState()

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

    val logoDrawable = if (isSystemInDarkTheme()) {
        R.drawable.logo_icontext_light
    } else {
        R.drawable.logo_icontext_dark
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp) // Overall padding for the screen content
        ) {
            if (!uiState.isLoading) {
                IconButton(
                    onClick = { navController.navigateUp() },
                    modifier = Modifier.align(Alignment.Start) // Aligns IconButton within the Padded Column
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        "Back",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(48.dp)) // Placeholder for back button space
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .imePadding(), // Handles keyboard overlap
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(Modifier.weight(0.5f)) // Pushes content down a bit

                Image(
                    painter = painterResource(id = logoDrawable),
                    contentDescription = "FurrEverCare Logo",
                    modifier = Modifier
                        .fillMaxWidth(0.7f) // Responsive width for the logo
                        .aspectRatio(2f)    // Maintain aspect ratio
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
                        .padding(horizontal = 32.dp, vertical = 8.dp) // Consistent padding for fields
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
                            )
                        }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp, vertical = 8.dp) // Consistent padding
                )

                // Error message or Spacer
                val currentErrorMessage = uiState.errorMessage
                val bodyMediumLineHeight = MaterialTheme.typography.bodyMedium.lineHeight
                val defaultSpacerHeight = with(LocalDensity.current) {
                    if (bodyMediumLineHeight.isSp) {
                        bodyMediumLineHeight.toDp() + 8.dp // Height of one line text + padding
                    } else if (!bodyMediumLineHeight.isUnspecified) {
                        bodyMediumLineHeight.value.dp + 8.dp // If already in dp or other convertible unit
                    } else {
                        24.dp // Fallback (approx one line height + padding)
                    }
                }

                if (currentErrorMessage != null) {
                    Text(
                        text = currentErrorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth() // Ensure it can center within available width
                            .padding(horizontal = 32.dp, vertical = 8.dp)
                    )
                } else {
                    Spacer(Modifier.height(defaultSpacerHeight)) // Use calculated or fallback height
                }

                // "Forgot Password?" Button - Centered
                TextButton(
                    onClick = {
                        if (!uiState.isLoading) {
                            navController.navigate(Routes.FORGOT_PASSWORD_EMAIL)
                        }
                    },
                    enabled = !uiState.isLoading,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally) // <<< CENTERED
                    // Consider if padding is still needed or if the parent Column's padding is sufficient
                    // .padding(horizontal = 32.dp) // Optional: if specific side padding is desired
                ) {
                    Text("Forgot Password?", color = MaterialTheme.colorScheme.primary)
                }

                Spacer(Modifier.height(16.dp)) // Space between "Forgot Password?" and "Log In" button

                Button(
                    onClick = viewModel::loginUser,
                    enabled = !uiState.isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp) // Consistent padding for button
                        .height(48.dp)
                ) {
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
                Spacer(Modifier.weight(1f)) // Pushes content up from bottom
            }
        }
    }
}

@Preview(showBackground = true, name = "Login Screen Light")
@Composable
fun LoginScreenLightPreview() {
    FurrEverCareTheme(darkTheme = false) {
        Surface {
            LoginScreen(rememberNavController(), hiltViewModel())
        }
    }
}

@Preview(showBackground = true, name = "Login Screen Dark")
@Composable
fun LoginScreenDarkPreview() {
    FurrEverCareTheme(darkTheme = true) {
        Surface {
            LoginScreen(rememberNavController(), hiltViewModel())
        }
    }
}