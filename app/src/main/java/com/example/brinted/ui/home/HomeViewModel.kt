package com.example.brinted.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.brinted.data.model.*
import com.example.brinted.data.riot.RiotRepositorio
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Estado UI que contiene todos los datos necesarios para la pantalla principal
//Sirve como un contenedor para los diferentes datos que se mostrarán en la UI.
data class DatosUiState( // Estado inicial con valores por defecto
    val dashboard: DashboardResumen? = null,
    val historial: List<PartidaResumen> = emptyList(),
    val analisis: AnalisisResumen? = null,
    val campeones: List<CampeonDetalle> = emptyList(),
    val noticias: List<NoticiaEsport> = emptyList(),
    val cargando: Boolean = false,
    val error: String? = null,
    val detallePartida: PartidaDetalle? = null,
    val detalleCargando: Boolean = false,
    val detalleError: String? = null
)

// ViewModel que maneja la lógica de negocio y el estado UI para la pantalla principal
class HomeViewModel(private val riotRepositorio: RiotRepositorio) : ViewModel() {

    private val _estado = MutableStateFlow(DatosUiState()) // Exposición del estado como StateFlow inmutable
    val estado: StateFlow<DatosUiState> = _estado.asStateFlow() // Exposición del estado como StateFlow inmutable

    fun cargarTodo(invocador: String, region: String) { // Función para cargar todos los datos necesarios
        Log.d("BRINTED_DEBUG", "Iniciando carga para: $invocador en $region") // Log para depuración
        viewModelScope.launch {// Lanzamiento de una coroutine en el scope del ViewModel
            _estado.update { it.copy(cargando = true, error = null) } // Actualización del estado para indicar que se está cargando
            
            try {
                val dashboard = riotRepositorio.cargarDashboard(invocador, region) // Carga del resumen del dashboard
                val historial = riotRepositorio.cargarHistorial(invocador, region) // Carga del historial de partidas
                val analisis = riotRepositorio.cargarAnalisis(invocador, region) // Carga del análisis de rendimiento
                val campeones = riotRepositorio.cargarCampeones(invocador, region) // Carga de detalles de campeones
                val noticias = riotRepositorio.cargarNoticias() // Carga de noticias de esports
                actualizarEstado( // Actualización del estado con los datos cargados
                    DatosCompletos( // Creación de un objeto DatosCompletos con los datos obtenidos
                        dashboard = dashboard, // Asignación del resumen del dashboard
                        historial = historial, // Asignación del historial de partidas
                        analisis = analisis, // Asignación del análisis de rendimiento
                        campeones = campeones, // Asignación de detalles de campeones
                        noticias = noticias // Asignación de noticias de esports
                    )
                )
            } catch (e: Exception) {
                Log.e("BRINTED_DEBUG", "ERROR CRÍTICO EN CARGAR_TODO", e) // Log del error para depuración
                _estado.update { // Actualización del estado en caso de error
                    it.copy(
                        cargando = false, // Indicación de que la carga ha finalizado
                        error = "Error: ${e.localizedMessage ?: "Fallo de conexión"}" // Mensaje de error
                    )
                }
            }
        }
    }

    // Función privada para actualizar el estado UI con los datos cargados
    private fun actualizarEstado(resultado: DatosCompletos) {
        _estado.update { // Actualización del estado con los datos proporcionados
            it.copy(
                dashboard = resultado.dashboard, // Asignación del resumen del dashboard
                historial = resultado.historial, // Asignación del historial de partidas
                analisis = resultado.analisis, // Asignación del análisis de rendimiento
                campeones = resultado.campeones, // Asignación de detalles de campeones
                noticias = resultado.noticias, // Asignación de noticias de esports
                cargando = false, // Indicación de que la carga ha finalizado
            )
        }
    }

    // Función para cargar el detalle de una partida específica
    fun cargarDetalle(partidaId: String, region: String, invocador: String) {
        viewModelScope.launch { // Lanzamiento de una coroutine en el scope del ViewModel
            _estado.update { it.copy(detalleCargando = true, detallePartida = null) } // Actualización del estado para indicar que se está cargando el detalle
            runCatching { riotRepositorio.cargarDetallePartida(partidaId, region, invocador) } // Intento de cargar el detalle de la partida
                .onSuccess { res -> _estado.update { it.copy(detallePartida = res, detalleCargando = false) } } // Actualización del estado con el detalle cargado
                .onFailure { e -> _estado.update { it.copy(detalleError = e.message, detalleCargando = false) } } // Actualización del estado en caso de error
        }
    }

    // Factory para crear instancias de HomeViewModel con la dependencia RiotRepository
    companion object {
        fun factory(provider: RiotRepositorio): ViewModelProvider.Factory = // Función que devuelve un ViewModelProvider.Factory
            object : ViewModelProvider.Factory { // Implementación anónima de ViewModelProvider.Factory
                @Suppress("UNCHECKED_CAST") // Supresión de advertencias de conversión de tipos
                override fun <T : ViewModel> create(modelClass: Class<T>): T { // Función para crear una instancia del ViewModel
                    return HomeViewModel(provider) as T // Creación y retorno de una instancia de HomeViewModel
                }
            }
    }
}

// Clase de datos que agrupa todos los datos necesarios para la pantalla principal
data class DatosCompletos(
    val dashboard: DashboardResumen,
    val historial: List<PartidaResumen>,
    val analisis: AnalisisResumen,
    val campeones: List<CampeonDetalle>,
    val noticias: List<NoticiaEsport>
)
