package com.example.brinted.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.CheckCircle
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.brinted.data.model.AnalisisResumen
import com.example.brinted.data.model.EstadisticaClave
import com.example.brinted.ui.theme.*

@Composable
fun AnalisisScreen(
    analisis: AnalisisResumen?,
    cargando: Boolean
) {
    if (analisis == null || analisis.metricas.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize().background(Fondo), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Morado)
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Fondo)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Text("Análisis de Rendimiento", style = Tipografia.headlineMedium, color = Color.White)
            Text("Basado en tus últimas 10 partidas", style = Tipografia.bodyMedium, color = GrisTexto)
        }

        // Hero Section: Resumen Estilo Blitz
        item {
            PerformanceHero(analisis.metricas)
        }

        // Gráfica de Tendencia de KDA
        item {
            TrendChartCard(analisis.kdaPorPartida)
        }

        // Fortalezas y Debilidades
        item {
            InsightsSection(analisis.metricas)
        }

        // Resto de Métricas en Rejilla
        item {
            Text("Métricas Detalladas", style = Tipografia.titleLarge, color = Color.White)
        }

        items(analisis.metricas.chunked(2)) { pair ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                pair.forEach { metrica ->
                    MiniMetricCard(metrica, Modifier.weight(1f))
                }
                if (pair.size == 1) Spacer(Modifier.weight(1f))
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
fun PerformanceHero(metricas: List<EstadisticaClave>) {
    val winRate = metricas.firstOrNull { it.titulo.contains("Victoria") }?.valor ?: "0%"
    
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = FondoElevado),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Círculo de WinRate
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(90.dp)) {
                CircularProgressIndicator(
                    progress = { (winRate.replace("%", "").toFloatOrNull() ?: 0f) / 100f },
                    modifier = Modifier.fillMaxSize(),
                    color = Morado,
                    strokeWidth = 8.dp,
                    trackColor = Morado.copy(alpha = 0.1f),
                    strokeCap = StrokeCap.Round,
                )
                Text(winRate, style = Tipografia.headlineSmall, color = Color.White, fontWeight = FontWeight.ExtraBold)
            }
            
            Spacer(modifier = Modifier.width(24.dp))
            
            Column {
                Text("Nivel de Juego", style = Tipografia.labelMedium, color = GrisTexto)
                Text("Invocador Élite", style = Tipografia.headlineSmall, color = Color.White)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.TrendingUp, null, tint = VerdeVictoria, modifier = Modifier.size(16.dp))
                    Text(" +12% vs semana pasada", color = VerdeVictoria, style = Tipografia.bodySmall)
                }
            }
        }
    }
}

@Composable
fun TrendChartCard(kdas: List<Double>) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = FondoElevado),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.ShowChart, null, tint = Morado, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Tendencia KDA", style = Tipografia.titleMedium, color = Color.White)
            }
            Spacer(modifier = Modifier.height(20.dp))
            SimpleLineChart(kdas)
        }
    }
}

@Composable
fun InsightsSection(metricas: List<EstadisticaClave>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Insights de Blitz", style = Tipografia.titleLarge, color = Color.White)
        
        InsightItem(
            titulo = "Control de Oleadas",
            descripcion = "Tu CS/min es superior a la media de tu rango. Mantén esa presión.",
            icono = Icons.Default.CheckCircle,
            color = VerdeVictoria
        )
        
        InsightItem(
            titulo = "Visión en Early",
            descripcion = "Estás comprando menos wards de lo recomendado antes del min 10.",
            icono = Icons.Default.Warning,
            color = Color(0xFFFFB74D)
        )
    }
}

@Composable
fun InsightItem(titulo: String, descripcion: String, icono: ImageVector, color: Color) {
    Surface(
        color = color.copy(alpha = 0.05f),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.2f))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icono, null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(titulo, style = Tipografia.labelLarge, color = Color.White)
                Text(descripcion, style = Tipografia.bodySmall, color = GrisTexto)
            }
        }
    }
}

@Composable
fun MiniMetricCard(metrica: EstadisticaClave, modifier: Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = FondoElevado)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(metrica.titulo, style = Tipografia.labelSmall, color = GrisTexto)
            Spacer(modifier = Modifier.height(4.dp))
            Text(metrica.valor, style = Tipografia.titleLarge, color = Color.White)
        }
    }
}

@Composable
private fun SimpleLineChart(valores: List<Double>) {
    val max = (valores.maxOrNull() ?: 1.0).coerceAtLeast(1.0).toFloat()
    
    Canvas(modifier = Modifier.fillMaxWidth().height(120.dp)) {
        val width = size.width
        val height = size.height
        val spacing = width / (valores.size - 1).coerceAtLeast(1)
        
        val points = valores.mapIndexed { index, valD ->
            Offset(index * spacing, height - (valD.toFloat() / max * height))
        }

        val path = Path().apply {
            points.forEachIndexed { i, pt -> if (i == 0) moveTo(pt.x, pt.y) else lineTo(pt.x, pt.y) }
        }

        drawPath(
            path = path,
            color = Morado,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )
        
        // Puntos destacados
        points.forEach { pt ->
            drawCircle(Color.White, radius = 4.dp.toPx(), center = pt)
            drawCircle(Morado, radius = 2.dp.toPx(), center = pt)
        }
    }
}
