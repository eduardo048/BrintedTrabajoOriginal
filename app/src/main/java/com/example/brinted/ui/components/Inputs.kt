package com.example.brinted.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.brinted.ui.theme.*

// Botón primario con estilo personalizado
@Composable
fun BotonPrimario(texto: String, modifier: Modifier = Modifier, habilitado: Boolean = true, onClick: () -> Unit) { // Botón primario con estilo personalizado
    Button( // Botón primario con estilo personalizado
        onClick = onClick, // Acción al hacer clic
        modifier = modifier.fillMaxWidth(), // Ocupa todo el ancho disponible
        shape = RoundedCornerShape(14.dp), // Esquinas redondeadas
        enabled = habilitado, // Habilitado o deshabilitado
        colors = ButtonDefaults.buttonColors(containerColor = Morado, contentColor = Color.White) // Colores personalizados
    ) {
        Text(texto, style = Tipografia.labelLarge) // Texto del botón con estilo personalizado
    }
}

// Botón secundario con estilo personalizado
@Composable
fun BotonSecundario(texto: String, modifier: Modifier = Modifier, onClick: () -> Unit) { // Botón secundario con estilo personalizado
    OutlinedButton( // Botón secundario con estilo personalizado
        onClick = onClick, // Acción al hacer clic
        modifier = modifier.fillMaxWidth(), // Ocupa todo el ancho disponible
        shape = RoundedCornerShape(14.dp), // Esquinas redondeadas
        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.Transparent, contentColor = Color.White) // Colores personalizados
    ) {
        Text(texto, style = Tipografia.labelLarge) // Texto del botón con estilo personalizado
    }
}

// Campo de formulario con soporte para contraseñas y personalización
@Composable
fun CampoFormulario( // Campo de formulario con soporte para contraseñas y personalización
    valor: String, // Valor actual del campo de texto
    onValueChange: (String) -> Unit, // Acción al cambiar el valor del campo
    label: String, // Etiqueta del campo de texto
    placeholder: String, // Texto de ejemplo del campo de texto
    leading: @Composable (() -> Unit)? = null, // Icono al inicio del campo de texto
    esPassword: Boolean = false, // Indica si el campo es una contraseña
    modifier: Modifier = Modifier // Modificador para personalizar el campo de texto
) {
    val mostrar = remember { mutableStateOf(!esPassword) } // Estado para mostrar u ocultar la contraseña
    OutlinedTextField( // Campo de texto con estilo personalizado
        value = valor, // Valor actual del campo de texto
        onValueChange = onValueChange, // Acción al cambiar el valor del campo
        modifier = modifier.fillMaxWidth(), // Ocupa todo el ancho disponible
        singleLine = true, // Campo de una sola línea
        shape = RoundedCornerShape(12.dp), // Esquinas redondeadas
        label = { Text(label) }, // Etiqueta del campo de texto
        placeholder = { Text(placeholder, color = GrisTexto) }, // Texto de ejemplo del campo de texto
        leadingIcon = leading, // Icono al inicio del campo de texto
        trailingIcon = if (esPassword) {    // Icono al final del campo de texto para mostrar/ocultar contraseña
            {
                IconButton(onClick = { mostrar.value = !mostrar.value }) { // Acción al hacer clic en el icono
                    Icon( // Icono para mostrar u ocultar la contraseña
                        imageVector = if (mostrar.value) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff, // Icono basado en el estado
                        contentDescription = null, // Descripción del contenido (no se usa aquí)
                        tint = GrisTexto // Color del icono
                    )
                }
            }
        } else null, // No hay icono al final si no es una contraseña
        visualTransformation = if (mostrar.value) VisualTransformation.None else PasswordVisualTransformation(), // Transformación visual para mostrar u ocultar la contraseña
        colors = TextFieldDefaults.colors( // Colores personalizados para el campo de texto
            focusedIndicatorColor = Morado, // Color del indicador cuando está enfocado
            unfocusedIndicatorColor = GrisTexto.copy(alpha = 0.4f), // Color del indicador cuando no está enfocado
            focusedLabelColor = Morado, // Color de la etiqueta cuando está enfocado
            cursorColor = Morado, // Color del cursor
            focusedTextColor = Color.White, // Color del texto cuando está enfocado
            unfocusedTextColor = Color.White, // Color del texto cuando no está enfocado
            focusedContainerColor = Color.Transparent, // Color del contenedor cuando está enfocado
            unfocusedContainerColor = Color.Transparent // Color del contenedor cuando no está enfocado
        )
    )
}


// Selector de región con menú desplegable
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectorRegion( // Selector de región con menú desplegable
    regionSeleccionada: String, // Región actualmente seleccionada
    onRegionSelected: (String) -> Unit // Acción al seleccionar una región
) {
    val regiones = listOf( // Lista de regiones disponibles
        "euw1" to "Europe West (EUW)",
        "eun1" to "Europe Nordic & East (EUNE)",
        "na1" to "North America (NA)",
        "kr" to "Korea (KR)",
        "br1" to "Brazil (BR)",
        "la1" to "Latin America North (LAN)",
        "la2" to "Latin America South (LAS)",
        "jp1" to "Japan (JP)",
        "tr1" to "Turkey (TR)",
        "ru" to "Russia (RU)",
        "oc1" to "Oceania (OCE)"
    )
    
    var expanded by remember { mutableStateOf(false) } // Estado para controlar si el menú desplegable está expandido
    val textoMostrar = regiones.find { it.first == regionSeleccionada }?.second ?: "Selecciona Región" // Texto a mostrar basado en la región seleccionada

    Box(modifier = Modifier.fillMaxWidth()) { // Contenedor para el selector de región
        OutlinedCard( // Tarjeta con borde para el selector de región
            onClick = { expanded = true }, // Acción al hacer clic para expandir el menú
            shape = RoundedCornerShape(12.dp), // Esquinas redondeadas
            colors = CardDefaults.outlinedCardColors( // Colores personalizados para la tarjeta
                containerColor = Color.Transparent, // Color de fondo transparente
                contentColor = Color.White // Color del contenido blanco
            ),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(GrisTexto.copy(alpha = 0.4f))) // Borde personalizado
        ) {
            Row( // Fila para el contenido de la tarjeta
                modifier = Modifier
                    .fillMaxWidth() // Ocupa todo el ancho disponible
                    .padding(16.dp), // Espaciado interno
                verticalAlignment = Alignment.CenterVertically, // Alineación vertical centrada
                horizontalArrangement = Arrangement.SpaceBetween // Espacio entre los elementos
            ) {
                Text(textoMostrar, color = Color.White) // Texto de la región seleccionada
                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Morado) // Icono de flecha desplegable
            }
        }

        DropdownMenu( // Menú desplegable para seleccionar la región
            expanded = expanded, // Estado de expansión del menú
            onDismissRequest = { expanded = false }, // Acción al cerrar el menú
            modifier = Modifier
                .fillMaxWidth(0.85f) // Ocupa el 85% del ancho disponible
                .background(FondoElevado) // Fondo del menú desplegable
        ) {
            regiones.forEach { (id, nombre) -> // Itera sobre las regiones para crear los elementos del menú
                DropdownMenuItem(  // Elemento del menú desplegable
                    text = { Text(nombre, color = Color.White) }, // Texto del elemento del menú
                    onClick = {  // Acción al hacer clic en el elemento del menú
                        onRegionSelected(id) // Acción al seleccionar una región
                        expanded = false // Cierra el menú desplegable
                    }
                )
            }
        }
    }
}
