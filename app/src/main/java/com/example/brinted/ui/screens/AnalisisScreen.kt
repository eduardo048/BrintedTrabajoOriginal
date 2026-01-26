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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.brinted.data.model.AnalisisResumen
import com.example.brinted.data.model.EstadisticaClave
import com.example.brinted.data.model.Insight
import com.example.brinted.ui.theme.*

@Composable
fun AnalisisScreen(
    analisis: AnalisisResumen?,
    cargando: Boolean
) {
    if (analisis == null) {
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
            Text("Basado en tus últimas partidas reales", style = Tipografia.bodyMedium, color = GrisTexto)
        }

        // Hero Section: WinRate Real
        item {
            PerformanceHero(analisis.metricas)
        }

        // Gráfica de Tendencia Real
        if (analisis.kdaPorPartida.isNotEmpty()) {
            item {
                TrendChartCard(analisis.kdaPorPartida)
            }
        }

        // Insights Reales del Servidor
        if (analisis.insights.isNotEmpty()) {
            item {
                InsightsSection(analisis.insights)
            }
        }

        // Métricas Detalladas (Visión, Daño, etc.)
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
    val winRateStr = metricas.firstOrNull { it.titulo.contains("Victoria") }?.valor ?: "0%"
    val winRate = winRateStr.replace("%", "").toFloatOrNull() ?: 0f
    
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = FondoElevado),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(24.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(90.dp)) {
                CircularProgressIndicator(
                    progress = { winRate / 100f },
                    modifier = Modifier.fillMaxSize(),
                    color = if (winRate >= 50) Morado else RojoDerrota,
                    strokeWidth = 8.dp,
                    trackColor = Color.White.copy(alpha = 0.05f),
                    strokeCap = StrokeCap.Round,
                )
                Text("$winRate%", style = Tipografia.headlineSmall, color = Color.White, fontWeight = FontWeight.ExtraBold)
            }
            
            Spacer(modifier = Modifier.width(24.dp))
            
            Column {
                Text("Estado Actual", style = Tipografia.labelMedium, color = GrisTexto)
                Text(if (winRate >= 50) "Dominando el Meta" else "Fase de Ajuste", style = Tipografia.headlineSmall, color = Color.White)
                Text("Sigue así para subir de rango", style = Tipografia.bodySmall, color = GrisTexto)
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
                Text("Evolución KDA (Últimas 10)", style = Tipografia.titleMedium, color = Color.White)
            }
            Spacer(modifier = Modifier.height(20.dp))
            SimpleLineChart(kdas)
        }
    }
}

@Composable
fun InsightsSection(insights: List<Insight>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Insights de Brinted", style = Tipografia.titleLarge, color = Color.White)
        insights.forEach { insight ->
            val color = when(insight.tipo) {
                "POSITIVE" -> VerdeVictoria
                "NEGATIVE" -> RojoDerrota
                else -> Color(0xFFFFB74D)
            }
            val icono = when(insight.tipo) {
                "POSITIVE" -> Icons.Default.CheckCircle
                "NEGATIVE" -> Icons.Default.Error
                else -> Icons.Default.Info
            }
            InsightItem(insight.titulo, insight.descripcion, icono, color)
        }
    }
}

@Composable
fun InsightItem(titulo: String, descripcion: String, icono: ImageVector, color: Color) {
    Surface(
        color = color.copy(alpha = 0.05f),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.15f))
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
    
    Canvas(modifier = Modifier.fillMaxWidth().height(100.dp)) {
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
        
        points.forEach { pt ->
            drawCircle(Color.White, radius = 3.dp.toPx(), center = pt)
            drawCircle(Morado, radius = 1.5.dp.toPx(), center = pt)
        }
    }
}
