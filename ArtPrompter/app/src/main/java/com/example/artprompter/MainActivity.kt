package com.example.artprompter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.artprompter.data.prefs.UserPreferencesManager
import com.example.artprompter.ui.AppViewModel
import com.example.artprompter.ui.navigation.AppNavigation
import com.example.artprompter.ui.theme.ArtPrompterTheme

class MainActivity : ComponentActivity() {

    private val appViewModel: AppViewModel by viewModels {
        AppViewModel.Factory(
            (application as ArtPrompterApplication).container.userPreferencesManager
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeMode by appViewModel.themeMode.collectAsState()
            val darkTheme = when (themeMode) {
                UserPreferencesManager.ThemeMode.LIGHT -> false
                UserPreferencesManager.ThemeMode.DARK -> true
                else -> isSystemInDarkTheme()
            }
            ArtPrompterTheme(darkTheme = darkTheme) {
                AppNavigation(appViewModel = appViewModel)
            }
        }
    }
}
