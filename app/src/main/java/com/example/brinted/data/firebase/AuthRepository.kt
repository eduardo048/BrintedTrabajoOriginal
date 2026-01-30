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

// Repositorio para manejar la autenticación y gestión de usuarios con Firebase
// Proporciona funciones para registrar, iniciar sesión, cerrar sesión y observar el estado de la sesión del usuario
// Utiliza Firebase Authentication y Firestore para almacenar y recuperar datos de usuarios
// Emite el estado de la sesión del usuario como un flujo de datos
class AuthRepository(
    private val auth: FirebaseAuth = Firebase.auth, // Instancia de FirebaseAuth
    private val firestore: FirebaseFirestore = Firebase.firestore // Instancia de FirebaseFirestore
) {

    val estadoSesion: Flow<Usuario?> = callbackFlow { // Flujo que emite el estado de la sesión del usuario
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->// Escucha los cambios en el estado de autenticación
            val usuario = firebaseAuth.currentUser // Obtiene el usuario actual
            if (usuario != null) { // Si hay un usuario autenticado
                launch {
                    val datos = obtenerDatosUsuario( // Obtiene los datos del usuario desde Firestore
                        uid = usuario.uid, // ID del usuario
                        correoFallback = usuario.email.orEmpty(), // Correo electrónico del usuario
                        nombreFallback = usuario.displayName.orEmpty() // Nombre del usuario
                    )
                    trySend(datos) // Envía los datos del usuario al flujo
                }
            } else { // Si no hay usuario autenticado
                trySend(null) // Envía null al flujo
            }
        }
        auth.addAuthStateListener(listener) // Agrega el listener al FirebaseAuth
        awaitClose { auth.removeAuthStateListener(listener) } // Elimina el listener cuando el flujo se cierra
    }

    // Función para registrar un nuevo usuario
    suspend fun registrar(correo: String, contrasena: String, invocador: String, region: String): Result<Usuario> =
        runCatching { // Maneja posibles excepciones
            val resultado = auth.createUserWithEmailAndPassword(correo, contrasena).await() // Crea un nuevo usuario con correo y contraseña
            val usuarioFirebase = resultado.user ?: error("No se pudo crear el usuario.") // Obtiene el usuario creado o lanza un error
            guardarUsuarioEnFirestore(usuarioFirebase.uid, correo, invocador, region) // Guarda los datos del usuario en Firestore
            
            usuarioFirebase.updateProfile( // Actualiza el perfil del usuario con el nombre de invocador
                UserProfileChangeRequest.Builder() // Construye la solicitud de cambio de perfil
                    .setDisplayName(invocador) // Establece el nombre de invocador
                    .build() // Construye la solicitud
            ).await() // Espera a que se complete la actualización
            
            Usuario(id = usuarioFirebase.uid, correo = correo, nombreInvocador = invocador, region = region) // Devuelve el objeto Usuario
        }

    // Función para iniciar sesión con correo y contraseña
    suspend fun iniciarSesion(correo: String, contrasena: String): Result<Usuario> =
        runCatching { // Maneja posibles excepciones
            val resultado = auth.signInWithEmailAndPassword(correo, contrasena).await() // Inicia sesión con correo y contraseña
            val usuarioFirebase = resultado.user ?: error("Usuario o contraseña inválidos.") // Obtiene el usuario autenticado o lanza un error
            obtenerDatosUsuario( // Obtiene los datos del usuario desde Firestore
                uid = usuarioFirebase.uid, // ID del usuario
                correoFallback = usuarioFirebase.email.orEmpty(), // Correo electrónico del usuario
                nombreFallback = usuarioFirebase.displayName.orEmpty() // Nombre del usuario
            )
        }

    fun cerrarSesion() = auth.signOut() // Función para cerrar la sesión del usuario

    // Función para guardar los datos del usuario en Firestore
    private suspend fun guardarUsuarioEnFirestore(uid: String, correo: String, invocador: String, region: String) {
        val datos = mapOf( // Mapa con los datos del usuario
            "correo" to correo, // Correo electrónico
            "nombreInvocador" to invocador, // Nombre de invocador
            "region" to region, // Región del usuario
            "creadoEn" to System.currentTimeMillis() // Marca de tiempo de creación
        )
        firestore.collection("usuarios").document(uid).set(datos).await() // Guarda los datos en Firestore
    }

    // Función para obtener los datos del usuario desde Firestore
    private suspend fun obtenerDatosUsuario(
        uid: String, // ID del usuario
        correoFallback: String, // Correo electrónico de respaldo
        nombreFallback: String // Nombre de invocador de respaldo
    ): Usuario { // Devuelve un objeto Usuario
        val snapshot = firestore.collection("usuarios").document(uid).get().await() // Obtiene el documento del usuario desde Firestore
        val invocador = snapshot.getString("nombreInvocador").orEmpty().ifBlank { nombreFallback } // Obtiene el nombre de invocador o usa el de respaldo
        val correo = snapshot.getString("correo").orEmpty().ifBlank { correoFallback } // Obtiene el correo electrónico o usa el de respaldo
        val region = snapshot.getString("region").orEmpty().ifBlank { "euw1" } // Obtiene la región o usa "euw1" como valor predeterminado

        // Devuelve el objeto Usuario con los datos obtenidos
        return Usuario(
            id = uid, // ID del usuario
            correo = correo, // Correo electrónico
            nombreInvocador = invocador, // Nombre de invocador
            region = region // Región del usuario
        )
    }
}
