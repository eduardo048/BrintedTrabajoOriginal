package com.example.brinted.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.brinted.ui.components.BotonPrimario
import com.example.brinted.ui.components.CampoFormulario
import com.example.brinted.ui.components.SelectorRegion
import com.example.brinted.ui.theme.Fondo
import com.example.brinted.ui.theme.GrisTexto
import com.example.brinted.ui.theme.Tarjeta
import com.example.brinted.ui.theme.Tipografia

// Pantalla de registro de usuario
@Composable
fun RegistroScreen(
    cargando: Boolean, // Indica si se está procesando el registro
    error: String?, // Mensaje de error en caso de fallo
    onBack: () -> Unit, // Acción al pulsar el botón de volver
    onRegistro: (String, String, String, String) -> Unit, // Acción al registrar (correo, contraseña, invocador, región)
    onYaTengoCuenta: () -> Unit // Acción al indicar que ya se tiene cuenta
) {
    val correo = remember { mutableStateOf("") } // Estado para el campo de correo electrónico
    val contrasena = remember { mutableStateOf("") } // Estado para el campo de contraseña
    val invocador = remember { mutableStateOf("") } // Estado para el campo de nombre de invocador
    val region = remember { mutableStateOf("euw1") } // Estado para la región seleccionada, por defecto "euw1"

    Column( // Contenedor principal de la pantalla
        modifier = Modifier
            .fillMaxSize() // Ocupa todo el espacio disponible
            .background(Fondo) // Fondo de la pantalla
            .padding(horizontal = 20.dp, vertical = 12.dp) // Padding alrededor del contenido
    ) {
        IconButton(onClick = onBack) { // Botón para volver atrás
            Icon(Icons.Outlined.ArrowBack, contentDescription = "Atrás", tint = Color.White) // Icono de flecha hacia atrás
        }
        Spacer(modifier = Modifier.height(10.dp)) // Espacio vertical
        Text("Registrarse", style = Tipografia.headlineMedium) // Título de la pantalla
        Spacer(modifier = Modifier.height(16.dp)) // Espacio vertical
        Card( // Tarjeta que contiene el formulario de registro
            shape = RoundedCornerShape(18.dp), // Esquinas redondeadas de la tarjeta
            colors = CardDefaults.cardColors(containerColor = Tarjeta),// Color de fondo de la tarjeta
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp) // Sin elevación
        ) {
            Column(modifier = Modifier.padding(16.dp)) { // Contenido de la tarjeta con padding
                Text("Correo electrónico", style = Tipografia.bodyMedium, color = GrisTexto) // Etiqueta del campo de correo
                Spacer(modifier = Modifier.height(6.dp)) // Espacio vertical
                CampoFormulario( // Campo de texto para el correo electrónico
                    valor = correo.value, // Valor actual del campo
                    onValueChange = { correo.value = it }, // Actualiza el valor al cambiar
                    label = "Correo electrónico", // Etiqueta del campo
                    sugerencia = "tu.email@ejemplo.com", // Texto de ejemplo
                    iconoInicio = { Icon(Icons.Outlined.Email, null, tint = GrisTexto) } // Icono al inicio del campo
                )
                Spacer(modifier = Modifier.height(14.dp)) // Espacio vertical
                Text("Contraseña", style = Tipografia.bodyMedium, color = GrisTexto) // Etiqueta del campo de contraseña
                Spacer(modifier = Modifier.height(6.dp)) // Espacio vertical
                CampoFormulario( // Campo de texto para la contraseña
                    valor = contrasena.value, // Valor actual del campo
                    onValueChange = { contrasena.value = it }, // Actualiza el valor al cambiar
                    label = "Contraseña", // Etiqueta del campo
                    sugerencia = "••••••••", // Texto de ejemplo
                    iconoInicio = { Icon(Icons.Outlined.Lock, null, tint = GrisTexto) }, // Icono al inicio del campo
                    esPassword = true // Indica que es un campo de contraseña
                )
                Spacer(modifier = Modifier.height(14.dp)) // Espacio vertical
                Text("Nombre de usuario (Riot ID)", style = Tipografia.bodyMedium, color = GrisTexto) // Etiqueta del campo de invocador
                Spacer(modifier = Modifier.height(6.dp)) // Espacio vertical
                CampoFormulario( // Campo de texto para el nombre de invocador
                    valor = invocador.value, // Valor actual del campo
                    onValueChange = { invocador.value = it }, // Actualiza el valor al cambiar
                    label = "Tu nombre", // Etiqueta del campo
                    sugerencia = "Ej: Faker", // Texto de ejemplo
                    iconoInicio = { Icon(Icons.Outlined.Person, null, tint = GrisTexto) } // Icono al inicio del campo
                )
                Spacer(modifier = Modifier.height(14.dp)) // Espacio vertical
                Text("Región", style = Tipografia.bodyMedium, color = GrisTexto) // Etiqueta del selector de región
                Spacer(modifier = Modifier.height(6.dp)) // Espacio vertical
                SelectorRegion( // Componente para seleccionar la región
                    regionSeleccionada = region.value, // Región actualmente seleccionada
                    onRegionSelected = { region.value = it } // Actualiza la región al seleccionar una nueva
                )
                
                Spacer(modifier = Modifier.height(18.dp)) // Espacio vertical
                if (error != null) { // Si hay un mensaje de error, lo mostramos
                    Text(error, color = Color.Red, style = Tipografia.bodyMedium) // Texto del error en rojo
                    Spacer(modifier = Modifier.height(8.dp)) // Espacio vertical
                }
                
                BotonPrimario(texto = if (cargando) "Registrando..." else "Registrarse") { // Botón para registrar
                    if (!cargando) { // Solo permite registrar si no está cargando
                        // Juntamos el nombre con el tag según la región
                        val tag = when(region.value) { // Mapeo de región a tag
                            "euw1" -> "EUW"
                            "eun1" -> "EUNE"
                            "na1" -> "NA"
                            "br1" -> "BR"
                            "la1" -> "LAN"
                            "la2" -> "LAS"
                            "kr" -> "KR"
                            "jp1" -> "JP"
                            "tr1" -> "TR"
                            "ru" -> "RU"
                            "oc1" -> "OCE"
                            else -> region.value.uppercase() // Por defecto, usamos la región en mayúsculas
                        }
                        val riotIdCompleto = if (invocador.value.contains("#")) { // Si el nombre ya contiene un tag
                            invocador.value.trim()  // Usamos el valor tal cual
                        } else { // Si no contiene tag
                            "${invocador.value.trim()}#$tag" // Añadimos el tag correspondiente
                        }
                        
                        onRegistro(correo.value.trim(), contrasena.value, riotIdCompleto, region.value) // Llamamos a la acción de registro
                    }
                }
                Spacer(modifier = Modifier.height(12.dp)) // Espacio vertical
                Row( // Fila para la opción de "Ya tengo cuenta"
                    horizontalArrangement = Arrangement.Center, // Centrado horizontalmente
                    modifier = Modifier.fillMaxWidth() // Ocupa todo el ancho disponible
                ) {
                    Text("¿Ya tienes cuenta? ", color = GrisTexto, style = Tipografia.bodyMedium) // Texto informativo
                    Text( // Texto clicable para iniciar sesión
                        text = "Iniciar sesión", // Texto del enlace
                        color = Color(0xFFA060FF), // Color púrpura
                        style = Tipografia.bodyMedium, // Estilo del texto
                        modifier = Modifier.clickable { onYaTengoCuenta() } // Acción al hacer clic
                    )
                }
            }
        }
        if (cargando) { // Si está cargando, mostramos un indicador de progreso
            Spacer(modifier = Modifier.height(20.dp)) // Espacio vertical
            CircularProgressIndicator(color = Color.White, modifier = Modifier.align(Alignment.CenterHorizontally)) // Indicador de carga centrado
        }
    }
}
