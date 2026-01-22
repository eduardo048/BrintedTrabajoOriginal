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

@Composable
fun PartidaDetalleScreen(detalle: PartidaDetalle, onBack: () -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(Fondo).padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Outlined.ArrowBack, "Atrás", tint = Color.White)
                }
                Text("Resumen de Partida", style = Tipografia.headlineSmall, color = Color.White)
            }
        }

        // Card Principal (Blitz Style)
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = FondoElevado)
            ) {
                Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = detalle.icono,
                        contentDescription = null,
                        modifier = Modifier.size(70.dp).clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(detalle.campeonPrincipal, style = Tipografia.headlineSmall, color = Color.White)
                        Text("Duración: ${detalle.duracion}", color = GrisTexto, style = Tipografia.bodySmall)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        ResultadoChip(detalle.resultado)
                        Text(detalle.kda, style = Tipografia.headlineSmall, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Métricas Globales (Novedad)
        if (detalle.metricasGlobales.isNotEmpty()) {
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    detalle.metricasGlobales.forEach { metrica ->
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = FondoElevado)
                        ) {
                            Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(metrica.titulo, style = Tipografia.labelSmall, color = GrisTexto)
                                Text(metrica.valor, style = Tipografia.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        item { Text("Equipos y Rendimiento", style = Tipografia.titleLarge, color = Color.White) }

        item { EquipoSection("Tus Aliados", detalle.aliados, true) }
        item { EquipoSection("Enemigos", detalle.enemigos, false) }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
fun EquipoSection(titulo: String, jugadores: List<JugadorPartida>, esAliado: Boolean) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = titulo, 
            color = if (esAliado) Morado else RojoDerrota, 
            style = Tipografia.labelLarge,
            fontWeight = FontWeight.Bold
        )
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = FondoElevado)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                jugadores.forEach { jugador ->
                    JugadorRow(jugador)
                }
            }
        }
    }
}

@Composable
fun JugadorRow(jugador: JugadorPartida) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = "https://ddragon.leagueoflegends.com/cdn/14.1.1/img/champion/${jugador.campeon}.png",
            contentDescription = null,
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(jugador.nombre, style = Tipografia.bodyMedium, color = Color.White, maxLines = 1)
            Text("${jugador.rol} · KDA ${jugador.kda}", style = Tipografia.bodySmall, color = GrisTexto)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(jugador.dano, style = Tipografia.bodyMedium, color = Color.White, fontWeight = FontWeight.Bold)
            Text("Daño", style = Tipografia.labelSmall, color = GrisTexto)
        }
    }
}

@Composable
private fun ResultadoChip(resultado: ResultadoPartida) {
    val esVictoria = resultado == ResultadoPartida.VICTORIA
    val color = if (esVictoria) VerdeVictoria else RojoDerrota
    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = if (esVictoria) "VICTORIA" else "DERROTA",
            color = color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = Tipografia.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}
