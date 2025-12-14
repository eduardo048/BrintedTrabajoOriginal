package com.example.brinted.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.brinted.ui.theme.Cian
import com.example.brinted.ui.theme.GrisTexto
import com.example.brinted.ui.theme.Morado
import com.example.brinted.ui.theme.Tipografia

@Composable
fun BotonPrimario(
    texto: String,
    modifier: Modifier = Modifier,
    habilitado: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        enabled = habilitado,
        colors = ButtonDefaults.buttonColors(
            containerColor = Morado,
            contentColor = Color.White
        )
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
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Transparent,
            contentColor = Color.White
        )
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
            disabledTextColor = Color.White.copy(alpha = 0.5f),
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent
        )
    )
}
