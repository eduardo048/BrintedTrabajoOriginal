package com.example.brinted.data.model

// Modelos de datos para la aplicación
// Representan las estructuras de datos utilizadas en la aplicación
// Incluyen usuarios, estadísticas, campeones, partidas, noticias, insights y análisis
// Cada modelo está diseñado para encapsular la información relevante de manera clara y concisa
// para facilitar su uso en la lógica de la aplicación y la interfaz de usuario

enum class ResultadoPartida { VICTORIA, DERROTA } // Enum para representar el resultado de una partida

data class Usuario( // Modelo de datos para un usuario
    val id: String = "",
    val correo: String = "",
    val nombreInvocador: String = "",
    val region: String = "euw1"
)

data class EstadisticasAvanzadas( // Modelo de datos para estadísticas avanzadas de un jugador
    val kdaPromedio: Double,
    val csPorMin: Double,
    val oroPromedio: Int,
    val duracionPromedio: String,
    val rachaVictorias: Int,
    val mejorKda: String,
    val tasaVictorias: Int,
    val nivel: Int
)

data class CampeonDetalle( // Modelo de datos para detalles de un campeón
    val nombre: String,
    val partidas: Int,
    val winRate: Int,
    val kda: String = "",
    val imagen: String
)

data class PartidaResumen( // Modelo de datos para el resumen de una partida
    val id: String,
    val campeon: String,
    val resultado: ResultadoPartida,
    val kda: String,
    val duracion: String,
    val hace: String,
    val icono: String
)

data class JugadorPartida( // Modelo de datos para un jugador en una partida
    val nombre: String,
    val rol: String,
    val campeon: String = "",
    val kda: String,
    val dano: String
)

data class PartidaDetalle( // Modelo de datos para el detalle de una partida
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

data class NoticiaEsport( // Modelo de datos para una noticia de esports
    val id: String,
    val titulo: String,
    val descripcion: String,
    val imagen: String,
    val url: String
)

data class Insight( // Modelo de datos para un insight generado
    val titulo: String,
    val descripcion: String,
    val tipo: String
)

data class AnalisisResumen( // Modelo de datos para el resumen del análisis de un jugador
    val kdaPorPartida: List<Double>,
    val metricas: List<EstadisticaClave>,
    val insights: List<Insight> = emptyList()
)

data class EstadisticaClave( // Modelo de datos para una estadística clave
    val titulo: String,
    val valor: String,
    val icono: String? = null,
    val tendencia: String? = null
)

data class DashboardResumen( // Modelo de datos para el resumen del dashboard de un jugador
    val invocador: Usuario,
    val estadisticas: EstadisticasAvanzadas,
    val campeones: List<CampeonDetalle>,
    val partidas: List<PartidaResumen>
)
