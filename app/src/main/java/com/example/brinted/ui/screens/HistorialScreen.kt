package com.example.brinted.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items as listItems
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.brinted.data.model.PartidaResumen
import com.example.brinted.data.model.ResultadoPartida
import com.example.brinted.ui.theme.Fondo
import com.example.brinted.ui.theme.GrisTexto
import com.example.brinted.ui.theme.Morado
import com.example.brinted.ui.theme.RojoDerrota
import com.example.brinted.ui.theme.Tipografia
import com.example.brinted.ui.theme.VerdeVictoria
import androidx.compose.ui.layout.ContentScale

// Pantalla de historial de partidas que muestra una lista de partidas con sus detalles
@Composable
fun HistorialScreen( // Pantalla de historial de partidas
    partidas: List<PartidaResumen>, // Lista de partidas a mostrar
    cargando: Boolean, // Indicador de carga de datos
    onClickPartida: (PartidaResumen) -> Unit // Acción a realizar al hacer clic en una partida
) {
    LazyColumn( // Lista perezosa para el contenido
        modifier = Modifier // Aplicación del modificador
            .fillMaxSize() // Ocupa todo el espacio disponible
            .background(Fondo) // Fondo de la pantalla
            .padding(16.dp), // Espaciado interno
        verticalArrangement = Arrangement.spacedBy(14.dp) // Espaciado vertical entre elementos
    ) {
        item { // Información de la pantalla
            Text("Historial", style = Tipografia.headlineMedium, color = Color.White) // Título de la pantalla
            Text("Analiza tus últimas partidas", style = Tipografia.bodyMedium, color = GrisTexto, fontStyle = FontStyle.Italic) // Subtítulo de la pantalla
        }
        if (cargando) { // Si está cargando datos
            item { // Indicador de carga
                Box( // Contenedor centrado
                    modifier = Modifier // Aplicación del modificador
                        .fillMaxWidth() // Ocupa todo el ancho disponible
                        .padding(16.dp), // Espaciado interno
                    contentAlignment = Alignment.Center // Alineación vertical y horizontal centrada
                ) {
                    CircularProgressIndicator(color = Color.White) // Indicador de carga
                }
            }
        }
        listItems(partidas) { partida -> // Itera sobre cada partida
            HistorialCard(partida, onClickPartida) // Componente de tarjeta de partida
        }
        item { Spacer(modifier = Modifier.height(32.dp)) } // Espacio inferior
    }
}

@Composable
private fun HistorialCard(partida: PartidaResumen, onClick: (PartidaResumen) -> Unit) { // Componente de tarjeta de partida
    Card(
        shape = RoundedCornerShape(18.dp), // Bordes redondeados
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1521)), // Color de fondo de la tarjeta
        modifier = Modifier // Aplicación del modificador
            .fillMaxWidth() // Ocupa todo el ancho disponible
            .padding(vertical = 4.dp) // Espaciado vertical
            .background( // Fondo con degradado
                Brush.linearGradient( // Degradado lineal
                    listOf(Color(0x337C3AED), Color(0x11000000)) // Colores del degradado
                ),
                shape = RoundedCornerShape(20.dp) // Bordes redondeados
            )
            .clickable { onClick(partida) } // Acción al hacer clic en la tarjeta
    ) {
        Row( // Fila para el contenido
            modifier = Modifier // Aplicación del modificador
                .padding(14.dp), // Espaciado interno
            verticalAlignment = Alignment.CenterVertically, // Alineación vertical centrada
            horizontalArrangement = Arrangement.spacedBy(14.dp) // Espaciado horizontal entre elementos
        ) {
            Box( // Contenedor para la imagen
                modifier = Modifier // Aplicación del modificador
                    .size(72.dp) // Tamaño fijo
                    .clip(RoundedCornerShape(18.dp)) // Bordes redondeados
                    .background( // Fondo con degradado
                        Brush.linearGradient( // Degradado lineal
                            listOf(Color(0x4D7C3AED), Color(0x2626C6DA)) // Colores del degradado
                        )
                    )
                    .padding(3.dp) // Espaciado interno
            ) {
                AsyncImage( // Imagen de la partida
                    model = partida.icono, // URL de la imagen
                    contentDescription = partida.campeon, // Descripción para accesibilidad
                    contentScale = ContentScale.Crop, // Escala la imagen para cubrir el área
                    modifier = Modifier // Aplicación del modificador
                        .fillMaxSize() // Ocupa todo el espacio
                        .clip(RoundedCornerShape(15.dp)) // Bordes redondeados
                        .background(Color(0xFF0C1220)) // Color de fondo
                )
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) { // Columna para el contenido
                Row( // Fila para el nombre del campeón y el resultado
                    modifier = Modifier.fillMaxWidth(), // Ocupa todo el ancho disponible
                    verticalAlignment = Alignment.CenterVertically, // Alineación vertical centrada
                    horizontalArrangement = Arrangement.SpaceBetween // Espaciado horizontal entre elementos
                ) {
                    Text(partida.campeon, style = Tipografia.bodyLarge, fontWeight = FontWeight.Bold, color = Color.White) // Nombre del campeón
                    ResultadoChip(partida.resultado) // Chip de resultado
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) { // Fila para las métricas
                    BadgeTexto("KDA ${partida.kda}") // Métricas
                    BadgeTexto(partida.duracion) // Métricas
                }
                Text(partida.hace, style = Tipografia.bodyMedium, color = GrisTexto) // Texto de hace
            }
        }
    }
}

@Composable
private fun ResultadoChip(resultado: ResultadoPartida) { // Componente de chip de resultado
    val color = if (resultado == ResultadoPartida.VICTORIA) VerdeVictoria else RojoDerrota // Color del chip
    val texto = if (resultado == ResultadoPartida.VICTORIA) "Victoria" else "Derrota" // Texto del chip
    Text( // Texto del chip
        texto, // Texto a mostrar
        color = color, // Color del texto
        style = Tipografia.labelMedium, // Estilo del texto
        modifier = Modifier // Aplicación del modificador
            .clip(RoundedCornerShape(12.dp)) // Bordes redondeados
            .background(color.copy(alpha = 0.18f)) // Fondo con bordes redondeados
            .padding(horizontal = 10.dp, vertical = 4.dp) // Espaciado interno
    )
}

@Composable
private fun BadgeTexto(texto: String) { // Componente de texto con borde
    Text( // Texto
        texto, // Texto a mostrar
        color = Color.White, // Color del texto
        style = Tipografia.labelMedium, // Estilo del texto
        modifier = Modifier // Aplicación del modificador
            .clip(RoundedCornerShape(10.dp)) // Bordes redondeados
            .background(Color(0xFF1A2434)) // Fondo con bordes redondeados
            .padding(horizontal = 10.dp, vertical = 4.dp) // Espaciado interno
    )
}
