package com.example.brinted.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.brinted.ui.components.BotonPrimario
import com.example.brinted.ui.components.BotonSecundario
import com.example.brinted.R

@Composable
fun BienvenidaScreen(
    onLogin: () -> Unit,
    onRegistro: () -> Unit
){
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // 1. Imagen de fondo a pantalla completa
        Image(
            painter = painterResource(id = R.drawable.brinted_splash),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // 2. Overlay para mejorar contraste (ajustado a 0.3f para que se vea más la imagen)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
        )

        // 3. Contenido (solo botones ahora)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 24.dp, vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))
            
            // Hemos quitado la Card con el texto "BRINTED" para que se vea la imagen limpia
            
            BotonPrimario(
                texto = "Iniciar sesión",
                modifier = Modifier.fillMaxWidth()
            ) { onLogin() }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            BotonSecundario(
                texto = "Crear cuenta",
                modifier = Modifier.fillMaxWidth()
            ) { onRegistro() }
            
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
