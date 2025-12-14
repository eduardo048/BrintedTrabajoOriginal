package com.example.brinted

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.brinted.ui.app.BrintedApp
import com.example.brinted.ui.theme.BrintedTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BrintedTheme {
                BrintedApp()
            }
        }
    }
}
