package com.jis_citu.furrevercare.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
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
import com.jis_citu.furrevercare.theme.Background
import com.jis_citu.furrevercare.theme.FurrEverCareTheme
import com.jis_citu.furrevercare.theme.PrimaryGreen

@Composable
fun WelcomeAuthScreen(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_icontext_colored),
                contentDescription = "FurrEverCare Logo",
                modifier = Modifier
                    .size(300.dp)
                    .padding(bottom = 16.dp),
                contentScale = ContentScale.Fit
            )

            Button(
                onClick = { navController.navigate(Routes.LOGIN) },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            ) {
                Text(text = "Login")
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = { navController.navigate(Routes.REGISTER) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            ) {
                Text(text = "Register")
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeAuthScreenPreview() {
    FurrEverCareTheme {
        WelcomeAuthScreen(rememberNavController())
    }
}
