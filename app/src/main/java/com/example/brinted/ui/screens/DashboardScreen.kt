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

// Pantalla principal de la aplicación que muestra el resumen del dashboard
@Composable
fun DashboardScreen( // Pantalla principal de la aplicación que muestra el resumen del dashboard
    estado: DatosUiState, // Estado actual de la pantalla
    onVerPartida: (PartidaResumen) -> Unit, // Acción a realizar al hacer clic en una partida
    onRefresh: () -> Unit, // Acción a realizar al hacer clic en el botón de refrescar
    onLogout: () -> Unit // Acción a realizar al hacer clic en el botón de cerrar sesión
) {
    val menuAbierto = remember { mutableStateOf(false) } // Estado del menú desplegable


    if (estado.cargando && estado.dashboard == null) { // Si hay datos cargando
        Box(modifier = Modifier.fillMaxSize().background(Fondo), contentAlignment = Alignment.Center) { // Contenedor centrado
            CircularProgressIndicator(color = Morado) // Indicador de carga
        }
        return // Salir de la función
    }

    val dashboard = estado.dashboard ?: return // Dashboard no nulo


    LazyColumn( // Lista perezosa para el contenido
        modifier = Modifier // Aplicación del modificador
            .fillMaxSize() // Ocupa todo el espacio disponible
            .background(Fondo) // Fondo de la pantalla
            .padding(horizontal = 16.dp), // Espaciado horizontal
        verticalArrangement = Arrangement.spacedBy(16.dp) // Espaciado vertical entre elementos
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) } // Espacio superior
        
        item { // Encabezado
            Row( // Fila para el encabezado
                modifier = Modifier.fillMaxWidth(), // Ocupa todo el ancho disponible
                horizontalArrangement = Arrangement.SpaceBetween, // Espaciado entre elementos
                verticalAlignment = Alignment.CenterVertically // Alineación vertical centrada
            ) {
                Column { // Columna para el texto
                    Text("¡Hola, ${dashboard.invocador.nombreInvocador.split("#")[0]}!", // Texto de saludo
                        style = Tipografia.headlineMedium, color = Color.White)  // Estilo del texto
                    Text("Resumen de tu cuenta", style = Tipografia.bodyMedium, color = GrisTexto) // Texto de resumen
                }
                IconButton(onClick = { menuAbierto.value = true }) { // Botón de menú desplegable
                    Icon(Icons.Outlined.MoreVert, null, tint = Morado) // Icono del menú
                    DropdownMenu(expanded = menuAbierto.value, onDismissRequest = { menuAbierto.value = false }) { // Menú desplegable
                        DropdownMenuItem( // Elemento del menú
                            text = { Text("Refrescar") }, // Texto del elemento
                            onClick = { menuAbierto.value = false; onRefresh() }, // Acción al hacer clic
                            leadingIcon = { Icon(Icons.Outlined.Refresh, null) } // Icono del elemento
                        )
                        DropdownMenuItem( // Elemento del menú
                            text = { Text("Cerrar sesión") }, // Texto del elemento
                            onClick = { menuAbierto.value = false; onLogout() }, // Acción al hacer clic
                            leadingIcon = { Icon(Icons.Outlined.ExitToApp, null) } // Icono del elemento
                        )
                    }
                }
            }
        }

        item { // Hero de Dashboard
            Card( // Tarjeta
                shape = RoundedCornerShape(24.dp), // Esquinas redondeadas
                colors = CardDefaults.cardColors(containerColor = FondoElevado), // Color de fondo
                modifier = Modifier.fillMaxWidth() // Ocupa todo el ancho
            ) {
                Row( // Fila para el hero
                    modifier = Modifier.padding(20.dp), // Espaciado interno
                    verticalAlignment = Alignment.CenterVertically // Alineación vertical centrada
                ) {
                    Box { // Contenedor para la imagen
                        AsyncImage( // Imagen del invocador
                            model = "https://ddragon.leagueoflegends.com/cdn/14.1.1/img/profileicon/${(dashboard.estadisticas.nivel % 20)}.png", // URL de la imagen
                            contentDescription = null, // Descripción para accesibilidad
                            modifier = Modifier.size(80.dp).clip(CircleShape), // Tamaño y forma de la imagen
                            contentScale = ContentScale.Crop // Escala la imagen para cubrir el área
                        )
                        Surface( // Indicador de nivel
                            modifier = Modifier.align(Alignment.BottomCenter).offset(y = 8.dp), // Posición y espaciado
                            shape = RoundedCornerShape(10.dp), // Esquinas redondeadas
                            color = Morado // Color de fondO
                        ) {
                            Text("${dashboard.estadisticas.nivel}", color = Color.White,  // Texto del nivel
                                modifier = Modifier.padding(horizontal = 8.dp), style = Tipografia.labelSmall) // Estilo del texto
                        }
                    }
                    Spacer(modifier = Modifier.width(20.dp)) // Espacio entre la imagen y el texto
                    Column { // Columna para el texto
                        Text(dashboard.invocador.nombreInvocador, style = Tipografia.headlineSmall, color = Color.White) // Nombre del invocador
                        Text("Win Rate: ${dashboard.estadisticas.tasaVictorias}%", color = Morado, fontWeight = FontWeight.Bold) // Tasa de victorias
                    }
                }
            }
        }

        item { // Estadísticas
            Card(  //Tarjeta
                shape = RoundedCornerShape(18.dp), // Esquinas redondeadas
                colors = CardDefaults.cardColors(containerColor = FondoElevado), // Color de fondo
                modifier = Modifier.fillMaxWidth() // Ocupa todo el ancho
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) { // Columna para el contenido
                    Text("Estadísticas", style = Tipografia.headlineSmall, color = Color.White) // Texto de Estadisticas
                    LazyVerticalGrid( // Lista de estadísticas en formato de cuadrícula
                        columns = GridCells.Fixed(2), // Dos columnas
                        horizontalArrangement = Arrangement.spacedBy(10.dp), // Espaciado horizontal entre elementos
                        verticalArrangement = Arrangement.spacedBy(10.dp), // Espaciado vertical entre elementos
                        modifier = Modifier.height(250.dp) // Altura fija
                    ) {
                        gridItems( // Elementos de la lista
                            listOf( // Lista de pares (título, valor)
                                "KDA Promedio" to dashboard.estadisticas.kdaPromedio.toString(), // Par de elementos
                                "CS/min" to dashboard.estadisticas.csPorMin.toString(), // Par de elementos
                                "Oro" to String.format("%.1fk", dashboard.estadisticas.oroPromedio / 1000f), // Par de elementos
                                "Duración" to dashboard.estadisticas.duracionPromedio, // Par de elementos
                                "Racha" to "${dashboard.estadisticas.rachaVictorias} Victorias", // Par de elementos
                                "Mejor KDA" to dashboard.estadisticas.mejorKda // Par de elementos
                            )
                        ) { (titulo, valor) -> // Iterar sobre cada par de elementos
                            StatMiniCard(titulo = titulo, valor = valor) // Componente de tarjeta
                        }
                    }
                }
            }
        }

        if (dashboard.campeones.isNotEmpty()) { // Si hay campeones
            item { SeccionTitulo("Tus Mejores Campeones") } // Título de la sección
            item { // Lista de campeones
                LazyRow( // Lista perezosa para el contenido
                    horizontalArrangement = Arrangement.spacedBy(12.dp), // Espaciado horizontal entre elementos
                    contentPadding = PaddingValues(bottom = 8.dp) // Espaciado inferior
                ) {
                    items(dashboard.campeones) { campeon -> // Iterar sobre cada campeón
                        ChampionCard(campeon.nombre, campeon.winRate, campeon.imagen) // Componente de tarjeta de campeón
                    }
                }
            }
        }

        item { SeccionTitulo("Últimas Partidas") } // Título de la sección
        items(dashboard.partidas) { partida -> // Iterar sobre cada partida
            PartidaItem(partida = partida, onClick = onVerPartida) // Componente de partida
        }
        
        item { Spacer(modifier = Modifier.height(24.dp)) } // Espacio inferior
    }
}

@Composable
private fun StatMiniCard(titulo: String, valor: String) { // Componente de tarjeta de estadística
    Card( // Tarjeta
        shape = RoundedCornerShape(14.dp), // Esquinas redondeadas
        colors = CardDefaults.cardColors(containerColor = Color(0xFF161C29)), // Color de fondo
        modifier = Modifier.fillMaxWidth() // Ocupa todo el ancho
    ) {
        Column( // Columna para el contenido
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 14.dp), // Espaciado interno
            verticalArrangement = Arrangement.spacedBy(6.dp) // Espaciado vertical entre elementos
        ) {
            Text(titulo, style = Tipografia.bodyMedium, color = GrisTexto) // Texto de título
            Text(valor, style = Tipografia.headlineMedium, color = Color.White) // Texto de valor
        }
    }
}

@Composable
fun ChampionCard(nombre: String, winRate: Int, imagen: String) { // Componente de tarjeta de campeón
    Card(
        shape = RoundedCornerShape(20.dp), // Esquinas redondeadas
        modifier = Modifier.width(120.dp), // Ancho fijo
        colors = CardDefaults.cardColors(containerColor = FondoElevado) // Color de fondo
    ) {
        Column( // Columna para el contenido
            horizontalAlignment = Alignment.CenterHorizontally, // Alineación horizontal centrada
            modifier = Modifier // Aplicación del modificador
                .fillMaxWidth() // Ocupa todo el ancho disponible
                .padding(vertical = 16.dp, horizontal = 8.dp) // Espaciado interno
        ) {
            AsyncImage( // Imagen del campeón
                model = imagen, // URL de la imagen
                contentDescription = null, // Descripción para accesibilidad
                modifier = Modifier // Aplicación del modificador
                    .size(64.dp) // Tamaño fijo
                    .clip(CircleShape), // Forma circular
                contentScale = ContentScale.Crop // Escala la imagen para cubrir el área
            )
            Spacer(modifier = Modifier.height(10.dp)) // Espacio entre la imagen y el texto
            Text( // Texto del nombre del campeón
                text = nombre,  // Texto del nombre del campeón
                style = Tipografia.labelLarge,  // Estilo del texto
                color = Color.White,  // Color del texto
                maxLines = 1, // Máximo una línea
                textAlign = TextAlign.Center // Alineación del texto
            )
            Text( // Texto de la tasa de victorias
                text = "$winRate% WR", // Texto de la tasa de victorias
                style = Tipografia.bodySmall,  // Estilo del texto
                color = Morado, // Color del texto
                fontWeight = FontWeight.Bold, // Peso fuerte del texto
                textAlign = TextAlign.Center // Alineación del texto
            )
        }
    }
}
