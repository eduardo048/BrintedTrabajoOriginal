package com.example.brinted.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.brinted.data.mock.MockData
import com.example.brinted.data.model.DashboardResumen
import com.example.brinted.data.model.PartidaResumen
import com.example.brinted.ui.components.PartidaItem
import com.example.brinted.ui.components.SeccionTitulo
import com.example.brinted.ui.home.DatosUiState
import com.example.brinted.ui.theme.Fondo
import com.example.brinted.ui.theme.FondoElevado
import com.example.brinted.ui.theme.GrisTexto
import com.example.brinted.ui.theme.Morado
import com.example.brinted.ui.theme.Tipografia
import androidx.compose.foundation.lazy.items as listItems
import androidx.compose.foundation.lazy.grid.items as gridItems

@Composable
fun DashboardScreen(
    estado: DatosUiState = DatosUiState(),
    onVerPartida: (PartidaResumen) -> Unit,
    onRefresh: () -> Unit,
    onLogout: () -> Unit
) {
    val dashboard: DashboardResumen = estado.dashboard ?: MockData.dashboard
    val campeones = if (estado.campeones.isNotEmpty()) estado.campeones else MockData.campeonesDemo
    val partidas = if (estado.historial.isNotEmpty()) estado.historial else MockData.partidasDemo
    val companeros = if (dashboard.companeros.isNotEmpty()) dashboard.companeros else MockData.companerosDemo
    val menuAbierto = remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Fondo)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Inicio", style = Tipografia.headlineMedium, color = Color.White)
                    Text("Estado de tu cuenta", style = Tipografia.bodyMedium, color = GrisTexto)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { menuAbierto.value = true }) {
                        Icon(Icons.Outlined.MoreVert, contentDescription = "Menú", tint = Morado)
                    }
                    DropdownMenu(expanded = menuAbierto.value, onDismissRequest = { menuAbierto.value = false }) {
                        DropdownMenuItem(
                            text = { Text("Refrescar") },
                            leadingIcon = { Icon(Icons.Outlined.Refresh, contentDescription = null) },
                            onClick = { menuAbierto.value = false; onRefresh() }
                        )
                        DropdownMenuItem(
                            text = { Text("Cerrar sesión") },
                            leadingIcon = { Icon(Icons.Outlined.ExitToApp, contentDescription = null) },
                            onClick = {
                                menuAbierto.value = false
                                onLogout()
                            }
                        )
                    }
                }
            }
        }

        // Hero invocador
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1128)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box {
                        val avatar = dashboard.campeones.firstOrNull()?.imagen
                            ?: "https://ddragon.leagueoflegends.com/cdn/img/champion/splash/Ahri_0.jpg"
                        AsyncImage(
                            model = avatar,
                            contentDescription = "Invocador",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(78.dp)
                                .clip(CircleShape)
                        )
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .align(Alignment.BottomEnd)
                                .background(Color(0xFF20D489), shape = CircleShape)
                        )
                    }
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(dashboard.invocador.nombreInvocador, style = Tipografia.headlineMedium, color = Color.White)
                        Text("Nivel ${dashboard.estadisticas.nivel}", style = Tipografia.bodyMedium, color = Color.White.copy(alpha = 0.85f))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Morado)
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                "Tasa de victorias: ${dashboard.estadisticas.tasaVictorias}%",
                                color = Color.White,
                                style = Tipografia.bodyMedium
                            )
                        }
                    }
                }
            }
        }

        // Estadísticas
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

        // Compañeros recientes
        item { SeccionTitulo("Compañeros Recientes") }
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 0.dp)
            ) {
                listItems(companeros) { compa ->
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = FondoElevado),
                        modifier = Modifier.width(120.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp, horizontal = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceEvenly
                        ) {
                            AsyncImage(
                                model = compa.avatar,
                                contentDescription = compa.nombre,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(16.dp))
                            )
                            Text(compa.nombre, style = Tipografia.bodyMedium, color = Color.White)
                            Text(compa.rol, style = Tipografia.bodyMedium, color = GrisTexto)
                        }
                    }
                }
            }
        }

        // Mejores campeones
        item { SeccionTitulo("Mejores Campeones") }
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp)
            ) {
                listItems(campeones) { campeon ->
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = FondoElevado),
                        modifier = Modifier.width(240.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            AsyncImage(
                                model = campeon.imagen,
                                contentDescription = campeon.nombre,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(130.dp)
                                    .clip(RoundedCornerShape(14.dp))
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(campeon.nombre, style = Tipografia.headlineSmall, color = Color.White)
                            Text("WR: ${campeon.winRate}%", style = Tipografia.bodyMedium, color = Morado)
                            if (campeon.kda.isNotBlank()) {
                                Text("KDA: ${campeon.kda}", style = Tipografia.bodyMedium, color = Color.White)
                            }
                        }
                    }
                }
            }
        }

        // Últimas partidas
        item { SeccionTitulo("Últimas Partidas") }
        listItems(partidas) { partida ->
            PartidaItem(partida = partida, onClick = onVerPartida)
            Spacer(modifier = Modifier.height(1.dp))
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
