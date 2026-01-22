package com.example.brinted.data.model

// Modelos de datos para la aplicación
enum class ResultadoPartida { VICTORIA, DERROTA } // Enum para representar el resultado de una partida

// Modelo de datos para el usuario
data class Usuario(
    val id: String = "",
    val correo: String = "",
    val nombreInvocador: String = "",
    val region: String = "euw1"
)

// Modelo de datos para estadísticas avanzadas del jugador
data class EstadisticasAvanzadas(
    val kdaPromedio: Double,
    val csPorMin: Double,
    val oroPromedio: Int,
    val duracionPromedio: String,
    val rachaVictorias: Int,
    val mejorKda: String,
    val tasaVictorias: Int,
    val nivel: Int
)

// Modelo de datos para un compañero reciente
data class CompaneroReciente(
    val nombre: String,
    val rol: String,
    val avatar: String
)

// Modelo de datos para el detalle de un campeón
data class CampeonDetalle(
    val nombre: String,
    val partidas: Int,
    val winRate: Int,
    val kda: String = "",
    val imagen: String
)

// Modelo de datos para el resumen de una partida
data class PartidaResumen(
    val id: String,
    val campeon: String,
    val resultado: ResultadoPartida,
    val kda: String,
    val duracion: String,
    val hace: String,
    val icono: String
)

// Modelo de datos para un jugador en una partida
data class JugadorPartida(
    val nombre: String,
    val rol: String,
    val campeon: String = "",
    val kda: String,
    val dano: String
)

// Modelo de datos para el detalle de una partida
data class PartidaDetalle(
    val id: String,
    val campeonPrincipal: String,
    val resultado: ResultadoPartida,
    val kda: String,
    val duracion: String,
    val icono: String,
    val aliados: List<JugadorPartida>,
    val enemigos: List<JugadorPartida>,
    val metricasGlobales: List<EstadisticaClave>
)

// Modelo de datos para una noticia de deportes electrónicos
data class NoticiaEsport(
    val id: String,
    val titulo: String,
    val descripcion: String,
    val imagen: String,
    val url: String
)

// Modelo de datos para el análisis resumen
data class AnalisisResumen(
    val kdaPorPartida: List<Double>,
    val metricas: List<EstadisticaClave>
)

// Modelo de datos para una estadística clave
data class EstadisticaClave(
    val titulo: String,
    val valor: String,
    val icono: String? = null,
    val tendencia: String? = null
)

// Modelo de datos para el resumen del dashboard
data class DashboardResumen(
    val invocador: Usuario,
    val estadisticas: EstadisticasAvanzadas,
    val companeros: List<CompaneroReciente>,
    val campeones: List<CampeonDetalle>,
    val partidas: List<PartidaResumen>
)
