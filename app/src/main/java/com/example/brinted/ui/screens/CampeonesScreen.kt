package com.example.brinted.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.brinted.data.model.CampeonDetalle
import com.example.brinted.ui.theme.Fondo
import com.example.brinted.ui.theme.FondoElevado
import com.example.brinted.ui.theme.GrisTexto
import com.example.brinted.ui.theme.Tipografia
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush

// Pantalla que muestra la lista de campeones con sus detalles
// incluyendo imagen, nombre, tasa de victorias y partidas jugadas.

@Composable
fun CampeonesScreen( // Pantalla de Campeones
    campeones: List<CampeonDetalle>, // Lista de campeones a mostrar
    cargando: Boolean // Indicador de carga de datos
) {
    Column( // Contenedor principal
        modifier = Modifier  // Aplicación del modificador
            .fillMaxSize() // Ocupa todo el espacio disponible
            .background(Fondo) // Fondo de la pantalla
            .padding(16.dp) // Espaciado interno
    ) {
        Text("Campeones", style = Tipografia.headlineMedium, color = Color.White) // Título de la pantalla
        if (cargando) { // Si está cargando datos
            Spacer(modifier = Modifier.height(6.dp)) // Espaciado
            Text("Actualizando datos...", color = GrisTexto, style = Tipografia.bodyMedium) // Mensaje de carga
        }
        Spacer(modifier = Modifier.height(12.dp)) // Espaciado entre el título y la lista
        LazyVerticalGrid( // Lista de campeones en formato de cuadrícula
            columns = GridCells.Fixed(2), // Dos columnas
            verticalArrangement = Arrangement.spacedBy(12.dp), // Espaciado vertical entre elementos
            horizontalArrangement = Arrangement.spacedBy(12.dp), // Espaciado horizontal entre elementos
            modifier = Modifier.fillMaxSize() // Ocupa todo el espacio disponible
        ) {
            items(campeones) { campeon -> // Itera sobre cada campeón
                Card( // Tarjeta para cada campeón
                    shape = RoundedCornerShape(18.dp), // Bordes redondeados
                    colors = CardDefaults.cardColors(containerColor = FondoElevado) // Color de fondo de la tarjeta
                ) {
                    Column { // Contenedor vertical dentro de la tarjeta
                        Box( // Contenedor para la imagen y el texto superpuesto
                            modifier = Modifier // Aplicación del modificador
                                .fillMaxWidth() // Ocupa todo el ancho disponible
                                .height(150.dp) // Altura fija para la imagen
                        ) {
                            AsyncImage( // Imagen del campeón
                                model = campeon.imagen, // URL de la imagen
                                contentDescription = campeon.nombre, // Descripción para accesibilidad
                                contentScale = ContentScale.Crop, // Escala la imagen para cubrir el área
                                modifier = Modifier.fillMaxSize() // Ocupa todo el espacio del contenedor
                            )
                            Box( // Capa de degradado para mejorar la legibilidad del texto
                                modifier = Modifier // Aplicación del modificador
                                    .matchParentSize() // Ocupa todo el espacio del contenedor
                                    .background( // Fondo con degradado
                                        Brush.verticalGradient( // Degradado vertical
                                            listOf(Color.Transparent, Color(0xB3000000)) // De transparente
                                        )
                                    )
                            )
                            Row( // Fila para la tasa de victorias
                                modifier = Modifier  // Aplicación del modificador
                                    .padding(10.dp) // Espaciado interno
                                    .fillMaxWidth(), // Ocupa todo el ancho disponible
                                horizontalArrangement = Arrangement.Start, // Alineación al inicio
                                verticalAlignment = Alignment.Top // Alineación en la parte superior
                            ) {
                                ChipTexto("WR ${campeon.winRate}%", color = Color.White, fondo = Color(0x8022C55E)) // Chip de tasa de victorias
                            }
                            Column( // Columna para el nombre del campeón
                                modifier = Modifier // Aplicación del modificador
                                    .align(Alignment.BottomStart) // Alineado en la parte inferior izquierda
                                    .padding(12.dp) // Espaciado interno
                            ) {
                                Text( // Nombre del campeón
                                    campeon.nombre, // Texto del nombre
                                    style = Tipografia.headlineSmall, // Estilo del texto
                                    maxLines = 1, // Máximo una línea
                                    overflow = TextOverflow.Ellipsis, // Elipsis si el texto es demasiado largo
                                    color = Color.White // Color del texto
                                )
                            }
                        }
                        Row( // Fila para las partidas jugadas
                            modifier = Modifier  // Aplicación del modificador
                                .fillMaxWidth() // Ocupa todo el ancho disponible
                                .padding(horizontal = 12.dp, vertical = 10.dp), // Espaciado interno
                            horizontalArrangement = Arrangement.SpaceBetween, // Espacio entre los elementos
                            verticalAlignment = Alignment.CenterVertically // Alineación vertical centrada
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) { // Columna para el texto de partidas jugadas
                                Text("Partidas jugadas", style = Tipografia.bodyMedium, color = GrisTexto) // Texto descriptivo
                                Text("${campeon.partidas}", style = Tipografia.bodyLarge, color = Color.White) // Número de partidas jugadas
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChipTexto( // Composable para un chip de texto estilizado
    texto: String, // Texto a mostrar en el chip
    color: Color = Color.White, // Color del texto
    fondo: Color = color.copy(alpha = 0.16f), // Color de fondo del chip
    modifier: Modifier = Modifier, // Modificador para personalización adicional
    maxLines: Int = 1 // Máximo de líneas para el texto
) {
    Text( // Texto del chip
        texto, // Texto a mostrar
        color = color, // Color del texto
        style = Tipografia.labelMedium, // Estilo del texto
        maxLines = maxLines, // Máximo de líneas
        overflow = TextOverflow.Ellipsis, // Elipsis si el texto es demasiado largo
        modifier = modifier // Aplicación del modificador
            .background(fondo, shape = RoundedCornerShape(12.dp)) // Fondo con bordes redondeados
            .padding(horizontal = 10.dp, vertical = 6.dp) // Espaciado interno
    )
}
