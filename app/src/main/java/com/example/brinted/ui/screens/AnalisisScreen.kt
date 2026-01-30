package com.example.brinted.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.brinted.data.model.AnalisisResumen
import com.example.brinted.data.model.EstadisticaClave
import com.example.brinted.data.model.Insight
import com.example.brinted.ui.theme.*

// Pantalla de Análisis de Rendimiento
// Muestra un resumen del rendimiento del jugador basado en sus partidas reales
// Incluye métricas clave, gráficos de tendencia e insights personalizados
// Basado en el diseño proporcionado

@Composable
fun AnalisisScreen( // Pantalla principal de análisis de rendimiento
    analisis: AnalisisResumen?, // Datos de análisis de rendimiento
    cargando: Boolean // Indicador de carga
) {
    if (analisis == null) { // Si no hay datos de análisis
        Box(modifier = Modifier.fillMaxSize().background(Fondo), contentAlignment = Alignment.Center) { // Contenedor centrado
            CircularProgressIndicator(color = Morado) // Indicador de carga
        }
        return // Salir de la función
    }

    LazyColumn( // Lista perezosa para el contenido
        modifier = Modifier
            .fillMaxSize() // Rellenar todo el espacio disponible
            .background(Fondo) // Fondo de la pantalla
            .padding(horizontal = 16.dp), // Padding horizontal
        verticalArrangement = Arrangement.spacedBy(20.dp) // Espaciado vertical entre elementos
    ) {
        item { // Encabezado
            Spacer(modifier = Modifier.height(12.dp)) // Espacio superior
            Text("Análisis de Rendimiento", style = Tipografia.headlineMedium, color = Color.White) // Título
            Text("Basado en tus últimas partidas reales", style = Tipografia.bodyMedium, color = GrisTexto) // Subtítulo
        }


        item {// Hero de Rendimiento
            PerformanceHero(analisis.metricas) // Componente de héroe de rendimiento
        }


        if (analisis.kdaPorPartida.isNotEmpty()) { // Gráfico de Tendencia de KDA
            item { // Elemento de lista
                TrendChartCard(analisis.kdaPorPartida) // Componente de tarjeta de gráfico de tendencia
            }
        }


        if (analisis.insights.isNotEmpty()) { // Sección de Insights
            item { // Elemento de lista
                InsightsSection(analisis.insights) // Componente de sección de insights
            }
        }


        item { // Métricas Detalladas
            Text("Métricas Detalladas", style = Tipografia.titleLarge, color = Color.White) // Título de la sección
        }

        items(analisis.metricas.chunked(2)) { pair -> // Iterar sobre las métricas en pares
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) { // Fila para cada par de métricas
                pair.forEach { metrica -> // Iterar sobre cada métrica en el par
                    MiniMetricCard(metrica, Modifier.weight(1f)) // Componente de tarjeta de métrica miniatura
                }
                if (pair.size == 1) Spacer(Modifier.weight(1f)) // Espacio si hay una sola métrica en el par
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) } // Espacio inferior
    }
}

@Composable
fun PerformanceHero(metricas: List<EstadisticaClave>) { // Componente de héroe de rendimiento
    val winRateStr = metricas.firstOrNull { it.titulo.contains("Victoria") }?.valor ?: "0%" // Obtener tasa de victorias
    val winRate = winRateStr.replace("%", "").toFloatOrNull() ?: 0f // Convertir a flotante
    
    Card( // Tarjeta de rendimiento
        shape = RoundedCornerShape(28.dp), // Esquinas redondeadas
        colors = CardDefaults.cardColors(containerColor = FondoElevado), // Color de fondo
        modifier = Modifier.fillMaxWidth() // Rellenar todo el ancho
    ) {
        Row(modifier = Modifier.padding(24.dp), verticalAlignment = Alignment.CenterVertically) { // Fila con padding
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(90.dp)) { // Caja para el indicador circular
                CircularProgressIndicator( // Indicador de progreso circular
                    progress = { winRate / 100f }, // Progreso basado en la tasa de victorias
                    modifier = Modifier.fillMaxSize(), // Rellenar todo el espacio disponible
                    color = if (winRate >= 50) Morado else RojoDerrota, // Color basado en el rendimiento
                    strokeWidth = 8.dp, // Ancho del trazo
                    trackColor = Color.White.copy(alpha = 0.05f), // Color de la pista
                    strokeCap = StrokeCap.Round, // Capas redondeadas
                )
                Text("$winRate%", style = Tipografia.headlineSmall, color = Color.White, fontWeight = FontWeight.ExtraBold) // Texto del porcentaje
            }
            
            Spacer(modifier = Modifier.width(24.dp)) // Espacio entre el indicador y el texto
            
            Column { // Columna para el texto
                Text("Estado Actual", style = Tipografia.labelMedium, color = GrisTexto) // Subtítulo
                Text(if (winRate >= 50) "Dominando el Meta" else "Fase de Ajuste", style = Tipografia.headlineSmall, color = Color.White) // Título basado en el rendimiento
                Text("Sigue así para subir de rango", style = Tipografia.bodySmall, color = GrisTexto) // Descripción
            }
        }
    }
}

@Composable
fun TrendChartCard(kdas: List<Double>) { // Componente de tarjeta de gráfico de tendencia
    Card( // Tarjeta
        shape = RoundedCornerShape(24.dp), // Esquinas redondeadas
        colors = CardDefaults.cardColors(containerColor = FondoElevado), // Color de fondo
        modifier = Modifier.fillMaxWidth() // Rellenar todo el ancho
    ) {
        Column(modifier = Modifier.padding(20.dp)) { // Columna con padding
            Row(verticalAlignment = Alignment.CenterVertically) { // Fila para el título
                Icon(Icons.Default.ShowChart, null, tint = Morado, modifier = Modifier.size(20.dp)) // Icono del gráfico
                Spacer(modifier = Modifier.width(8.dp)) // Espacio entre el icono y el texto
                Text("Evolución KDA (Últimas 10)", style = Tipografia.titleMedium, color = Color.White) // Título
            }
            Spacer(modifier = Modifier.height(20.dp)) // Espacio entre el título y el gráfico
            SimpleLineChart(kdas) // Componente de gráfico de línea simple
        }
    }
}

@Composable
fun InsightsSection(insights: List<Insight>) { // Componente de sección de insights
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) { // Columna con espaciado entre elementos
        Text("Insights de Brinted", style = Tipografia.titleLarge, color = Color.White) // Título de la sección
        insights.forEach { insight -> // Iterar sobre cada insight
            val color = when(insight.tipo) { // Determinar el color basado en el tipo de insight
                "POSITIVE" -> VerdeVictoria // Color para insights positivos
                "NEGATIVE" -> RojoDerrota // Color para insights negativos
                else -> Color(0xFFFFB74D) // Color para insights neutrales
            }
            val icono = when(insight.tipo) { // Determinar el icono basado en el tipo de insight
                "POSITIVE" -> Icons.Default.CheckCircle // Icono para insights positivos
                "NEGATIVE" -> Icons.Default.Error // Icono para insights negativos
                else -> Icons.Default.Info // Icono para insights neutrales
            }
            InsightItem(insight.titulo, insight.descripcion, icono, color) // Componente de item de insight
        }
    }
}

@Composable
fun InsightItem(titulo: String, descripcion: String, icono: ImageVector, color: Color) { // Componente de item de insight
    Surface(
        color = color.copy(alpha = 0.05f), // Color de fondo con transparencia
        shape = RoundedCornerShape(16.dp), // Esquinas redondeadas
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.15f)) // Borde con transparencia
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) { // Fila con padding
            Icon(icono, null, tint = color, modifier = Modifier.size(24.dp)) // Icono del insight
            Spacer(modifier = Modifier.width(16.dp)) // Espacio entre el icono y el texto
            Column { // Columna para el texto
                Text(titulo, style = Tipografia.labelLarge, color = Color.White) // Título del insight
                Text(descripcion, style = Tipografia.bodySmall, color = GrisTexto) // Descripción del insight
            }
        }
    }
}

@Composable
fun MiniMetricCard(metrica: EstadisticaClave, modifier: Modifier) { // Componente de tarjeta de métrica miniatura
    Card( // Tarjeta
        modifier = modifier, // Modificador pasado como parámetro
        shape = RoundedCornerShape(16.dp), // Esquinas redondeadas
        colors = CardDefaults.cardColors(containerColor = FondoElevado) // Color de fondo
    ) {
        Column(modifier = Modifier.padding(16.dp)) { // Columna con padding
            Text(metrica.titulo, style = Tipografia.labelSmall, color = GrisTexto) // Título de la métrica
            Spacer(modifier = Modifier.height(4.dp)) // Espacio entre el título y el valor
            Text(metrica.valor, style = Tipografia.titleLarge, color = Color.White) // Valor de la métrica
        }
    }
}

@Composable
private fun SimpleLineChart(valores: List<Double>) { // Componente de gráfico de línea simple
    val max = (valores.maxOrNull() ?: 1.0).coerceAtLeast(1.0).toFloat() // Valor máximo para escalar el gráfico
    
    Canvas(modifier = Modifier.fillMaxWidth().height(100.dp)) { // Lienzo para dibujar el gráfico
        val width = size.width // Ancho del lienzo
        val height = size.height // Alto del lienzo
        val spacing = width / (valores.size - 1).coerceAtLeast(1) // Espaciado entre puntos
        
        val points = valores.mapIndexed { index, valD -> // Calcular los puntos del gráfico
            Offset(index * spacing, height - (valD.toFloat() / max * height)) // Escalar el valor al alto del lienzo
        }

        val path = Path().apply { // Crear el camino del gráfico
            points.forEachIndexed { i, pt -> if (i == 0) moveTo(pt.x, pt.y) else lineTo(pt.x, pt.y) } // Moverse a cada punto
        }

        drawPath( // Dibujar el camino
            path = path, // Camino del gráfico
            color = Morado, // Color de la línea
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round) // Estilo de trazo
        )
        
        points.forEach { pt -> // Dibujar los puntos
            drawCircle(Color.White, radius = 3.dp.toPx(), center = pt) // Punto blanco grande
            drawCircle(Morado, radius = 1.5.dp.toPx(), center = pt) // Punto morado pequeño
        }
    }
}
