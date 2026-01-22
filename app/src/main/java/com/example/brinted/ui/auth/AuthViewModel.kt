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

data class AuthUiState(
    val usuario: Usuario? = null,
    val cargando: Boolean = false,
    val error: String? = null
)

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _estado = MutableStateFlow(AuthUiState())
    val estado: StateFlow<AuthUiState> = _estado.asStateFlow()

    init {
        viewModelScope.launch {
            repository.estadoSesion.collect { user ->
                _estado.update { it.copy(usuario = user) }
            }
        }
    }

    fun registrar(correo: String, contrasena: String, invocador: String, region: String) {
        if (correo.isBlank() || contrasena.isBlank() || invocador.isBlank()) {
            _estado.update { it.copy(error = "Por favor, completa todos los campos.") }
            return
        }

        viewModelScope.launch {
            _estado.update { it.copy(cargando = true, error = null) }
            val res = repository.registrar(correo, contrasena, invocador, region)
            res.fold(
                onSuccess = { _estado.update { it.copy(cargando = false) } },
                onFailure = { e -> _estado.update { it.copy(cargando = false, error = e.message) } }
            )
        }
    }

    fun iniciarSesion(correo: String, contrasena: String) {
        viewModelScope.launch {
            _estado.update { it.copy(cargando = true, error = null) }
            val res = repository.iniciarSesion(correo, contrasena)
            res.fold(
                onSuccess = { _estado.update { it.copy(cargando = false) } },
                onFailure = { e -> _estado.update { it.copy(cargando = false, error = e.message) } }
            )
        }
    }

    fun cerrarSesion() {
        repository.cerrarSesion()
    }

    companion object {
        fun factory(repository: AuthRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return AuthViewModel(repository) as T
                }
            }
    }
}
