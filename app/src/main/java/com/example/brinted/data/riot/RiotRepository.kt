package com.example.brinted.data.riot

import com.example.brinted.data.mock.MockData
import com.example.brinted.data.model.AnalisisResumen
import com.example.brinted.data.model.CampeonDetalle
import com.example.brinted.data.model.DashboardResumen
import com.example.brinted.data.model.NoticiaEsport
import com.example.brinted.data.model.PartidaDetalle
import com.example.brinted.data.model.PartidaResumen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

interface RiotRepository {
    suspend fun cargarDashboard(invocador: String, region: String): DashboardResumen
    suspend fun cargarHistorial(invocador: String, region: String): List<PartidaResumen>
    suspend fun cargarAnalisis(invocador: String, region: String): AnalisisResumen
    suspend fun cargarCampeones(invocador: String, region: String): List<CampeonDetalle>
    suspend fun cargarNoticias(): List<NoticiaEsport>
    suspend fun cargarDetallePartida(partidaId: String, region: String): PartidaDetalle
}

class RiotRepositoryRemoto(
    private val service: RiotFunctionsService
) : RiotRepository {
    override suspend fun cargarDashboard(invocador: String, region: String): DashboardResumen =
        service.dashboard(region, invocador)

    override suspend fun cargarHistorial(invocador: String, region: String): List<PartidaResumen> =
        service.historial(region, invocador)

    override suspend fun cargarAnalisis(invocador: String, region: String): AnalisisResumen =
        service.analisis(region, invocador)

    override suspend fun cargarCampeones(invocador: String, region: String): List<CampeonDetalle> =
        service.campeones(region, invocador)

    override suspend fun cargarNoticias(): List<NoticiaEsport> = service.noticias()

    override suspend fun cargarDetallePartida(partidaId: String, region: String): PartidaDetalle =
        service.detalle(region, partidaId)
}

class RiotRepositoryMock : RiotRepository {
    override suspend fun cargarDashboard(invocador: String, region: String): DashboardResumen =
        withContext(Dispatchers.Default) { MockData.dashboard }

    override suspend fun cargarHistorial(invocador: String, region: String): List<PartidaResumen> =
        withContext(Dispatchers.Default) { MockData.partidasDemo }

    override suspend fun cargarAnalisis(invocador: String, region: String): AnalisisResumen =
        withContext(Dispatchers.Default) { MockData.analisisDemo }

    override suspend fun cargarCampeones(invocador: String, region: String): List<CampeonDetalle> =
        withContext(Dispatchers.Default) { MockData.campeonesDemo }

    override suspend fun cargarNoticias(): List<NoticiaEsport> =
        withContext(Dispatchers.Default) { MockData.noticiasDemo }

    override suspend fun cargarDetallePartida(partidaId: String, region: String): PartidaDetalle =
        withContext(Dispatchers.Default) { MockData.detallePorId(partidaId) }
}

object RiotRepositoryFactory {
    fun crear(baseUrl: String?, usarMock: Boolean = false): RiotRepository {
        if (usarMock || baseUrl.isNullOrBlank()) return RiotRepositoryMock()
        val base = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
        val finalBase = base + "riotProxy/"
        val retrofit = Retrofit.Builder()
            .baseUrl(finalBase)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
        return RiotRepositoryRemoto(retrofit.create(RiotFunctionsService::class.java))
    }
}
