package com.example.travelplanner.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.example.travelplanner.data.biometric.BiometricAuthState
import com.example.travelplanner.ui.viewmodel.SecurityViewModel

@Composable
fun AppLockScreen(
    viewModel: SecurityViewModel,
    onUnlockSuccess: () -> Unit
) {
    val context = LocalContext.current
    val authState by viewModel.biometricAuthState.collectAsState()

    BackHandler {
        /* Do nothing—the app is blocked */
    }

    // Automatically start the scan when the screen first appears
    LaunchedEffect(Unit) {
        val activity = context as? FragmentActivity
        activity?.let {
            viewModel.triggerBiometricAuth(it, "Підтвердіть особу для доступу до планів подорожей")
        }
    }

    // Monitor the authentication status
    LaunchedEffect(authState) {
        if (authState is BiometricAuthState.Success) {
            viewModel.handleAuthSuccess()
            onUnlockSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Заблоковано",
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "TravelPlanner заблоковано",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Ваші персональні маршрути знаходяться під захистом. Будь ласка, пройдіть автентифікацію",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = {
                    val activity = context as? FragmentActivity
                    activity?.let {
                        viewModel.triggerBiometricAuth(it, "Вхід у додаток")
                    }
                },
                modifier = Modifier.fillMaxWidth(0.7f)
            ) {
                Text("Увійти")
            }

            if (authState is BiometricAuthState.Failed) {
                Text(
                    text = (authState as BiometricAuthState.Failed).error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 16.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}