package com.jis_citu.furrevercare.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface // Use Surface for themed background
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jis_citu.furrevercare.R
import com.jis_citu.furrevercare.navigation.Routes
import com.jis_citu.furrevercare.theme.FurrEverCareTheme
// Removed direct imports of Background and PrimaryGreen as we'll use MaterialTheme.colorScheme

@Composable
fun VerificationSuccessScreen(navController: NavController) {
    // Use Surface to get the correct background color from the theme
    // and ensure proper text color contrast by default for its children.
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background // Use theme background color
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.verification_success),
                contentDescription = "Verification Success",
                modifier = Modifier
                    .size(300.dp) // You can adjust this size
                    .padding(bottom = 24.dp),
                contentScale = ContentScale.Fit
            )

            Text(
                text = "Paw-some!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground, // Explicitly use onBackground
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "You're Signed Up!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground, // Explicitly use onBackground
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "Welcome to FurrEverCare, your one-stop solution for all your pet care needs.", // Made text slightly shorter for readability
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f), // Slightly less emphasis
                modifier = Modifier.padding(horizontal = 32.dp, vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.WELCOME_AUTH) { inclusive = true }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary, // Use theme primary color
                    contentColor = MaterialTheme.colorScheme.onPrimary  // Ensure text on button is contrasting
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .height(48.dp) // Consistent button height
            ) {
                Text(text = "Let's Go")
            }
        }
    }
}

@Preview(showBackground = true, name = "Verification Success Light")
@Composable
fun VerificationSuccessScreenLightPreview() {
    FurrEverCareTheme(darkTheme = false) { // Explicitly light theme
        VerificationSuccessScreen(rememberNavController())
    }
}

@Preview(showBackground = true, name = "Verification Success Dark")
@Composable
fun VerificationSuccessScreenDarkPreview() {
    FurrEverCareTheme(darkTheme = true) { // Explicitly dark theme
        VerificationSuccessScreen(rememberNavController())
    }
}