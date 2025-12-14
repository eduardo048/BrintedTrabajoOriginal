package com.example.brinted.data.model

enum class ResultadoPartida { VICTORIA, DERROTA }

data class Usuario(
    val id: String = "",
    val correo: String = "",
    val nombreInvocador: String = ""
)

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

data class CompaneroReciente(
    val nombre: String,
    val rol: String,
    val avatar: String
)

data class CampeonDetalle(
    val nombre: String,
    val partidas: Int,
    val winRate: Int,
    val kda: String = "",
    val imagen: String
)

data class PartidaResumen(
    val id: String,
    val campeon: String,
    val resultado: ResultadoPartida,
    val kda: String,
    val duracion: String,
    val hace: String,
    val icono: String
)

data class JugadorPartida(
    val nombre: String,
    val rol: String,
    val campeon: String = "",
    val kda: String,
    val dano: String
)

data class PartidaDetalle(
    val id: String,
    val campeonPrincipal: String,
    val resultado: ResultadoPartida,
    val kda: String,
    val duracion: String,
    val icono: String,
    val equipoAzul: List<JugadorPartida>,
    val equipoRojo: List<JugadorPartida>,
    val metricasGlobales: List<EstadisticaClave>
)

data class NoticiaEsport(
    val id: String,
    val titulo: String,
    val descripcion: String,
    val imagen: String,
    val url: String
)

data class AnalisisResumen(
    val kdaPorPartida: List<Double>,
    val metricas: List<EstadisticaClave>
)

data class EstadisticaClave(
    val titulo: String,
    val valor: String,
    val icono: String? = null,
    val tendencia: String? = null
)

data class DashboardResumen(
    val invocador: Usuario,
    val estadisticas: EstadisticasAvanzadas,
    val companeros: List<CompaneroReciente>,
    val campeones: List<CampeonDetalle>,
    val partidas: List<PartidaResumen>
)
