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

//Pantalla de inicio de sesión con campos de correo y contraseña.
@Composable
//Muestra un indicador de carga y mensajes de error si es necesario.
fun LoginScreen(
    cargando: Boolean, // Indica si se está procesando el inicio de sesión
    error: String?, // Mensaje de error a mostrar, si lo hay
    onBack: () -> Unit, // Acción al presionar el botón de retroceso
    onLogin: (String, String) -> Unit, // Acción al intentar iniciar sesión con correo y contraseña
    onCrearCuenta: () -> Unit // Acción al presionar el enlace para crear una cuenta
) {
    val correo = remember { mutableStateOf("") } // Estado para el campo de correo electrónico
    val contrasena = remember { mutableStateOf("") } // Estado para el campo de contraseña

    // Estructura principal de la pantalla
    Column(
        modifier = Modifier
            .fillMaxSize() //Ocupa todo el espacio disponible
            .background(Fondo) // Fondo de la pantalla
            .padding(horizontal = 20.dp, vertical = 12.dp) // Espaciado interno
    ) {
        IconButton(onClick = onBack) { // Botón de retroceso
            Icon(Icons.Outlined.ArrowBack, contentDescription = "Atrás", tint = Color.White) // Icono de flecha hacia atrás
        }
        Spacer(modifier = Modifier.height(10.dp)) // Espacio vertical
        Text("Iniciar sesión", style = Tipografia.headlineMedium) // Título de la pantalla
        Spacer(modifier = Modifier.height(16.dp)) // Espacio vertical
        Card( // Tarjeta que contiene el formulario de inicio de sesión
            shape = RoundedCornerShape(18.dp), // Bordes redondeados
            colors = CardDefaults.cardColors(containerColor = Tarjeta), // Color de fondo de la tarjeta
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp) // Sin elevación
        ) {
            Column( // Columna dentro de la tarjeta
                modifier = Modifier
                    .padding(16.dp) // Espaciado interno
            ) {
                Text("Correo electrónico", style = Tipografia.bodyMedium, color = GrisTexto) // Etiqueta del campo de correo
                Spacer(modifier = Modifier.height(6.dp)) // Espacio vertical
                CampoFormulario( // Campo de entrada para el correo electrónico
                    valor = correo.value, // Valor actual del campo
                    onValueChange = { correo.value = it }, // Actualiza el valor al cambiar
                    label = "Correo electrónico", // Etiqueta del campo
                    placeholder = "tu.correo@ejemplo.com", // Texto de marcador de posición
                    leading = { Icon(Icons.Outlined.Email, null, tint = GrisTexto) } // Icono al inicio del campo
                )
                Spacer(modifier = Modifier.height(14.dp)) // Espacio vertical
                Text("Contraseña", style = Tipografia.bodyMedium, color = GrisTexto) // Etiqueta del campo de contraseña
                Spacer(modifier = Modifier.height(6.dp)) // Espacio vertical
                CampoFormulario( // Campo de entrada para la contraseña
                    valor = contrasena.value, // Valor actual del campo
                    onValueChange = { contrasena.value = it }, // Actualiza el valor al cambiar
                    label = "Contraseña", // Etiqueta del campo
                    placeholder = "••••••••", // Texto de marcador de posición
                    leading = { Icon(Icons.Outlined.Lock, null, tint = GrisTexto) }, // Icono al inicio del campo
                    esPassword = true // Indica que es un campo de contraseña
                )
                Spacer(modifier = Modifier.height(18.dp)) // Espacio vertical
                if (error != null) { // Si hay un mensaje de error, mostrarlo
                    Text(error, color = Color.Red, style = Tipografia.bodyMedium) // Texto del error en rojo
                    Spacer(modifier = Modifier.height(8.dp)) // Espacio vertical
                }
                BotonPrimario(texto = if (cargando) "Cargando..." else "Iniciar sesión") { // Botón para iniciar sesión
                    if (!cargando) { // Solo permite iniciar sesión si no está cargando
                        onLogin(correo.value.trim(), contrasena.value) // Llama a la función de inicio de sesión con los valores ingresados
                    }
                }
                Spacer(modifier = Modifier.height(12.dp)) // Espacio vertical
                Row( // Fila para el enlace de crear cuenta
                    horizontalArrangement = Arrangement.Center, // Centra el contenido horizontalmente
                    modifier = Modifier.fillMaxWidth() // Ocupa todo el ancho disponible
                ) {
                    Text("¿No tienes cuenta? ", color = GrisTexto, style = Tipografia.bodyMedium) // Texto informativo
                    Text( // Enlace para crear una cuenta
                        text = "Crear cuenta", // Texto del enlace
                        color = Color(0xFFA060FF), // Color púrpura
                        style = Tipografia.bodyMedium, // Estilo del texto
                        modifier = Modifier.clickable { onCrearCuenta() } // Acción al hacer clic en el enlace
                    )
                }
            }
        }
        if (cargando) { // Si está cargando, mostrar un indicador de progreso
            Spacer(modifier = Modifier.height(20.dp)) // Espacio vertical
            CircularProgressIndicator(color = Color.White, modifier = Modifier.align(Alignment.CenterHorizontally)) // Indicador de carga centrado
        }
    }
}
