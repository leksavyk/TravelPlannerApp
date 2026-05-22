package com.example.travelplanner.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Fingerprint
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
import com.example.travelplanner.data.local.dao.UserDao
import com.example.travelplanner.data.local.entity.UserEntity
import com.example.travelplanner.ui.viewmodel.SecurityViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BiometricLoginScreen(
    securityViewModel: SecurityViewModel,
    userDao: UserDao,
    savedUserId: String,
    onLoginSuccess: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? FragmentActivity
    val authState by securityViewModel.biometricAuthState.collectAsState()

    var savedUser by remember { mutableStateOf<UserEntity?>(null) }

    LaunchedEffect(savedUserId) {
        withContext(Dispatchers.IO) {
            savedUser = userDao.getUserById(savedUserId)
        }
    }

    LaunchedEffect(authState) {
        if (authState is BiometricAuthState.Success) {
            securityViewModel.resetAuthState()
            onLoginSuccess(savedUserId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Вхід по біометрії") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад до форми")
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
                imageVector = Icons.Default.Fingerprint,
                contentDescription = null,
                modifier = Modifier.size(90.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Ласкаво просимо назад!",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = savedUser?.email ?: "Завантаження акаунта...",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = {
                    if (activity != null) {
                        securityViewModel.triggerBiometricAuth(activity, "Підтвердження входу в акаунт")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Сканувати відбиток / Face ID", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onBackClick) {
                Text("Увійти за допомогою пароля", color = MaterialTheme.colorScheme.outline)
            }
        }
    }
}