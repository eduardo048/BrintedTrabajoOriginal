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
    val cargando: Boolean = false,
    val error: String? = null,
    val usuario: Usuario? = null
)

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _estado = MutableStateFlow(AuthUiState())
    val estado: StateFlow<AuthUiState> = _estado.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.estadoSesion.collect { usuario ->
                _estado.update { it.copy(usuario = usuario, cargando = false, error = null) }
            }
        }
    }

    /** Inicia sesión con correo y contraseña. */
    fun iniciarSesion(correo: String, contrasena: String) {
        viewModelScope.launch {
            _estado.update { it.copy(cargando = true, error = null) }
            val resultado = authRepository.iniciarSesion(correo, contrasena)
            _estado.update {
                resultado.fold(
                    onSuccess = { usuario -> it.copy(cargando = false, usuario = usuario, error = null) },
                    onFailure = { error -> it.copy(cargando = false, error = error.message) }
                )
            }
        }
    }

    /** Registra usuario nuevo y guarda perfil en Firestore. */
    fun registrar(correo: String, contrasena: String, invocador: String) {
        viewModelScope.launch {
            _estado.update { it.copy(cargando = true, error = null) }
            val resultado = authRepository.registrar(correo, contrasena, invocador)
            _estado.update {
                resultado.fold(
                    onSuccess = { usuario -> it.copy(cargando = false, usuario = usuario, error = null) },
                    onFailure = { error -> it.copy(cargando = false, error = error.message) }
                )
            }
        }
    }

    /** Cierra sesión en Firebase Auth y limpia estado local. */
    fun cerrarSesion() {
        authRepository.cerrarSesion()
        _estado.update { AuthUiState() }
    }

    companion object {
        fun factory(repo: AuthRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return AuthViewModel(repo) as T
                }
            }
    }
}
