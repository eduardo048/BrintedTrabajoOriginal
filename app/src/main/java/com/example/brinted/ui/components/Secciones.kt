package com.example.brinted.ui.components

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
fun SeccionTitulo(titulo: String, modifier: Modifier = Modifier) {
    Text(
        text = titulo,
        style = Tipografia.titleLarge,
        modifier = modifier.padding(vertical = 8.dp),
        color = Color.White
    )
}

@Composable
fun PartidaItem(partida: PartidaResumen, onClick: (PartidaResumen) -> Unit = {}) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = FondoElevado),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(partida) }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = partida.icono,
                contentDescription = null,
                modifier = Modifier.size(55.dp).clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(partida.campeon, style = Tipografia.bodyLarge, fontWeight = FontWeight.Bold, color = Color.White)
                Text("KDA: ${partida.kda} · ${partida.duracion}", style = Tipografia.bodySmall, color = GrisTexto)
            }
            EtiquetaResultado(partida.resultado)
        }
    }
}

@Composable
fun EtiquetaResultado(resultado: ResultadoPartida) {
    val esVictoria = resultado == ResultadoPartida.VICTORIA
    val color = if (esVictoria) VerdeVictoria else RojoDerrota
    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(10.dp)
    ) {
        Text(
            text = if (esVictoria) "Victoria" else "Derrota",
            color = color,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = Tipografia.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun NoticiaCard(noticia: NoticiaEsport, onClick: (NoticiaEsport) -> Unit = {}) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = FondoElevado),
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).clickable { onClick(noticia) }
    ) {
        Column {
            AsyncImage(
                model = noticia.imagen,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().height(160.dp)
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(noticia.titulo, style = Tipografia.headlineSmall, color = Color.White)
                Spacer(modifier = Modifier.height(6.dp))
                Text(noticia.descripcion, style = Tipografia.bodyMedium, color = GrisTexto, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.height(12.dp))
                BotonPrimario(texto = "Leer más", modifier = Modifier.height(40.dp)) { onClick(noticia) }
            }
        }
    }
}
