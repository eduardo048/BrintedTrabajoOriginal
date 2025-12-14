package com.example.brinted.data.riot

import com.example.brinted.ui.home.DatosCompletos
import com.example.brinted.data.model.PartidaDetalle

data class ResultadoFallback<T>(
    val dato: T,
    val usandoMock: Boolean
)

/**
 * Proveedor que intenta consumir el backend (Functions + Riot) y
 * si falla devuelve datos de prueba junto con un indicador de fallback.
 */
class RiotFallbackProvider(
    private val remoto: RiotRepository,
    private val mock: RiotRepository = RiotRepositoryMock(),
    private val permitirFallback: Boolean = true
) {
    /** Detalle de partida con intento remoto y respaldo mock. */
    suspend fun cargarDetalle(partidaId: String, region: String): ResultadoFallback<PartidaDetalle> {
        return cargaConFallback(
            remoto = { remoto.cargarDetallePartida(partidaId, region) },
            respaldo = { mock.cargarDetallePartida(partidaId, region) }
        )
    }

    /** Carga todo el dashboard (dashboard, historial, análisis, campeones, noticias). */
    suspend fun cargarTodo(invocador: String, region: String): ResultadoFallback<DatosCompletos> {
        val dashboard = cargaConFallback(
            remoto = { remoto.cargarDashboard(invocador, region) },
            respaldo = { mock.cargarDashboard(invocador, region) }
        )
        val historial = cargaConFallback(
            remoto = { remoto.cargarHistorial(invocador, region) },
            respaldo = { mock.cargarHistorial(invocador, region) }
        )
        val analisis = cargaConFallback(
            remoto = { remoto.cargarAnalisis(invocador, region) },
            respaldo = { mock.cargarAnalisis(invocador, region) }
        )
        val campeones = cargaConFallback(
            remoto = { remoto.cargarCampeones(invocador, region) },
            respaldo = { mock.cargarCampeones(invocador, region) }
        )
        val noticias = cargaConFallback(
            remoto = { remoto.cargarNoticias() },
            respaldo = { mock.cargarNoticias() }
        )

        val usandoMock = dashboard.usandoMock || historial.usandoMock || analisis.usandoMock || campeones.usandoMock || noticias.usandoMock

        return ResultadoFallback(
            dato = DatosCompletos(
                dashboard = dashboard.dato,
                historial = historial.dato,
                analisis = analisis.dato,
                campeones = campeones.dato,
                noticias = noticias.dato
            ),
            usandoMock = usandoMock
        )
    }

    /** Helper para intentar remoto y caer a mock si se permite. */
    private suspend fun <T> cargaConFallback(
        remoto: suspend () -> T,
        respaldo: suspend () -> T
    ): ResultadoFallback<T> {
        return runCatching { remoto() }
            .fold(
                onSuccess = { ResultadoFallback(it, false) },
                onFailure = { error ->
                    if (!permitirFallback) throw error
                    ResultadoFallback(respaldo(), true)
                }
            )
    }
}
