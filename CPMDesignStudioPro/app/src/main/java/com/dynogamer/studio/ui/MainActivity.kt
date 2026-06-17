package com.dynogamer.studio.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.dynogamer.studio.ui.navigation.AppNavigation
import com.dynogamer.studio.ui.theme.CPMStudioTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CPMStudioTheme {
                AppNavigation()
            }
        }
    }
}
