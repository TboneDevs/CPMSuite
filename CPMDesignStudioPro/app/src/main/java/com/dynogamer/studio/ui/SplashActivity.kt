package com.dynogamer.studio.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        // Keep splash visible briefly, then launch main activity
        splashScreen.setKeepOnScreenCondition { false }

        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
