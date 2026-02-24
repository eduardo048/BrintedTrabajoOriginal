package com.example.brinted

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.brinted.ui.app.BrintedApp
import com.example.brinted.ui.theme.BrintedTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen() // Configura el splash screen
        enableEdgeToEdge() // Habilita el modo de bordes para la actividad
        super.onCreate(savedInstanceState) // Llama al método onCreate de la superclase
        setContent { // Establece el contenido de la actividad
            BrintedTheme { // Utiliza el tema personalizado
                BrintedApp() // Componente principal de la aplicación
            }
        }
    }
}
