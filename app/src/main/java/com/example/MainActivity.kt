package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.screens.MainAppScaffold
import com.example.ui.theme.DarkBgPrimary
import com.example.ui.theme.RizzXTheme
import com.example.ui.viewmodel.AppDestination
import com.example.ui.viewmodel.RizzViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: RizzViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val profile by viewModel.userProfile.collectAsState()

            LaunchedEffect(profile.hasCompletedOnboarding) {
                if (!profile.hasCompletedOnboarding) {
                    viewModel.navigateTo(AppDestination.ONBOARDING)
                }
            }

            RizzXTheme(darkTheme = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DarkBgPrimary
                ) {
                    MainAppScaffold(viewModel = viewModel)
                }
            }
        }
    }
}
