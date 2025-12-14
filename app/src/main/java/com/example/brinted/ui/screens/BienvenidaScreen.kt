package com.example.brinted.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.example.brinted.ui.components.BotonPrimario
import com.example.brinted.ui.components.BotonSecundario
import com.example.brinted.ui.theme.GradienteInferior
import com.example.brinted.ui.theme.GradienteSuperior

@Composable
fun BienvenidaScreen(
    onLogin: () -> Unit,
    onRegistro: () -> Unit
){
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setStatusBarColor(GradienteSuperior, darkIcons = false)
        systemUiController.setNavigationBarColor(GradienteInferior, darkIcons = false)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        GradienteSuperior,
                        Color(0xFF6D5DF6),
                        GradienteInferior
                    )
                )
            )
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        // Glow shapes
        Box(
            modifier = Modifier
                .size(220.dp)
                .align(Alignment.TopEnd)
                .offset(x = 50.dp, y = (-40).dp)
                .background(Color.White.copy(alpha = 0.08f), shape = CircleShape)
        )
        Box(
            modifier = Modifier
                .size(320.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-80).dp, y = 80.dp)
                .background(Color.White.copy(alpha = 0.05f), shape = CircleShape)
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(0.8f))
            Card(
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.08f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 24.dp)
                ) {
                    Text(
                        text = "BRINTED",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
            Spacer(modifier = Modifier.height(28.dp))
            BotonPrimario(
                texto = "Iniciar sesión",
                modifier = Modifier.fillMaxWidth()
            ) { onLogin() }
            Spacer(modifier = Modifier.height(12.dp))
            BotonSecundario(
                texto = "Crear cuenta",
                modifier = Modifier.fillMaxWidth()
            ) { onRegistro() }
            Spacer(modifier = Modifier.weight(1.2f))
        }
    }
}
