package com.jis_citu.furrevercare.ui.auth

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
// import androidx.navigation.NavHostController // Not needed directly in this file
import androidx.navigation.compose.rememberNavController
import com.jis_citu.furrevercare.R
import com.jis_citu.furrevercare.navigation.Routes
import com.jis_citu.furrevercare.theme.FurrEverCareTheme
// import com.jis_citu.furrevercare.theme.PrimaryGreen // Use MaterialTheme.colorScheme.primary
import com.jis_citu.furrevercare.ui.auth.viewmodel.RegisterViewModel
// import com.jis_citu.furrevercare.ui.auth.viewmodel.RegisterUiState // uiState is collected directly
import com.jis_citu.furrevercare.ui.auth.viewmodel.RegisterNavigationEvent
import kotlinx.coroutines.flow.collectLatest


@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collectLatest { event ->
            when (event) {
                is RegisterNavigationEvent.NavigateToSuccess -> {
                    navController.navigate(Routes.VERIFICATION_SUCCESS) {
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
                if (!uiState.isLoading) { // Show back button only if not loading
                    IconButton(
                        onClick = {
                            if (uiState.currentStep > 1) {
                                viewModel.previousStep()
                            } else {
                                navController.navigateUp()
                            }
                        },
                        modifier = Modifier.align(Alignment.Start)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = MaterialTheme.colorScheme.onBackground)
                    }
                } else { // Maintain space when loading
                    Spacer(modifier = Modifier.height(48.dp)) // Standard IconButton size
                }

                Box(modifier = Modifier.weight(1f).padding(top = 16.dp)) {
                    when (uiState.currentStep) {
                        1 -> RegisterStep1Email( // Renamed for clarity
                            email = uiState.email,
                            onEmailChange = viewModel::updateEmail,
                            isEmailValid = uiState.isEmailValid,
                            onNext = viewModel::nextStep,
                            isLoading = uiState.isLoading
                        )
                        2 -> RegisterStep2Password( // Renamed for clarity
                            password = uiState.password,
                            onPasswordChange = viewModel::updatePassword,
                            confirmPassword = uiState.confirmPassword,
                            onConfirmPasswordChange = viewModel::updateConfirmPassword,
                            isPasswordValid = uiState.isPasswordValid,
                            doPasswordsMatch = uiState.doPasswordsMatch,
                            onNext = viewModel::nextStep,
                            isLoading = uiState.isLoading
                        )
                        3 -> RegisterStep3FullName( // Renamed for clarity and modified
                            fullName = uiState.fullName,
                            onFullNameChange = viewModel::updateFullName,
                            isFullNameValid = uiState.isFullNameValid, // Use isFullNameValid
                            onComplete = viewModel::registerUser,
                            isLoading = uiState.isLoading
                        )
                    }
                }

                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.padding(bottom = 8.dp))
                        Spacer(modifier = Modifier.height(44.dp)) // Placeholder for error text and dots
                    } else {
                        uiState.errorMessage?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        } ?: Spacer(modifier = Modifier.height(MaterialTheme.typography.bodyMedium.lineHeight.value.dp + 16.dp)) // Approx height of error text + padding
                        ProgressDots(currentStep = uiState.currentStep, totalSteps = 3) // totalSteps is 3
                    }
                }
            }
        }
    }
}

@Composable
fun RegisterStep1Email( // Renamed from RegisterStep1
    email: String,
    onEmailChange: (String) -> Unit,
    isEmailValid: Boolean,
    onNext: () -> Unit,
    isLoading: Boolean
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp).imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
    ) {
        Spacer(Modifier.weight(0.5f))
        Image(painterResource(R.drawable.signup_email), "Email Illustration", modifier = Modifier.fillMaxWidth(0.6f).aspectRatio(1f).padding(bottom = 24.dp), contentScale = ContentScale.Fit)
        Text("Enter your email", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.padding(bottom = 8.dp), color = MaterialTheme.colorScheme.onBackground)
        Text("We'll use this to create your FurrEverCare account.", style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
        OutlinedTextField(
            value = email, onValueChange = onEmailChange, label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), singleLine = true,
            enabled = !isLoading,
            isError = email.isNotEmpty() && !isEmailValid,
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )
        if (email.isNotEmpty() && !isEmailValid) {
            Text("Please enter a valid email address.", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.align(Alignment.Start).padding(start = 4.dp))
        }
        Spacer(Modifier.weight(1f))
        Button(
            onClick = onNext,
            enabled = isEmailValid && !isLoading,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp).height(48.dp)
        ) { Text("Continue") }
    }
}

@Composable
fun RegisterStep2Password( // Renamed from RegisterStep3
    password: String, onPasswordChange: (String) -> Unit,
    confirmPassword: String, onConfirmPasswordChange: (String) -> Unit,
    isPasswordValid: Boolean,
    doPasswordsMatch: Boolean,
    onNext: () -> Unit, isLoading: Boolean
) {
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp).imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
    ) {
        Spacer(Modifier.weight(0.5f))
        Image(painterResource(R.drawable.create_password), "Password Illustration", modifier = Modifier.fillMaxWidth(0.6f).aspectRatio(1f).padding(bottom = 24.dp), contentScale = ContentScale.Fit)
        Text("Create a password", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.padding(bottom = 8.dp), color = MaterialTheme.colorScheme.onBackground)
        Text("Password must be at least 8 characters long.", style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
        OutlinedTextField(
            value = password, onValueChange = onPasswordChange, label = { Text("Password") }, enabled = !isLoading,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = { IconButton(onClick = { passwordVisible = !passwordVisible }) { Icon(if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, "", tint = MaterialTheme.colorScheme.onSurfaceVariant) } },
            isError = password.isNotEmpty() && !isPasswordValid,
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )
        if (password.isNotEmpty() && !isPasswordValid) { Text("Password must be at least 8 characters.", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.align(Alignment.Start).padding(start = 4.dp)) }
        Spacer(Modifier.height(4.dp))
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = onConfirmPasswordChange,
            label = { Text("Confirm Password") }, enabled = !isLoading,
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = { IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) { Icon(if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, "", tint = MaterialTheme.colorScheme.onSurfaceVariant) } },
            isError = confirmPassword.isNotEmpty() && !doPasswordsMatch,
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )
        if (confirmPassword.isNotEmpty() && !doPasswordsMatch) { Text("Passwords do not match.", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.align(Alignment.Start).padding(start = 4.dp)) }
        Spacer(Modifier.weight(1f))
        Button(
            onClick = onNext,
            enabled = isPasswordValid && doPasswordsMatch && !isLoading,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp).height(48.dp)
        ) { Text("Continue") }
    }
}


@Composable
fun RegisterStep3FullName( // Renamed from RegisterStep4
    fullName: String, onFullNameChange: (String) -> Unit, // Changed parameters
    isFullNameValid: Boolean, // Changed parameter name
    onComplete: () -> Unit, isLoading: Boolean
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp).imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
    ) {
        Spacer(Modifier.weight(0.5f))
        Image(painterResource(R.drawable.signup_name), "Name Illustration", modifier = Modifier.fillMaxWidth(0.6f).aspectRatio(1f).padding(bottom = 24.dp), contentScale = ContentScale.Fit)
        Text("What should we call you?", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.padding(bottom = 8.dp), color = MaterialTheme.colorScheme.onBackground)
        Text("Enter your full name as you'd like it to appear.", style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
        OutlinedTextField(
            value = fullName, // Bind to fullName
            onValueChange = onFullNameChange, // Use onFullNameChange
            label = { Text("Full Name") },
            singleLine = true,
            enabled = !isLoading,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text, capitalization = KeyboardCapitalization.Words),
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            ),
            isError = fullName.isNotEmpty() && !isFullNameValid // Validate fullName
        )
        if (fullName.isNotEmpty() && !isFullNameValid) {
            Text("Full name must be at least 2 characters.", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.align(Alignment.Start).padding(start = 4.dp))
        }

        // Optional: Add Phone Number Field Here if desired as part of this step
        // OutlinedTextField(value = phone, onValueChange = onPhoneChange, label = { Text("Phone (Optional)")}, ...)

        Spacer(Modifier.weight(1f))
        Button(
            onClick = onComplete,
            enabled = isFullNameValid && !isLoading, // Use isFullNameValid
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp).height(48.dp)
        ) {
            if (isLoading) { CircularProgressIndicator(Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp) }
            else { Text("Complete Registration") }
        }
    }
}

@Composable
fun ProgressDots(currentStep: Int, totalSteps: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth().padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        for (i in 1..totalSteps) {
            Box(
                modifier = Modifier.padding(horizontal = 6.dp).size(12.dp)
                    .clip(MaterialTheme.shapes.small) // Use MaterialTheme shape
                    .background(color = if (i <= currentStep) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
            )
        }
    }
}

// --- Previews ---
@Preview(showBackground = true, name = "Register Step 1 Email")
@Composable
private fun RegisterStep1EmailPreview() { // Renamed
    FurrEverCareTheme {
        Surface {
            RegisterStep1Email(email = "test@example.com", onEmailChange = {}, isEmailValid = true, onNext = {}, isLoading = false)
        }
    }
}

@Preview(showBackground = true, name = "Register Step 2 Password")
@Composable
private fun RegisterStep2PasswordPreview() { // Renamed
    FurrEverCareTheme { Surface { RegisterStep2Password("password", {}, "password", {}, true, true, {}, false) } }
}

@Preview(showBackground = true, name = "Register Step 3 Full Name")
@Composable
private fun RegisterStep3FullNamePreview() { // Renamed
    FurrEverCareTheme { Surface { RegisterStep3FullName("John Doe", {}, true, {}, false) } }
}