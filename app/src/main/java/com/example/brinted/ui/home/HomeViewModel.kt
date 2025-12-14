package com.example.brinted.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.brinted.data.model.AnalisisResumen
import com.example.brinted.data.model.CampeonDetalle
import com.example.brinted.data.model.DashboardResumen
import com.example.brinted.data.model.NoticiaEsport
import com.example.brinted.data.model.PartidaDetalle
import com.example.brinted.data.model.PartidaResumen
import com.example.brinted.data.riot.RiotFallbackProvider
import com.example.brinted.data.riot.ResultadoFallback
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DatosUiState(
    val dashboard: DashboardResumen? = null,
    val historial: List<PartidaResumen> = emptyList(),
    val analisis: AnalisisResumen? = null,
    val campeones: List<CampeonDetalle> = emptyList(),
    val noticias: List<NoticiaEsport> = emptyList(),
    val detallePartida: PartidaDetalle? = null,
    val detalleCargando: Boolean = false,
    val detalleError: String? = null,
    val cargando: Boolean = false,
    val error: String? = null,
    val avisoMock: String? = null
)

class HomeViewModel(
    private val riotProvider: RiotFallbackProvider
) : ViewModel() {

    private val _estado = MutableStateFlow(DatosUiState())
    val estado: StateFlow<DatosUiState> = _estado.asStateFlow()

    fun cargarTodo(invocador: String, region: String = "euw1") {
        viewModelScope.launch {
            _estado.update { it.copy(cargando = true, error = null) }
            val resultadoPrimero = runCatching { riotProvider.cargarTodo(invocador, region) }
            if (resultadoPrimero.isSuccess) {
                actualizarEstado(resultadoPrimero.getOrThrow())
            } else {
                // Reintento automático si falló la llamada
                val resultadoReintento = runCatching { riotProvider.cargarTodo(invocador, region) }
                if (resultadoReintento.isSuccess) {
                    actualizarEstado(resultadoReintento.getOrThrow())
                } else {
                    _estado.update {
                        it.copy(
                            cargando = false,
                            error = "No se pudo conectar con la API de Riot. Intenta de nuevo en unos segundos."
                        )
                    }
                }
            }
        }
    }

    private fun actualizarEstado(resultado: ResultadoFallback<DatosCompletos>) {
        val mensaje = if (resultado.usandoMock) {
            "Mostrando datos de prueba por fallo con Riot/Functions"
        } else {
            "Datos actualizados desde Riot"
        }
        _estado.update {
            it.copy(
                dashboard = resultado.dato.dashboard,
                historial = resultado.dato.historial,
                analisis = resultado.dato.analisis,
                campeones = resultado.dato.campeones,
                noticias = resultado.dato.noticias,
                cargando = false,
                detallePartida = null,
                detalleCargando = false,
                detalleError = null,
                avisoMock = mensaje
            )
        }
    }

    fun cargarDetalle(partidaId: String, region: String = "euw1") {
        viewModelScope.launch {
            _estado.update { it.copy(detalleCargando = true, detalleError = null) }
            val resultado = runCatching { riotProvider.cargarDetalle(partidaId, region) }
            resultado.fold(
                onSuccess = { res ->
                    _estado.update {
                        it.copy(
                            detallePartida = res.dato,
                            detalleCargando = false,
                            detalleError = if (res.usandoMock) "Mostrando datos de prueba por fallo con Riot/Functions" else null,
                            avisoMock = if (res.usandoMock) "Mostrando datos de prueba por fallo con Riot/Functions" else it.avisoMock
                        )
                    }
                },
                onFailure = {
                    _estado.update {
                        it.copy(
                            detalleCargando = false,
                            detalleError = "No se pudo cargar el detalle de la partida. Intenta de nuevo."
                        )
                    }
                }
            )
        }
    }

    companion object {
        fun factory(provider: RiotFallbackProvider): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return HomeViewModel(provider) as T
                }
            }
    }
}

data class DatosCompletos(
    val dashboard: DashboardResumen,
    val historial: List<PartidaResumen>,
    val analisis: AnalisisResumen,
    val campeones: List<CampeonDetalle>,
    val noticias: List<NoticiaEsport>
)
