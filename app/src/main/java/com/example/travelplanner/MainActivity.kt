package com.example.travelplanner

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.fragment.app.FragmentActivity
import com.example.travelplanner.data.biometric.BiometricManagerImpl
import com.example.travelplanner.ui.navigation.AppNavigation
import com.example.travelplanner.ui.screen.AppLockScreen
import com.example.travelplanner.ui.viewmodel.SecurityViewModel
import com.example.travelplanner.data.biometric.AppLockController
import com.example.travelplanner.data.local.SharedSecurityStorage

class MainActivity : FragmentActivity() {
    private lateinit var securityViewModel: SecurityViewModel
    private var showLockScreenState = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val securityStorage = SharedSecurityStorage(applicationContext)
        val biometricManager = BiometricManagerImpl(applicationContext, securityStorage)
        val appLockController = AppLockController(securityStorage)

        securityViewModel = SecurityViewModel(biometricManager, securityStorage, appLockController)

        showLockScreenState.value = securityViewModel.checkIfAppShouldLock()

        setContent {
            MaterialTheme {
                Surface {
                    val showLockScreen by showLockScreenState

                    Box(modifier = androidx.compose.ui.Modifier.fillMaxSize()) {
                        AppNavigation()

                        if (showLockScreen) {
                            AppLockScreen(
                                viewModel = securityViewModel,
                                onUnlockSuccess = {
                                    showLockScreenState.value = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (::securityViewModel.isInitialized) {
            // We check whether 30 seconds have passed since onStop() was called
            if (securityViewModel.checkIfAppShouldLock()) {
                showLockScreenState.value = true
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (::securityViewModel.isInitialized) {
            securityViewModel.appWentToBackground()
        }
    }
}
