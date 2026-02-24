package com.example.brinted.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.brinted.data.model.JugadorPartida
import com.example.brinted.data.model.PartidaDetalle
import com.example.brinted.data.model.ResultadoPartida
import com.example.brinted.ui.theme.*

// Pantalla de detalle de partida que muestra los detalles de una partida en particular
@Composable
fun PartidaDetalleScreen(detalle: PartidaDetalle, onBack: () -> Unit) { // Pantalla de detalle de partida
    LazyColumn( // Lista perezosa para el contenido
        modifier = Modifier.fillMaxSize().background(Fondo).padding(horizontal = 16.dp), // Aplicación del modificador
        verticalArrangement = Arrangement.spacedBy(16.dp) // Espaciado vertical entre elementos
    ) {
        item { // Barra de navegación
            Row( // Fila para el título y el botón de retroceso
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), // Espaciado interno
                verticalAlignment = Alignment.CenterVertically // Alineación vertical centrada
            ) {
                IconButton(onClick = onBack) { // Botón de retroceso
                    Icon(Icons.Outlined.ArrowBack, "Atrás", tint = Color.White) // Ícono de retroceso
                }
                Text("Resumen de Partida", style = Tipografia.headlineSmall, color = Color.White) // Título de la pantalla
            }
        }


        item {// Detalle de la partida
            Card( // Tarjeta para el detalle de la partida
                shape = RoundedCornerShape(24.dp), // Bordes redondeados
                colors = CardDefaults.cardColors(containerColor = FondoElevado) // Color de fondo de la tarjeta
            ) {
                Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) { // Fila para el contenido
                    AsyncImage( // Imagen del campeón
                        model = detalle.icono, // URL de la imagen
                        contentDescription = null, // Descripción para accesibilidad
                        modifier = Modifier.size(70.dp).clip(RoundedCornerShape(12.dp)), // Tamaño y bordes redondeados
                        contentScale = ContentScale.Crop // Escala la imagen para cubrir el área
                    )
                    Spacer(modifier = Modifier.width(16.dp)) // Espacio entre la imagen y el contenido
                    Column(modifier = Modifier.weight(1f)) { // Columna para el contenido
                        Text(detalle.campeonPrincipal, style = Tipografia.headlineSmall, color = Color.White) // Nombre del campeón
                        Text("Duración: ${detalle.duracion}", color = GrisTexto, style = Tipografia.bodySmall) // Duración de la partida
                    }
                    Column(horizontalAlignment = Alignment.End) { // Columna para el resultado y KDA
                        ResultadoChip(detalle.resultado) // Chip de resultado
                        Text(detalle.kda, style = Tipografia.headlineSmall, color = Color.White, fontWeight = FontWeight.Bold) // Texto de KDA
                    }
                }
            }
        }


        if (detalle.metricasGlobales.isNotEmpty()) { // Si hay métricas globales
            item { // Sección de métricas globales
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) { // Fila para las métricas globales
                    detalle.metricasGlobales.forEach { metrica -> // Itera sobre cada métrica global
                        Card( // Tarjeta para cada métrica global
                            modifier = Modifier.weight(1f), // Ocupa el mismo espacio
                            shape = RoundedCornerShape(16.dp), // Bordes redondeados
                            colors = CardDefaults.cardColors(containerColor = FondoElevado) // Color de fondo de la tarjeta
                        ) {
                            Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) { // Columna para el contenido
                                Text(metrica.titulo, style = Tipografia.labelSmall, color = GrisTexto) // Título de la métrica
                                Text(metrica.valor, style = Tipografia.titleMedium, color = Color.White, fontWeight = FontWeight.Bold) // Valor de la métrica
                            }
                        }
                    }
                }
            }
        }

        item { Text("Equipos y Rendimiento", style = Tipografia.titleLarge, color = Color.White) } // Título de la sección de equipos y rendimiento

        item { EquipoSection("Tus Aliados", detalle.aliados, true) } // Sección de aliados
        item { EquipoSection("Enemigos", detalle.enemigos, false) } // Sección de enemigos

        item { Spacer(modifier = Modifier.height(24.dp)) } // Espacio inferior
    }
}

@Composable
fun EquipoSection(titulo: String, jugadores: List<JugadorPartida>, esAliado: Boolean) { // Componente de sección de equipo
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) { // Columna para el contenido
        Text( // Texto del título
            text = titulo,  // Texto a mostrar
            color = if (esAliado) Morado else RojoDerrota,  // Color del texto
            style = Tipografia.labelLarge, // Estilo del texto
            fontWeight = FontWeight.Bold // Negrita
        )
        Card( // Tarjeta para el equipo
            shape = RoundedCornerShape(20.dp), // Bordes redondeados
            colors = CardDefaults.cardColors(containerColor = FondoElevado) // Color de fondo de la tarjeta
        ) {
            Column(modifier = Modifier.padding(8.dp)) { // Columna para el contenido
                jugadores.forEach { jugador -> // Itera sobre cada jugador
                    JugadorRow(jugador) // Componente de fila de jugador
                }
            }
        }
    }
}

@Composable
fun JugadorRow(jugador: JugadorPartida) { // Componente de fila de jugador
    Row( // Fila para el contenido
        modifier = Modifier // Aplicación del modificador
            .fillMaxWidth() // Ocupa todo el ancho disponible
            .padding(8.dp), // Espaciado interno
        verticalAlignment = Alignment.CenterVertically // Alineación vertical centrada
    ) {
        AsyncImage( // Imagen del jugador
            model = "https://ddragon.leagueoflegends.com/cdn/14.1.1/img/champion/${jugador.campeon}.png", // URL de la imagen
            contentDescription = null, // Descripción para accesibilidad
            modifier = Modifier // Aplicación del modificador
                .size(42.dp) // Tamaño fijo
                .clip(CircleShape) // Bordes redondeados
                .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape), // Borde blanco con transparencia
            contentScale = ContentScale.Crop // Escala la imagen para cubrir el área
        )
        Spacer(modifier = Modifier.width(12.dp)) // Espacio entre la imagen y el contenido
        Column(modifier = Modifier.weight(1f)) { // Columna para el contenido
            Text(jugador.nombre, style = Tipografia.bodyMedium, color = Color.White, maxLines = 1) // Nombre del jugador
            Text("${jugador.rol} · KDA ${jugador.kda}", style = Tipografia.bodySmall, color = GrisTexto) // Rol y KDA del jugador
        }
        Column(horizontalAlignment = Alignment.End) { // Columna para el contenido
            Text(jugador.dano, style = Tipografia.bodyMedium, color = Color.White, fontWeight = FontWeight.Bold) // Dano del jugador
            Text("Daño", style = Tipografia.labelSmall, color = GrisTexto) // Texto de daño
        }
    }
}

@Composable
private fun ResultadoChip(resultado: ResultadoPartida) { // Componente de chip de resultado
    val esVictoria = resultado == ResultadoPartida.VICTORIA // Indica si es victoria o derrota
    val color = if (esVictoria) VerdeVictoria else RojoDerrota // Color del chip
    Surface( // Superficie para el chip
        color = color.copy(alpha = 0.15f), // Fondo con transparencia
        shape = RoundedCornerShape(8.dp) // Bordes redondeados
    ) {
        Text( // Texto del chip
            text = if (esVictoria) "VICTORIA" else "DERROTA", // Texto a mostrar
            color = color, // Color del texto
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), // Espaciado interno
            style = Tipografia.labelSmall, // Estilo del texto
            fontWeight = FontWeight.Bold // Negrita
        )
    }
}
