package com.example.brinted.data.riot

import com.example.brinted.data.model.AnalisisResumen
import com.example.brinted.data.model.CampeonDetalle
import com.example.brinted.data.model.DashboardResumen
import com.example.brinted.data.model.NoticiaEsport
import com.example.brinted.data.model.PartidaDetalle
import com.example.brinted.data.model.PartidaResumen
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Servicio HTTP que apunta al proxy de Cloud Functions.
 * Cada endpoint corresponde a una ruta expuesta en riotProxy.
 */
interface RiotFunctionsService {
    /** Resumen principal del invocador (dashboard). */
    @GET("dashboard")
    suspend fun dashboard(
        @Query("region") region: String,
        @Query("invocador") invocador: String
    ): DashboardResumen

    /** Historial de partidas recientes. */
    @GET("historial")
    suspend fun historial(
        @Query("region") region: String,
        @Query("invocador") invocador: String
    ): List<PartidaResumen>

    /** Bloque de análisis de rendimiento. */
    @GET("analisis")
    suspend fun analisis(
        @Query("region") region: String,
        @Query("invocador") invocador: String
    ): AnalisisResumen

    /** Campeones más jugados con sus métricas. */
    @GET("campeones")
    suspend fun campeones(
        @Query("region") region: String,
        @Query("invocador") invocador: String
    ): List<CampeonDetalle>

    /** Noticias de eSports. */
    @GET("noticias")
    suspend fun noticias(): List<NoticiaEsport>

    /** Detalle de una partida específica. */
    @GET("detalle")
    suspend fun detalle(
        @Query("region") region: String,
        @Query("partidaId") partidaId: String
    ): PartidaDetalle
}
