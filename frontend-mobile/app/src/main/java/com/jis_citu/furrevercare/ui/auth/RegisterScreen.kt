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
import androidx.compose.runtime.* // Keep basic runtime imports
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
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.jis_citu.furrevercare.R
import com.jis_citu.furrevercare.navigation.Routes
import com.jis_citu.furrevercare.theme.FurrEverCareTheme
import com.jis_citu.furrevercare.theme.PrimaryGreen
import com.jis_citu.furrevercare.ui.auth.viewmodel.RegisterViewModel
import com.jis_citu.furrevercare.ui.auth.viewmodel.RegisterUiState
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
                if (!uiState.isLoading) {
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
                } else {
                    Spacer(modifier = Modifier.height(48.dp))
                }

                Box(modifier = Modifier.weight(1f).padding(top = 16.dp)) {
                    when (uiState.currentStep) {
                        1 -> RegisterStep1(
                            email = uiState.email,
                            onEmailChange = viewModel::updateEmail,
                            isEmailValid = uiState.isEmailValid,
                            onNext = viewModel::nextStep,
                            isLoading = uiState.isLoading
                        )
                        2 -> RegisterStep3(
                            password = uiState.password,
                            onPasswordChange = viewModel::updatePassword,
                            confirmPassword = uiState.confirmPassword,
                            onConfirmPasswordChange = viewModel::updateConfirmPassword,
                            isPasswordValid = uiState.isPasswordValid,
                            doPasswordsMatch = uiState.doPasswordsMatch,
                            onNext = viewModel::nextStep,
                            isLoading = uiState.isLoading
                        )
                        3 -> RegisterStep4(
                            firstName = uiState.firstName,
                            onFirstNameChange = viewModel::updateFirstName,
                            lastName = uiState.lastName,
                            onLastNameChange = viewModel::updateLastName,
                            isNameValid = uiState.isNameValid,
                            onComplete = viewModel::registerUser,
                            isLoading = uiState.isLoading
                        )
                    }
                }

                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.padding(bottom = 8.dp))
                        Spacer(modifier = Modifier.height(44.dp))
                    } else {
                        uiState.errorMessage?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        } ?: Spacer(modifier = Modifier.height(MaterialTheme.typography.bodyMedium.lineHeight.value.dp + 16.dp))
                        ProgressDots(currentStep = uiState.currentStep, totalSteps = 3)
                    }
                }
            }
        }
    }
}

@Composable
fun RegisterStep1(
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
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )
        if (email.isNotEmpty() && !isEmailValid) {
            Text("Please enter a valid email address.", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.align(Alignment.Start).padding(start = 4.dp))
        }
        Spacer(Modifier.weight(1f))
        Button(
            onClick = onNext,
            enabled = isEmailValid && !isLoading,
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen, disabledContainerColor = PrimaryGreen.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp).height(48.dp)
        ) { Text("Continue") }
    }
}

@Composable
fun RegisterStep3(
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
            isError = password.isNotEmpty() && !isPasswordValid, // Use passed validation state
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
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
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )
        if (confirmPassword.isNotEmpty() && !doPasswordsMatch) { Text("Passwords do not match.", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.align(Alignment.Start).padding(start = 4.dp)) }
        Spacer(Modifier.weight(1f))
        Button(
            onClick = onNext,
            enabled = isPasswordValid && doPasswordsMatch && !isLoading,
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen, disabledContainerColor = PrimaryGreen.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp).height(48.dp)
        ) { Text("Continue") }
    }
}


@Composable
fun RegisterStep4(
    firstName: String, onFirstNameChange: (String) -> Unit,
    lastName: String, onLastNameChange: (String) -> Unit,
    isNameValid: Boolean, // Receive validation state
    onComplete: () -> Unit, isLoading: Boolean
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp).imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
    ) {
        Spacer(Modifier.weight(0.5f))
        Image(painterResource(R.drawable.signup_name), "Name Illustration", modifier = Modifier.fillMaxWidth(0.6f).aspectRatio(1f).padding(bottom = 24.dp), contentScale = ContentScale.Fit)
        Text("What should we call you?", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.padding(bottom = 8.dp), color = MaterialTheme.colorScheme.onBackground)
        Text("Enter your first and last name.", style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
        OutlinedTextField(
            value = firstName, onValueChange = onFirstNameChange, label = { Text("First Name") }, singleLine = true, enabled = !isLoading,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text, capitalization = KeyboardCapitalization.Words),
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )
        OutlinedTextField(
            value = lastName, onValueChange = onLastNameChange, label = { Text("Last Name") }, singleLine = true, enabled = !isLoading,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text, capitalization = KeyboardCapitalization.Words),
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )
        Spacer(Modifier.weight(1f))
        Button(
            onClick = onComplete,
            enabled = isNameValid && !isLoading,
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen, disabledContainerColor = PrimaryGreen.copy(alpha = 0.5f)),
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
                    .clip(MaterialTheme.shapes.small)
                    .background(color = if (i <= currentStep) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
            )
        }
    }
}

@Preview(showBackground = true, name = "Register Screen Preview (Step 1)")
@Composable
private fun RegisterScreenPreview() {
    FurrEverCareTheme {
        Surface {
            RegisterStep1(email = "test@example.com", onEmailChange = {}, isEmailValid = true, onNext = {}, isLoading = false)
        }
    }
}

@Preview(showBackground = true, name = "Step 2 (Password) Preview")
@Composable
private fun RegisterStep3Preview() {
    FurrEverCareTheme { Surface { RegisterStep3("password", {}, "password", {}, true, true, {}, false) } }
}

@Preview(showBackground = true, name = "Step 3 (Name) Preview")
@Composable
private fun RegisterStep4Preview() {
    FurrEverCareTheme { Surface { RegisterStep4("John", {}, "Doe", {}, true, {}, false) } }
}