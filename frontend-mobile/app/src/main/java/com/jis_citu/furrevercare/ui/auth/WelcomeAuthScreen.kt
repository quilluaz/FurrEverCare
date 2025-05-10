package com.jis_citu.furrevercare.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jis_citu.furrevercare.R
import com.jis_citu.furrevercare.navigation.Routes
import com.jis_citu.furrevercare.theme.FurrEverCareTheme

@Composable
fun WelcomeAuthScreen(navController: NavController) {
    // Determine the correct logo based on the theme
    val logoDrawable = if (isSystemInDarkTheme()) {
        R.drawable.logo_icontext_light
    } else {
        R.drawable.logo_icontext_dark
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = logoDrawable), // Use theme-aware logo
                contentDescription = "FurrEverCare Logo",
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .padding(bottom = 32.dp), // Increased padding
                contentScale = ContentScale.Fit
            )

            Button(
                onClick = { navController.navigate(Routes.LOGIN) },
                // Use theme color for button
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .height(48.dp) // Consistent button height
            ) {
                // Text color is typically handled by ButtonDefaults (onPrimary)
                Text(text = "Login", style = MaterialTheme.typography.bodyLarge)
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = { navController.navigate(Routes.REGISTER) },
                // OutlinedButton defaults usually use primary color for text and border
                // colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary),
                // border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary), // Only if defaults aren't right
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .height(48.dp) // Consistent button height
            ) {
                // Text color defaults to button's content color (primary)
                Text(text = "Register", style = MaterialTheme.typography.bodyLarge)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Preview(showBackground = true, name = "WelcomeAuth Light")
@Composable
fun WelcomeAuthScreenLightPreview() {
    FurrEverCareTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            WelcomeAuthScreen(rememberNavController())
        }
    }
}

@Preview(showBackground = true, name = "WelcomeAuth Dark")
@Composable
fun WelcomeAuthScreenDarkPreview() {
    FurrEverCareTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            WelcomeAuthScreen(rememberNavController())
        }
    }
}