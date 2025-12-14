package com.example.brinted.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.brinted.data.mock.MockData
import com.example.brinted.data.model.AnalisisResumen
import com.example.brinted.data.model.EstadisticaClave
import com.example.brinted.ui.theme.Fondo
import com.example.brinted.ui.theme.FondoElevado
import com.example.brinted.ui.theme.GrisTexto
import com.example.brinted.ui.theme.Morado
import com.example.brinted.ui.theme.Tipografia

/**
 * Pantalla de análisis: muestra gráfica de KDA reciente y métricas clave.
 * Usa datos de HomeViewModel (API Riot o MockData como respaldo).
 */
@Composable
fun AnalisisScreen(
    analisis: AnalisisResumen?,
    cargando: Boolean
) {
    val data = analisis ?: MockData.analisisDemo

    // Capturamos algunas metricas comunes para destacar
    val winRate = data.metricas.findBy("victoria")
    val kdaAvg = data.metricas.findBy("kda")
    val cs = data.metricas.findBy("cs")
    val duracion = data.metricas.findBy("duracion")
    val metricasFiltradas = data.metricas.filterNot {
        it.titulo.contains("victoria", ignoreCase = true) ||
                it.titulo.contains("kda", ignoreCase = true) ||
                it.titulo.contains("cs", ignoreCase = true)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Fondo)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Text("Analisis", style = Tipografia.headlineMedium, color = Color.White)
        }

        // Resumen rapido
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = FondoElevado)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HighlightStat(
                        modifier = Modifier.weight(1f),
                        title = "Tasa de Victoria",
                        valor = winRate?.valor ?: "-",
                        detalle = ""
                    )
                    HighlightStat(
                        modifier = Modifier.weight(1f),
                        title = "KDA",
                        valor = kdaAvg?.valor ?: "-",
                        detalle = "Promedio"
                    )
                    HighlightStat(
                        modifier = Modifier.weight(1f),
                        title = "CS/min",
                        valor = cs?.valor ?: "-",
                        detalle = duracion?.titulo ?: ""
                    )
                }
            }
        }

        // Grafico principal
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = FondoElevado)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Rendimiento KDA", style = Tipografia.headlineSmall, color = Color.White)
                        Text("Ultimas 10 partidas", style = Tipografia.bodyMedium, color = GrisTexto)
                    }
                    SimpleLineChart(valores = data.kdaPorPartida)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Canvas(modifier = Modifier.size(10.dp)) {
                                drawCircle(color = Morado)
                            }
                            Text("KDA", color = GrisTexto, style = Tipografia.bodyMedium)
                        }
                    }
                }
            }
        }

        // Bloque de metricas
        item {
            Text("Metricas Clave", style = Tipografia.headlineSmall, color = Color.White)
        }

        items(metricasFiltradas) { metrica ->
            MetricCard(metrica = metrica, modifier = Modifier.fillMaxWidth())
        }

        if (cargando) {
            item { Text("Actualizando datos...", color = GrisTexto, style = Tipografia.bodyMedium) }
        } else {
            item { Spacer(modifier = Modifier.height(8.dp)) }
        }
    }
}

@Composable
private fun HighlightStat(modifier: Modifier = Modifier, title: String, valor: String, detalle: String) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(title, style = Tipografia.bodyMedium, color = GrisTexto)
        Text(valor, style = Tipografia.headlineSmall.copy(fontSize = 22.sp), fontWeight = FontWeight.Bold, color = Color.White)
        if (detalle.isNotEmpty()) {
            Text(detalle, style = Tipografia.bodyMedium, color = GrisTexto)
        }
    }
}

private fun List<EstadisticaClave>.findBy(texto: String): EstadisticaClave? =
    firstOrNull { it.titulo.contains(texto, ignoreCase = true) }

@Composable
private fun MetricCard(metrica: EstadisticaClave, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1826)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(metrica.titulo, style = Tipografia.bodyMedium, color = GrisTexto)
                if (!metrica.tendencia.isNullOrBlank()) {
                    Text(
                        metrica.tendencia,
                        color = Morado,
                        style = Tipografia.labelMedium,
                        modifier = Modifier
                            .background(Morado.copy(alpha = 0.12f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            Text(
                metrica.valor,
                style = Tipografia.headlineSmall.copy(fontSize = 22.sp),
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun SimpleLineChart(valores: List<Double>) {
    val maxValor = (valores.maxOrNull() ?: 1.0).coerceAtLeast(1.0)
    val minValor = valores.minOrNull() ?: 0.0
    val rango = (maxValor - minValor).takeIf { it != 0.0 } ?: 1.0

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1624))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            ) {
                val ancho = size.width
                val alto = size.height
                val pasoX = if (valores.size > 1) ancho / (valores.size - 1) else ancho
                val linePath = Path()
                val fillPath = Path()

                // Lineas guia sutiles
                val pasosHorizontales = 4
                val gap = alto / (pasosHorizontales + 1)
                for (i in 1..pasosHorizontales) {
                    val y = gap * i
                    drawLine(
                        color = Color.White.copy(alpha = 0.05f),
                        start = Offset(0f, y),
                        end = Offset(ancho, y),
                        strokeWidth = 1f
                    )
                }

                // Construye paths
                valores.forEachIndexed { index, valor ->
                    val x = (pasoX * index).toFloat()
                    val y = (alto - ((valor - minValor) / rango * alto)).toFloat()
                    if (index == 0) {
                        linePath.moveTo(x, y)
                        fillPath.moveTo(x, alto)
                        fillPath.lineTo(x, y)
                    } else {
                        linePath.lineTo(x, y)
                        fillPath.lineTo(x, y)
                    }
                    if (index == valores.lastIndex) {
                        fillPath.lineTo(x, alto)
                        fillPath.close()
                    }
                }

                // Relleno degradado bajo la curva
                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(Morado.copy(alpha = 0.18f), Color.Transparent),
                        endY = alto
                    )
                )

                drawPath(
                    path = linePath,
                    color = Morado,
                    style = Stroke(width = 4f, cap = StrokeCap.Round)
                )
                valores.forEachIndexed { index, valor ->
                    val x = (pasoX * index).toFloat()
                    val y = (alto - ((valor - minValor) / rango * alto)).toFloat()
                    val centro = Offset(x, y)
                    drawCircle(color = Morado.copy(alpha = 0.28f), radius = 11f, center = centro)
                    drawCircle(color = Morado, radius = 8f, center = centro)
                    drawCircle(color = Color.White, radius = 5f, center = centro)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                valores.forEachIndexed { index, _ ->
                    Text("P.${index + 1}", color = Color.White.copy(alpha = 0.8f), style = Tipografia.bodyMedium)
                }
            }
        }
    }
}
