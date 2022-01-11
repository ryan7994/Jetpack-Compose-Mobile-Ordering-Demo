package com.ryanjames.jetpackmobileordering.core

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ryanjames.jetpackmobileordering.ui.theme.AppTheme
import com.ryanjames.jetpackmobileordering.ui.theme.MyComposeAppTheme
import dagger.hilt.android.AndroidEntryPoint

abstract class BaseActivity: ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyComposeAppTheme {
                // ToDo: Replace if directly supported by Jetpack Compose
                val systemUiController = rememberSystemUiController()
                val statusBarColor = AppTheme.colors.materialColors.primary
                SideEffect {
                    systemUiController.setSystemBarsColor(statusBarColor, darkIcons = false)
                }

                SetContent()
            }
        }
    }

    @Composable
    abstract fun SetContent()

}