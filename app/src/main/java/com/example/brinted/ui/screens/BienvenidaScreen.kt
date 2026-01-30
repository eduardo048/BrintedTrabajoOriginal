package com.example.brinted.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.brinted.ui.components.BotonPrimario
import com.example.brinted.ui.components.BotonSecundario
import com.example.brinted.R

// Pantalla de bienvenida con opciones para iniciar sesión o registrarse
// mostrando una imagen de fondo.
// Los botones llaman a las funciones proporcionadas cuando se presionan.

@Composable
fun BienvenidaScreen( // Funciones de callback para los botones
    onLogin: () -> Unit, // Función llamada al presionar "Iniciar sesión"
    onRegistro: () -> Unit // Función llamada al presionar "Crear cuenta"
){
    Box( // Contenedor principal que llena toda la pantalla
        modifier = Modifier.fillMaxSize() // Ocupa todo el espacio disponible
    ) {
        Image( // Imagen de fondo que cubre toda la pantalla
            painter = painterResource(id = R.drawable.brinted_splash), // Recurso de imagen
            contentDescription = null, // Descripción de accesibilidad (nula en este caso)
            modifier = Modifier.fillMaxSize(), // Ocupa todo el espacio disponible
            contentScale = ContentScale.Crop // Escala la imagen para cubrir el área sin distorsión
        )

        Column( // Contenedor vertical para los botones
            modifier = Modifier
                .fillMaxSize() // Ocupa todo el espacio disponible
                .systemBarsPadding() // Añade padding para evitar las barras del sistema
                .padding(horizontal = 24.dp, vertical = 40.dp), // Padding interno
            horizontalAlignment = Alignment.CenterHorizontally // Centra los elementos horizontalmente
        ) {
            Spacer(modifier = Modifier.weight(1f)) // Espaciador flexible para empujar los botones hacia abajo
            
            BotonPrimario( // Botón primario para iniciar sesión
                texto = "Iniciar sesión", // Texto del botón
                modifier = Modifier.fillMaxWidth() // Ocupa todo el ancho disponible
            ) { onLogin() } // Llama a la función onLogin al presionar
            
            Spacer(modifier = Modifier.height(16.dp)) // Espacio entre los botones
            
            BotonSecundario( // Botón secundario para crear cuenta
                texto = "Crear cuenta", // Texto del botón
                modifier = Modifier.fillMaxWidth() // Ocupa todo el ancho disponible
            ) { onRegistro() } // Llama a la función onRegistro al presionar
            
            Spacer(modifier = Modifier.height(20.dp)) // Espacio inferior fijo
        }
    }
}
