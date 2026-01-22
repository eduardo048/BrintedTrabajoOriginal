package com.example.brinted.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.brinted.data.model.*
import com.example.brinted.data.riot.RiotRepository
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
    val cargando: Boolean = false,
    val error: String? = null,
    val avisoMock: String? = null,
    val detallePartida: PartidaDetalle? = null,
    val detalleCargando: Boolean = false,
    val detalleError: String? = null
)

class HomeViewModel(private val riotRepository: RiotRepository) : ViewModel() {

    private val _estado = MutableStateFlow(DatosUiState())
    val estado: StateFlow<DatosUiState> = _estado.asStateFlow()

    fun cargarTodo(invocador: String, region: String) {
        Log.d("BRINTED_DEBUG", "Iniciando carga para: $invocador en $region")
        viewModelScope.launch {
            _estado.update { it.copy(cargando = true, error = null) }
            
            try {
                val dashboard = riotRepository.cargarDashboard(invocador, region)
                val historial = riotRepository.cargarHistorial(invocador, region)
                val analisis = riotRepository.cargarAnalisis(invocador, region)
                val campeones = riotRepository.cargarCampeones(invocador, region)
                val noticias = riotRepository.cargarNoticias()
                actualizarEstado(
                    DatosCompletos(
                        dashboard = dashboard,
                        historial = historial,
                        analisis = analisis,
                        campeones = campeones,
                        noticias = noticias
                    )
                )
            } catch (e: Exception) {
                Log.e("BRINTED_DEBUG", "ERROR CRÍTICO EN CARGAR_TODO", e)
                _estado.update {
                    it.copy(
                        cargando = false,
                        error = "Error: ${e.localizedMessage ?: "Fallo de conexión"}"
                    )
                }
            }
        }
    }

    private fun actualizarEstado(resultado: DatosCompletos) {
        _estado.update {
            it.copy(
                dashboard = resultado.dashboard,
                historial = resultado.historial,
                analisis = resultado.analisis,
                campeones = resultado.campeones,
                noticias = resultado.noticias,
                cargando = false,
                avisoMock = null
            )
        }
    }

    fun cargarDetalle(partidaId: String, region: String, invocador: String) {
        viewModelScope.launch {
            _estado.update { it.copy(detalleCargando = true, detallePartida = null) }
            runCatching { riotRepository.cargarDetallePartida(partidaId, region, invocador) }
                .onSuccess { res -> _estado.update { it.copy(detallePartida = res, detalleCargando = false) } }
                .onFailure { e -> _estado.update { it.copy(detalleError = e.message, detalleCargando = false) } }
        }
    }

    companion object {
        fun factory(provider: RiotRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
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
