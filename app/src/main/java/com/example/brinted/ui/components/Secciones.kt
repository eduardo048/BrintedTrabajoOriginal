package com.example.brinted.ui.components
//Esta clase contiene componentes reutilizables para secciones de la app como títulos, items de partidas y noticias.

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.brinted.data.model.*
import com.example.brinted.ui.theme.*

@Composable
// Componente para mostrar el título de una sección con estilo personalizado.
fun SeccionTitulo(titulo: String, modifier: Modifier = Modifier) { // Título de la sección
    Text( // Texto del título
        text = titulo, // Contenido del texto
        style = Tipografia.titleLarge, // Estilo del texto
        modifier = modifier.padding(vertical = 8.dp), // Modificador con padding vertical
        color = Color.White // Color del texto
    )
}

@Composable

// Componente para mostrar un item de partida con detalles como campeón, KDA, duración y resultado.
fun PartidaItem(partida: PartidaResumen, onClick: (PartidaResumen) -> Unit = {}) { // Detalles de la partida y función onClick
    Card( // Tarjeta para mostrar la información de la partida
        shape = RoundedCornerShape(20.dp), // Forma redondeada de la tarjeta
        colors = CardDefaults.cardColors(containerColor = FondoElevado), // Colores de la tarjeta
        modifier = Modifier
            .fillMaxWidth() // Ancho completo
            .clickable { onClick(partida) } // Acción al hacer clic
    ) {
        Row( // Fila para organizar los elementos horizontalmente
            modifier = Modifier.padding(12.dp), // Padding alrededor del contenido
            verticalAlignment = Alignment.CenterVertically // Alineación vertical centrada
        ) {
            AsyncImage( // Imagen del campeón
                model = partida.icono, // Modelo de la imagen
                contentDescription = null, // Descripción de contenido nula
                modifier = Modifier.size(55.dp).clip(RoundedCornerShape(12.dp)), // Tamaño y forma de la imagen
                contentScale = ContentScale.Crop // Escala de contenido para recortar la imagen
            )
            Spacer(modifier = Modifier.width(16.dp)) // Espacio horizontal entre la imagen y el texto
            Column(modifier = Modifier.weight(1f)) { // Columna para organizar el texto verticalmente
                Text(partida.campeon, style = Tipografia.bodyLarge, fontWeight = FontWeight.Bold, color = Color.White) // Nombre del campeón
                Text("KDA: ${partida.kda} · ${partida.duracion}", style = Tipografia.bodySmall, color = GrisTexto) // KDA y duración de la partida
            }
            EtiquetaResultado(partida.resultado) // Etiqueta que muestra el resultado de la partida
        }
    }
}

@Composable
// Componente para mostrar una etiqueta que indica si la partida fue una victoria o derrota.
fun EtiquetaResultado(resultado: ResultadoPartida) { // Resultado de la partida
    val esVictoria = resultado == ResultadoPartida.VICTORIA // Verifica si el resultado es victoria
    val color = if (esVictoria) VerdeVictoria else RojoDerrota // Color basado en el resultado
    Surface( // Superficie para la etiqueta
        color = color.copy(alpha = 0.15f), // Color de fondo con transparencia
        shape = RoundedCornerShape(10.dp) // Forma redondeada de la etiqueta
    ) {
        Text( // Texto de la etiqueta
            text = if (esVictoria) "Victoria" else "Derrota", // Texto basado en el resultado
            color = color, // Color del texto
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), // Padding alrededor del texto
            style = Tipografia.labelSmall, // Estilo del texto
            fontWeight = FontWeight.Bold // Peso de fuente en negrita
        )
    }
}

@Composable
// Componente para mostrar una tarjeta de noticia deportiva con imagen, título, descripción y botón.
fun NoticiaCard(noticia: NoticiaEsport, onClick: (NoticiaEsport) -> Unit = {}) { // Detalles de la noticia y función onClick
    Card( // Tarjeta para mostrar la noticia
        shape = RoundedCornerShape(24.dp), // Forma redondeada de la tarjeta
        colors = CardDefaults.cardColors(containerColor = FondoElevado), // Colores de la tarjeta
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).clickable { onClick(noticia) } // Ancho completo con padding y acción al hacer clic
    ) {
        Column { // Columna para organizar los elementos verticalmente
            AsyncImage( // Imagen de la noticia
                model = noticia.imagen, // Modelo de la imagen
                contentDescription = null, // Descripción de contenido nula
                contentScale = ContentScale.Crop, // Escala de contenido para recortar la imagen
                modifier = Modifier.fillMaxWidth().height(160.dp) // Tamaño de la imagen
            )
            Column(modifier = Modifier.padding(16.dp)) { // Columna para el contenido textual con padding
                Text(noticia.titulo, style = Tipografia.headlineSmall, color = Color.White) // Título de la noticia
                Spacer(modifier = Modifier.height(6.dp)) // Espacio vertical entre el título y la descripción
                Text(noticia.descripcion, style = Tipografia.bodyMedium, color = GrisTexto, maxLines = 2, overflow = TextOverflow.Ellipsis) // Descripción de la noticia
                Spacer(modifier = Modifier.height(12.dp)) // Espacio vertical antes del botón
                BotonPrimario(texto = "Leer más", modifier = Modifier.height(40.dp)) { onClick(noticia) } // Botón para leer más sobre la noticia
            }
        }
    }
}
