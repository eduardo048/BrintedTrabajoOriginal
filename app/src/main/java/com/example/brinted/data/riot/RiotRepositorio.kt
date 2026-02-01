package com.example.brinted.data.riot

import com.example.brinted.data.model.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

// Definición de la interfaz del repositorio
// Define los métodos para interactuar con los datos de Riot Games
// Cada metodo retorna un tipo de dato específico relacionado con la funcionalidad requerida.¡

interface RiotRepositorio {
    suspend fun cargarDashboard(invocador: String, region: String): DashboardResumen // Cambiado a DashboardResumen
    suspend fun cargarHistorial(invocador: String, region: String): List<PartidaResumen> // Cambiado a PartidaResumen
    suspend fun cargarAnalisis(invocador: String, region: String): AnalisisResumen // Cambiado a AnalisisResumen
    suspend fun cargarCampeones(invocador: String, region: String): List<CampeonDetalle> // Cambiado a CampeonDetalle
    suspend fun cargarNoticias(): List<NoticiaEsport> // Cambiado a NoticiaEsport
    suspend fun cargarDetallePartida(partidaId: String, region: String, invocador: String): PartidaDetalle // Cambiado a PartidaDetalle
}

// Implementación del repositorio que utiliza Retrofit para llamadas de red
class RiotRepositorioRemoto(private val service: RiotFunctionsService) : RiotRepositorio {
    override suspend fun cargarDashboard(invocador: String, region: String) = service.dashboard(region, invocador) // Retorna DashboardResumen
    override suspend fun cargarHistorial(invocador: String, region: String) = service.historial(region, invocador) // Retorna List<PartidaResumen>
    override suspend fun cargarAnalisis(invocador: String, region: String) = service.analisis(region, invocador) // Retorna AnalisisResumen
    override suspend fun cargarCampeones(invocador: String, region: String) = service.campeones(region, invocador) // Retorna List<CampeonDetalle>
    override suspend fun cargarNoticias() = service.noticias() // Retorna List<NoticiaEsport>
    override suspend fun cargarDetallePartida(partidaId: String, region: String, invocador: String) =  // Retorna PartidaDetalle
        service.detalle(region, partidaId, invocador) // Retorna PartidaDetalle
}

// Fábrica para crear instancias del repositorio
object RiotRepositorioFactorizar {
    // Metodo para crear una instancia del repositorio con configuración de red
    fun crear(baseUrl: String?): RiotRepositorio {
        val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build() // Configuración de Moshi con soporte para Kotlin
        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY } // Interceptor para logging de solicitudes HTTP
        val client = OkHttpClient.Builder().addInterceptor(logging).build() // Cliente HTTP con interceptor de logging
        val finalBase = if (baseUrl?.endsWith("/") == true) baseUrl else "$baseUrl/" // Asegura que la URL base termine con "/"

        // Construcción de Retrofit con la URL base, cliente HTTP y convertidor Moshi
        val retrofit = Retrofit.Builder()
            .baseUrl(finalBase) // Establece la URL base
            .client(client) // Establece el cliente HTTPs
            .addConverterFactory(MoshiConverterFactory.create(moshi)) // Agrega el convertidor Moshi
            .build() // Construye la instancia de Retrofit
            
        return RiotRepositorioRemoto(retrofit.create(RiotFunctionsService::class.java)) // Crea y retorna la instancia del repositorio remoto
    }
}
