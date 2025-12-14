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
import com.example.brinted.data.mock.MockData
import com.example.brinted.data.model.PartidaResumen
import com.example.brinted.data.model.ResultadoPartida
import com.example.brinted.ui.theme.Fondo
import com.example.brinted.ui.theme.GrisTexto
import com.example.brinted.ui.theme.Morado
import com.example.brinted.ui.theme.RojoDerrota
import com.example.brinted.ui.theme.Tipografia
import com.example.brinted.ui.theme.VerdeVictoria
import androidx.compose.ui.layout.ContentScale

@Composable
fun HistorialScreen(
    partidas: List<PartidaResumen>,
    cargando: Boolean,
    onClickPartida: (PartidaResumen) -> Unit
) {
    val lista = if (partidas.isNotEmpty()) partidas else MockData.partidasDemo
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Fondo)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("Historial", style = Tipografia.headlineMedium, color = Color.White)
            Text("Analiza tus últimas partidas", style = Tipografia.bodyMedium, color = GrisTexto, fontStyle = FontStyle.Italic)
        }
        if (cargando) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            }
        }
        listItems(lista) { partida ->
            HistorialCard(partida, onClickPartida)
        }
        item { Spacer(modifier = Modifier.height(32.dp)) }
    }
}

@Composable
private fun HistorialCard(partida: PartidaResumen, onClick: (PartidaResumen) -> Unit) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1521)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(
                Brush.linearGradient(
                    listOf(Color(0x337C3AED), Color(0x11000000))
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onClick(partida) }
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0x4D7C3AED), Color(0x2626C6DA))
                        )
                    )
                    .padding(3.dp)
            ) {
                AsyncImage(
                    model = partida.icono,
                    contentDescription = partida.campeon,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(15.dp))
                        .background(Color(0xFF0C1220))
                )
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(partida.campeon, style = Tipografia.bodyLarge, fontWeight = FontWeight.Bold, color = Color.White)
                    ResultadoChip(partida.resultado)
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    BadgeTexto("KDA ${partida.kda}")
                    BadgeTexto(partida.duracion)
                }
                Text(partida.hace, style = Tipografia.bodyMedium, color = GrisTexto)
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
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}

@Composable
private fun BadgeTexto(texto: String) {
    Text(
        texto,
        color = Color.White,
        style = Tipografia.labelMedium,
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF1A2434))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}
