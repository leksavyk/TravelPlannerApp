package com.example.travelplanner.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.example.travelplanner.data.biometric.BiometricAuthState
import com.example.travelplanner.ui.viewmodel.SecurityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CriticalActionScreen(
    viewModel: SecurityViewModel,
    onActionConfirmed: () -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? FragmentActivity
    val authState by viewModel.biometricAuthState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is BiometricAuthState.Success) {
            viewModel.resetAuthState()
            onActionConfirmed()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Підтвердження дії") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Скасувати")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.error
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Увага! Критична дія",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.error
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Ви збираєтеся видалити свій акаунт. Цю дію неможливо буде скасувати, а всі ваші подорожі буде втрачено",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Для продовження необхідно підтвердити особу за допомогою біометрії пристрою",
                style = Modifier.let { MaterialTheme.typography.bodyMedium },
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = {
                    if (activity != null) {
                        viewModel.triggerBiometricAuth(activity, "Підтвердження видалення акаунта")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Підтвердити через біометрію", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = onBackClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Скасувати дію", color = MaterialTheme.colorScheme.outline)
            }
        }
    }
}