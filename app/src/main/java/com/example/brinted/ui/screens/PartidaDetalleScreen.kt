package com.example.brinted.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.brinted.data.mock.MockData
import com.example.brinted.data.model.JugadorPartida
import com.example.brinted.data.model.PartidaDetalle
import com.example.brinted.data.model.ResultadoPartida
import com.example.brinted.ui.theme.Fondo
import com.example.brinted.ui.theme.FondoElevado
import com.example.brinted.ui.theme.GrisTexto
import com.example.brinted.ui.theme.Morado
import com.example.brinted.ui.theme.RojoDerrota
import com.example.brinted.ui.theme.Tipografia
import com.example.brinted.ui.theme.VerdeVictoria

/** Detalle de partida: muestra resumen de equipos, daño y métricas globales. */
@Composable
fun PartidaDetalleScreen(detalle: PartidaDetalle?, onBack: () -> Unit) {
    val data = detalle ?: MockData.detalleDemo

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Fondo)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Outlined.ArrowBack, contentDescription = "Volver", tint = Color.White)
                }
                Column {
                    Text("Detalle de Partida", style = Tipografia.headlineMedium, color = Color.White)
                    Text(data.campeonPrincipal, style = Tipografia.bodyMedium, color = GrisTexto)
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = FondoElevado)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = data.icono,
                        contentDescription = data.campeonPrincipal,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(16.dp))
                    )
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(data.campeonPrincipal, style = Tipografia.headlineSmall, color = Color.White)
                        Text("KDA ${data.kda}", style = Tipografia.bodyMedium, color = Color.White)
                        Text("Duracion ${data.duracion}", style = Tipografia.bodyMedium, color = GrisTexto)
                    }
                    ResultadoChip(data.resultado)
                }
            }
        }

        item { Text("Resumen de Equipos", style = Tipografia.headlineSmall, color = Color.White) }
        item { EquipoCard("Equipo Azul", data.equipoAzul, data.campeonPrincipal) }
        item { EquipoCard("Equipo Rojo", data.equipoRojo, data.campeonPrincipal) }

        item { Text("Resumen de la partida", style = Tipografia.headlineSmall, color = Color.White) }
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1826)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    data.metricasGlobales.forEach { metrica ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0x1AFFFFFF), shape = RoundedCornerShape(10.dp))
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(metrica.titulo, color = GrisTexto, style = Tipografia.bodyMedium)
                            Text(metrica.valor, color = Color.White, style = Tipografia.bodyMedium, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EquipoCard(nombre: String, jugadores: List<JugadorPartida>, invocador: String) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1826)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(nombre, style = Tipografia.bodyLarge, color = Color.White)
            jugadores.forEach { jugador ->
                val esInvocador = jugador.nombre.equals(invocador, ignoreCase = true)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                if (esInvocador) listOf(Morado.copy(alpha = 0.18f), Color.Transparent) else listOf(Color.Transparent, Color.Transparent)
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(jugador.nombre + if (esInvocador) " (Tu)" else "", color = Color.White, style = Tipografia.bodyMedium)
                        Text("${jugador.rol} · ${jugador.campeon} · KDA ${jugador.kda}", color = GrisTexto, style = Tipografia.bodyMedium)
                    }
                    Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text("Daño", color = GrisTexto, style = Tipografia.labelMedium)
                        Text(jugador.dano, color = Color.White, style = Tipografia.bodyMedium, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun ResultadoChip(resultado: ResultadoPartida) {
    val color = if (resultado == ResultadoPartida.VICTORIA) VerdeVictoria else RojoDerrota
    val texto = if (resultado == ResultadoPartida.VICTORIA) "Victoria" else "Derrota"
    Text(
        texto,
        color = color,
        style = Tipografia.labelMedium,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.18f))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    )
}
