package com.example.brinted.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.brinted.data.firebase.AuthRepository
import com.example.brinted.data.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Estado de la UI para la autenticación
//Sirve para representar el estado actual de la interfaz de usuario en relación con la autenticación
// Contiene información sobre el usuario autenticado, si hay una operación en progreso y cualquier mensaje de error
// Es inmutable para garantizar que el estado no se modifique directamente desde fuera del ViewModel
data class AuthUiState(
    val usuario: Usuario? = null,  // Usuario autenticado
    val cargando: Boolean = false, // Indica si una operación está en progreso
    val error: String? = null      // Mensaje de error si ocurre algún problema
)

// ViewModel para manejar la lógica de autenticación
class AuthViewModel(private val repository: AuthRepository) : ViewModel() { // Inyección del repositorio de autenticación

    private val _estado = MutableStateFlow(AuthUiState()) // Estado mutable interno
    val estado: StateFlow<AuthUiState> = _estado.asStateFlow() // Exposición del estado como inmutable

    init { // Observa los cambios en el estado de la sesión
        viewModelScope.launch { // Lanzamiento de una coroutine en el scope del ViewModel
            repository.estadoSesion.collect { user -> // Colección del flujo de estado de sesión
                _estado.update { it.copy(usuario = user) } // Actualización del estado con el usuario actual
            }
        }
    }

    // Función para registrar un nuevo usuario
    fun registrar(correo: String, contrasena: String, invocador: String, region: String) { // Validación de campos
        if (correo.isBlank() || contrasena.isBlank() || invocador.isBlank()) { // Si algún campo está vacío
            _estado.update { it.copy(error = "Por favor, completa todos los campos.") } // Actualiza el estado con un mensaje de error
            return // Sale de la función
        }

        // Inicia el proceso de registro
        viewModelScope.launch { // Lanzamiento de una coroutine en el scope del ViewModel
            _estado.update { it.copy(cargando = true, error = null) } // Actualiza el estado para indicar que se está cargando
            val res = repository.registrar(correo, contrasena, invocador, region) // Llama al repositorio para registrar al usuario
            res.fold( // Manejo del resultado
                onSuccess = { _estado.update { it.copy(cargando = false) } }, // En caso de éxito, actualiza el estado para indicar que ya no se está cargando
                onFailure = { e -> _estado.update { it.copy(cargando = false, error = e.message) } } // En caso de fallo, actualiza el estado con el mensaje de error
            )
        }
    }

    // Función para iniciar sesión
    fun iniciarSesion(correo: String, contrasena: String) { // Validación de campos
        viewModelScope.launch { // Lanzamiento de una coroutine en el scope del ViewModel
            _estado.update { it.copy(cargando = true, error = null) } // Actualiza el estado para indicar que se está cargando
            val res = repository.iniciarSesion(correo, contrasena) // Llama al repositorio para iniciar sesión
            res.fold( // Manejo del resultado
                onSuccess = { _estado.update { it.copy(cargando = false) } }, // En caso de éxito, actualiza el estado para indicar que ya no se está cargando
                onFailure = { e -> _estado.update { it.copy(cargando = false, error = e.message) } } // En caso de fallo, actualiza el estado con el mensaje de error
            )
        }
    }

    // Función para cerrar sesión
    fun cerrarSesion() { // Llama al repositorio para cerrar sesión
        repository.cerrarSesion() // Llama al repositorio para cerrar sesión
    }

    // Factory para crear instancias de AuthViewModel con el repositorio inyectado
    companion object {
        fun factory(repository: AuthRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory { // Implementación de ViewModelProvider.Factory
                override fun <T : ViewModel> create(modelClass: Class<T>): T { // Creación del ViewModel
                    @Suppress("UNCHECKED_CAST")  // Supresión de la advertencia de conversión no verificada
                    return AuthViewModel(repository) as T // Retorna una nueva instancia de AuthViewModel
                }
            }
    }
}
