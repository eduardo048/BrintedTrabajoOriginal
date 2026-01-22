package com.example.brinted.data.firebase

import com.example.brinted.data.model.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// Repositorio para manejar la autenticación y datos de usuario con Firebase
class AuthRepository(
    private val auth: FirebaseAuth = Firebase.auth, // Instancia de FirebaseAuth para autenticación
    private val firestore: FirebaseFirestore = Firebase.firestore // Instancia de FirebaseFirestore para base de datos
) {

    val estadoSesion: Flow<Usuario?> = callbackFlow { // Flujo que emite el estado de sesión del usuario
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth -> // Listener para cambios en el estado de autenticación
            val usuario = firebaseAuth.currentUser // Usuario actualmente autenticado
            if (usuario != null) { // Si hay un usuario autenticado
                launch {
                    val datos = obtenerDatosUsuario( // Obtener datos del usuario desde Firestore
                        uid = usuario.uid, // ID del usuario
                        correoFallback = usuario.email.orEmpty(), // Correo electrónico del usuario
                        nombreFallback = usuario.displayName.orEmpty() // Nombre del usuario
                    )
                    trySend(datos) // Enviar los datos del usuario al flujo
                }
            } else {
                trySend(null) // Si no hay usuario, enviar null
            }
        }
        auth.addAuthStateListener(listener) // Agregar el listener al FirebaseAuth
        awaitClose { auth.removeAuthStateListener(listener) } // Remover el listener cuando el flujo se cierra
    }

    // Función para registrar un nuevo usuario
    suspend fun registrar(correo: String, contrasena: String, invocador: String, region: String): Result<Usuario> =
        // Intentar crear un nuevo usuario y manejar posibles errores
        runCatching {
            val resultado = auth.createUserWithEmailAndPassword(correo, contrasena).await() // Crear usuario con correo y contraseña
            val usuarioFirebase = resultado.user ?: error("No se pudo crear el usuario.") // Obtener el usuario creado
            guardarUsuarioEnFirestore(usuarioFirebase.uid, correo, invocador, region) // Guardar datos adicionales en Firestore
            // Actualizar el perfil del usuario con el nombre de invocador
            usuarioFirebase.updateProfile(
                UserProfileChangeRequest.Builder() // Construir la solicitud de cambio de perfil
                    .setDisplayName(invocador) // Establecer el nombre de visualización
                    .build() // Construir la solicitud
            ).await() // Esperar a que se complete la actualización del perfil
            Usuario(id = usuarioFirebase.uid, correo = correo, nombreInvocador = invocador, region = region) // Devolver el objeto Usuario
        }

    // Función para iniciar sesión con correo y contraseña
    suspend fun iniciarSesion(correo: String, contrasena: String): Result<Usuario> =
        runCatching {
            val resultado = auth.signInWithEmailAndPassword(correo, contrasena).await() // Iniciar sesión con correo y contraseña
            val usuarioFirebase = resultado.user ?: error("Usuario o contraseña inválidos.") // Obtener el usuario autenticado
            // Obtener los datos del usuario desde Firestore
            obtenerDatosUsuario(
                uid = usuarioFirebase.uid, // ID del usuario
                correoFallback = usuarioFirebase.email.orEmpty(), // Correo electrónico del usuario
                nombreFallback = usuarioFirebase.displayName.orEmpty() // Nombre del usuario
            )
        }

    // Función para cerrar la sesión del usuario
    fun cerrarSesion() = auth.signOut()

    // Función privada para guardar los datos del usuario en Firestore
    private suspend fun guardarUsuarioEnFirestore(uid: String, correo: String, invocador: String, region: String) {
        // Crear un mapa con los datos del usuario
        val datos = mapOf(
            "correo" to correo,
            "nombreInvocador" to invocador,
            "region" to region,
            "creadoEn" to System.currentTimeMillis(),
            "idioma" to "es",
            "tema" to "oscuro",
            "opciones" to emptyMap<String, Any>()
        )
        firestore.collection("usuarios").document(uid).set(datos).await() // Guardar los datos en Firestore
    }

    // Función privada para obtener los datos del usuario desde Firestore
    private suspend fun obtenerDatosUsuario(
        uid: String,
        correoFallback: String,
        nombreFallback: String
    ): Usuario {
        val snapshot = firestore.collection("usuarios").document(uid).get().await() // Obtener el documento del usuario
        val invocador = snapshot.getString("nombreInvocador").orEmpty().ifBlank { nombreFallback } // Obtener el nombre de invocador
        val correo = snapshot.getString("correo").orEmpty().ifBlank { correoFallback } // Obtener el correo electrónico
        val region = snapshot.getString("region").orEmpty().ifBlank { "euw1" } // Obtener la región, con valor predeterminado "euw1"
        // Devolver el objeto Usuario con los datos obtenidos
        return Usuario(
            id = uid,
            correo = correo,
            nombreInvocador = invocador,
            region = region
        )
    }
}
