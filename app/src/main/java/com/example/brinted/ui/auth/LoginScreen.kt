package com.example.brinted.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.brinted.ui.components.BotonPrimario
import com.example.brinted.ui.components.CampoFormulario
import com.example.brinted.ui.theme.Fondo
import com.example.brinted.ui.theme.GrisTexto
import com.example.brinted.ui.theme.Tarjeta
import com.example.brinted.ui.theme.Tipografia

/** Pantalla de inicio de sesión con campos de correo y contraseña. */
@Composable
fun LoginScreen(
    cargando: Boolean,
    error: String?,
    onBack: () -> Unit,
    onLogin: (String, String) -> Unit,
    onCrearCuenta: () -> Unit
) {
    val correo = remember { mutableStateOf("") }
    val contrasena = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Fondo)
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.Outlined.ArrowBack, contentDescription = "Atrás", tint = Color.White)
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text("Iniciar sesión", style = Tipografia.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Tarjeta),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text("Correo electrónico", style = Tipografia.bodyMedium, color = GrisTexto)
                Spacer(modifier = Modifier.height(6.dp))
                CampoFormulario(
                    valor = correo.value,
                    onValueChange = { correo.value = it },
                    label = "Correo electrónico",
                    placeholder = "tu.correo@ejemplo.com",
                    leading = { Icon(Icons.Outlined.Email, null, tint = GrisTexto) }
                )
                Spacer(modifier = Modifier.height(14.dp))
                Text("Contraseña", style = Tipografia.bodyMedium, color = GrisTexto)
                Spacer(modifier = Modifier.height(6.dp))
                CampoFormulario(
                    valor = contrasena.value,
                    onValueChange = { contrasena.value = it },
                    label = "Contraseña",
                    placeholder = "••••••••",
                    leading = { Icon(Icons.Outlined.Lock, null, tint = GrisTexto) },
                    esPassword = true
                )
                Spacer(modifier = Modifier.height(18.dp))
                if (error != null) {
                    Text(error, color = Color.Red, style = Tipografia.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                BotonPrimario(texto = if (cargando) "Cargando..." else "Iniciar sesión") {
                    if (!cargando) {
                        onLogin(correo.value.trim(), contrasena.value)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("¿No tienes cuenta? ", color = GrisTexto, style = Tipografia.bodyMedium)
                    Text(
                        text = "Crear cuenta",
                        color = Color(0xFFA060FF),
                        style = Tipografia.bodyMedium,
                        modifier = Modifier.clickable { onCrearCuenta() }
                    )
                }
            }
        }
        if (cargando) {
            Spacer(modifier = Modifier.height(20.dp))
            CircularProgressIndicator(color = Color.White, modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    }
}
