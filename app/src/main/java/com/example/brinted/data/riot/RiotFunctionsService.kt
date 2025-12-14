package com.example.brinted.data.riot

import com.example.brinted.data.model.AnalisisResumen
import com.example.brinted.data.model.CampeonDetalle
import com.example.brinted.data.model.DashboardResumen
import com.example.brinted.data.model.NoticiaEsport
import com.example.brinted.data.model.PartidaDetalle
import com.example.brinted.data.model.PartidaResumen
import retrofit2.http.GET
import retrofit2.http.Query

interface RiotFunctionsService {
    @GET("dashboard")
    suspend fun dashboard(
        @Query("region") region: String,
        @Query("invocador") invocador: String
    ): DashboardResumen

    @GET("historial")
    suspend fun historial(
        @Query("region") region: String,
        @Query("invocador") invocador: String
    ): List<PartidaResumen>

    @GET("analisis")
    suspend fun analisis(
        @Query("region") region: String,
        @Query("invocador") invocador: String
    ): AnalisisResumen

    @GET("campeones")
    suspend fun campeones(
        @Query("region") region: String,
        @Query("invocador") invocador: String
    ): List<CampeonDetalle>

    @GET("noticias")
    suspend fun noticias(): List<NoticiaEsport>

    @GET("detalle")
    suspend fun detalle(
        @Query("region") region: String,
        @Query("partidaId") partidaId: String
    ): PartidaDetalle
}
