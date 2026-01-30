package com.example.brinted.data.riot

import com.example.brinted.data.model.AnalisisResumen
import com.example.brinted.data.model.CampeonDetalle
import com.example.brinted.data.model.DashboardResumen
import com.example.brinted.data.model.NoticiaEsport
import com.example.brinted.data.model.PartidaDetalle
import com.example.brinted.data.model.PartidaResumen
import retrofit2.http.GET
import retrofit2.http.Query

// Definición de la interfaz para las funciones de Riot utilizando Retrofit
// Cada función corresponde a un endpoint específico de la API
// y utiliza anotaciones para definir el metodo HTTP y los parámetros de consulta
// Las funciones son suspendidas para permitir su uso con corutinas de Kotlin
// y manejar operaciones asíncronas de manera eficiente
// La interfaz incluye funciones para obtener el resumen del dashboard,
// el historial de partidas, el análisis del invocador, los detalles de los campeones,
// las noticias de esports y los detalles de una partida específica

interface RiotFunctionsService {
    // Función para obtener el resumen del dashboard de un invocador
    @GET("dashboard")
    suspend fun dashboard(
        @Query("region") region: String,
        @Query("invocador") invocador: String
    ): DashboardResumen

    // Función para obtener el historial de partidas de un invocador
    @GET("historial")
    suspend fun historial(
        @Query("region") region: String,
        @Query("invocador") invocador: String
    ): List<PartidaResumen>

    // Función para obtener el análisis de un invocador
    @GET("analisis")
    suspend fun analisis(
        @Query("region") region: String,
        @Query("invocador") invocador: String
    ): AnalisisResumen

    // Función para obtener los detalles de los campeones de un invocador
    @GET("campeones")
    suspend fun campeones(
        @Query("region") region: String,
        @Query("invocador") invocador: String
    ): List<CampeonDetalle>

    // Función para obtener las noticias de esports
    @GET("noticias")
    suspend fun noticias(): List<NoticiaEsport>

    // Función para obtener los detalles de una partida específica
    @GET("detalle")
    suspend fun detalle(
        @Query("region") region: String,
        @Query("partidaId") partidaId: String,
        @Query("invocador") invocador: String
    ): PartidaDetalle
}
