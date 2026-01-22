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

@Composable
fun BotonPrimario(texto: String, modifier: Modifier = Modifier, habilitado: Boolean = true, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        enabled = habilitado,
        colors = ButtonDefaults.buttonColors(containerColor = Morado, contentColor = Color.White)
    ) {
        Text(texto, style = Tipografia.labelLarge)
    }
}

@Composable
fun BotonSecundario(texto: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.Transparent, contentColor = Color.White)
    ) {
        Text(texto, style = Tipografia.labelLarge)
    }
}

@Composable
fun CampoFormulario(
    valor: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leading: @Composable (() -> Unit)? = null,
    esPassword: Boolean = false,
    modifier: Modifier = Modifier
) {
    val mostrar = remember { mutableStateOf(!esPassword) }
    OutlinedTextField(
        value = valor,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        label = { Text(label) },
        placeholder = { Text(placeholder, color = GrisTexto) },
        leadingIcon = leading,
        trailingIcon = if (esPassword) {
            {
                IconButton(onClick = { mostrar.value = !mostrar.value }) {
                    Icon(
                        imageVector = if (mostrar.value) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                        contentDescription = null,
                        tint = GrisTexto
                    )
                }
            }
        } else null,
        visualTransformation = if (mostrar.value) VisualTransformation.None else PasswordVisualTransformation(),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Morado,
            unfocusedIndicatorColor = GrisTexto.copy(alpha = 0.4f),
            focusedLabelColor = Morado,
            cursorColor = Morado,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectorRegion(
    regionSeleccionada: String,
    onRegionSelected: (String) -> Unit
) {
    val regiones = listOf(
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
    
    var expanded by remember { mutableStateOf(false) }
    val textoMostrar = regiones.find { it.first == regionSeleccionada }?.second ?: "Selecciona Región"

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedCard(
            onClick = { expanded = true },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.outlinedCardColors(
                containerColor = Color.Transparent,
                contentColor = Color.White
            ),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(GrisTexto.copy(alpha = 0.4f)))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(textoMostrar, color = Color.White)
                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Morado)
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .background(FondoElevado)
        ) {
            regiones.forEach { (id, nombre) ->
                DropdownMenuItem(
                    text = { Text(nombre, color = Color.White) },
                    onClick = {
                        onRegionSelected(id)
                        expanded = false
                    }
                )
            }
        }
    }
}
