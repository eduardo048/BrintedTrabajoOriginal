package com.example.brinted.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.brinted.data.model.DashboardResumen
import com.example.brinted.data.model.PartidaResumen
import com.example.brinted.ui.components.PartidaItem
import com.example.brinted.ui.components.SeccionTitulo
import com.example.brinted.ui.home.DatosUiState
import com.example.brinted.ui.theme.*

/** Pantalla de Inicio/Dashboard con resumen del invocador real. */
@Composable
fun DashboardScreen(
    estado: DatosUiState,
    onVerPartida: (PartidaResumen) -> Unit,
    onRefresh: () -> Unit,
    onLogout: () -> Unit
) {
    val menuAbierto = remember { mutableStateOf(false) }

    if (estado.cargando && estado.dashboard == null) {
        Box(modifier = Modifier.fillMaxSize().background(Fondo), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Morado)
        }
        return
    }

    val dashboard = estado.dashboard ?: return

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Fondo)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }
        
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("¡Hola, ${dashboard.invocador.nombreInvocador.split("#")[0]}!", 
                        style = Tipografia.headlineMedium, color = Color.White)
                    Text("Resumen de tu cuenta", style = Tipografia.bodyMedium, color = GrisTexto)
                }
                IconButton(onClick = { menuAbierto.value = true }) {
                    Icon(Icons.Outlined.MoreVert, null, tint = Morado)
                    DropdownMenu(expanded = menuAbierto.value, onDismissRequest = { menuAbierto.value = false }) {
                        DropdownMenuItem(
                            text = { Text("Refrescar") },
                            onClick = { menuAbierto.value = false; onRefresh() },
                            leadingIcon = { Icon(Icons.Outlined.Refresh, null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Cerrar sesión") },
                            onClick = { menuAbierto.value = false; onLogout() },
                            leadingIcon = { Icon(Icons.Outlined.ExitToApp, null) }
                        )
                    }
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = FondoElevado),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box {
                        AsyncImage(
                            model = "https://ddragon.leagueoflegends.com/cdn/14.1.1/img/profileicon/${(dashboard.estadisticas.nivel % 20)}.png",
                            contentDescription = null,
                            modifier = Modifier.size(80.dp).clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Surface(
                            modifier = Modifier.align(Alignment.BottomCenter).offset(y = 8.dp),
                            shape = RoundedCornerShape(10.dp),
                            color = Morado
                        ) {
                            Text("${dashboard.estadisticas.nivel}", color = Color.White, 
                                modifier = Modifier.padding(horizontal = 8.dp), style = Tipografia.labelSmall)
                        }
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    Column {
                        Text(dashboard.invocador.nombreInvocador, style = Tipografia.headlineSmall, color = Color.White)
                        Text("Win Rate: ${dashboard.estadisticas.tasaVictorias}%", color = Morado, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = FondoElevado),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Estadísticas", style = Tipografia.headlineSmall, color = Color.White)
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.height(250.dp)
                    ) {
                        gridItems(
                            listOf(
                                "KDA Promedio" to dashboard.estadisticas.kdaPromedio.toString(),
                                "CS/min" to dashboard.estadisticas.csPorMin.toString(),
                                "Oro" to String.format("%.1fk", dashboard.estadisticas.oroPromedio / 1000f),
                                "Duración" to dashboard.estadisticas.duracionPromedio,
                                "Racha" to "${dashboard.estadisticas.rachaVictorias} Victorias",
                                "Mejor KDA" to dashboard.estadisticas.mejorKda
                            )
                        ) { (titulo, valor) ->
                            StatMiniCard(titulo = titulo, valor = valor)
                        }
                    }
                }
            }
        }

        if (dashboard.campeones.isNotEmpty()) {
            item { SeccionTitulo("Tus Mejores Campeones") }
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 8.dp)
                ) {
                    items(dashboard.campeones) { campeon ->
                        ChampionCard(campeon.nombre, campeon.winRate, campeon.imagen)
                    }
                }
            }
        }

        item { SeccionTitulo("Últimas Partidas") }
        items(dashboard.partidas) { partida ->
            PartidaItem(partida = partida, onClick = onVerPartida)
        }
        
        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
private fun StatMiniCard(titulo: String, valor: String) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF161C29)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(titulo, style = Tipografia.bodyMedium, color = GrisTexto)
            Text(valor, style = Tipografia.headlineMedium, color = Color.White)
        }
    }
}

@Composable
fun ChampionCard(nombre: String, winRate: Int, imagen: String) {
    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.width(120.dp),
        colors = CardDefaults.cardColors(containerColor = FondoElevado)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 8.dp)
        ) {
            AsyncImage(
                model = imagen,
                contentDescription = null,
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = nombre, 
                style = Tipografia.labelLarge, 
                color = Color.White, 
                maxLines = 1,
                textAlign = TextAlign.Center
            )
            Text(
                text = "$winRate% WR", 
                style = Tipografia.bodySmall, 
                color = Morado,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}
