package com.example.brinted.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
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
import com.example.brinted.data.model.CampeonDetalle
import com.example.brinted.data.model.NoticiaEsport
import com.example.brinted.data.model.PartidaResumen
import com.example.brinted.data.model.ResultadoPartida
import com.example.brinted.ui.theme.FondoElevado
import com.example.brinted.ui.theme.GrisTexto
import com.example.brinted.ui.theme.Morado
import com.example.brinted.ui.theme.RojoDerrota
import com.example.brinted.ui.theme.Tarjeta
import com.example.brinted.ui.theme.Tipografia
import com.example.brinted.ui.theme.VerdeVictoria

/** Componentes reutilizables: secciones, tarjetas de stats, partidas, campeones y noticias. */
@Composable
fun SeccionTitulo(titulo: String, modifier: Modifier = Modifier) {
    Text(
        text = titulo,
        style = Tipografia.headlineSmall,
        modifier = modifier.padding(vertical = 8.dp),
        color = Color.White
    )
}

@Composable
fun TarjetaEstadistica(titulo: String, valor: String, secundario: String? = null) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Tarjeta),
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(titulo, style = Tipografia.bodyMedium, color = GrisTexto)
            Spacer(modifier = Modifier.height(6.dp))
            Text(valor, style = Tipografia.headlineMedium, color = Color.White)
            secundario?.let {
                Spacer(modifier = Modifier.height(2.dp))
                Text(it, style = Tipografia.bodyMedium, color = GrisTexto)
            }
        }
    }
}

@Composable
fun PartidaItem(partida: PartidaResumen, onClick: (PartidaResumen) -> Unit = {}) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = FondoElevado),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick(partida) }
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AsyncImage(
                model = partida.icono,
                contentDescription = partida.campeon,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(14.dp))
            )
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(partida.campeon, style = Tipografia.bodyLarge, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    EtiquetaResultado(partida.resultado)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text("KDA: ${partida.kda}", style = Tipografia.bodyMedium, color = Color.White)
                Text(partida.duracion, style = Tipografia.bodyMedium, color = GrisTexto)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(partida.hace, style = Tipografia.bodyMedium, color = GrisTexto)
            }
        }
    }
}

@Composable
fun EtiquetaResultado(resultado: ResultadoPartida) {
    val color = if (resultado == ResultadoPartida.VICTORIA) VerdeVictoria else RojoDerrota
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = if (resultado == ResultadoPartida.VICTORIA) "Victoria" else "Derrota",
            color = color,
            style = Tipografia.labelMedium
        )
    }
}

@Composable
fun CampeonCard(campeon: CampeonDetalle) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = FondoElevado),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            AsyncImage(
                model = campeon.imagen,
                contentDescription = campeon.nombre,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(14.dp))
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(campeon.nombre, style = Tipografia.headlineSmall, color = Color.White)
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Partidas ${campeon.partidas}", style = Tipografia.bodyMedium, color = GrisTexto)
                Text("WR: ${campeon.winRate}%", style = Tipografia.bodyMedium, color = Morado)
            }
            if (campeon.kda.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text("KDA: ${campeon.kda}", style = Tipografia.bodyMedium, color = Color.White)
            }
        }
    }
}

@Composable
fun NoticiaCard(noticia: NoticiaEsport, onClick: (NoticiaEsport) -> Unit = {}) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = FondoElevado),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .clickable { onClick(noticia) }
    ) {
        Column {
            AsyncImage(
                model = noticia.imagen,
                contentDescription = noticia.titulo,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
            )
            Column(modifier = Modifier.padding(14.dp)) {
                Text(noticia.titulo, style = Tipografia.headlineSmall, color = Color.White)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    noticia.descripcion,
                    style = Tipografia.bodyMedium,
                    color = GrisTexto,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(12.dp))
                BotonPrimario(texto = "Ver mas") { onClick(noticia) }
            }
        }
    }
}
