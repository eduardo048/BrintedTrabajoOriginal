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
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth = Firebase.auth,
    private val firestore: FirebaseFirestore = Firebase.firestore
)
{
    val estadoSesion: Flow<Usuario?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val usuario = firebaseAuth.currentUser
            if (usuario != null) {
                trySend(
                    Usuario(
                        id = usuario.uid,
                        correo = usuario.email.orEmpty(),
                        nombreInvocador = usuario.displayName.orEmpty()
                    )
                )
            } else {
                trySend(null)
            }
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    suspend fun registrar(correo: String, contrasena: String, invocador: String): Result<Usuario> =
        runCatching {
            val resultado = auth.createUserWithEmailAndPassword(correo, contrasena).await()
            val usuarioFirebase = resultado.user ?: error("No se pudo crear el usuario.")
            guardarUsuarioEnFirestore(usuarioFirebase.uid, correo, invocador)
            usuarioFirebase.updateProfile(
                UserProfileChangeRequest.Builder()
                    .setDisplayName(invocador)
                    .build()
            ).await()
            Usuario(id = usuarioFirebase.uid, correo = correo, nombreInvocador = invocador)
        }

    suspend fun iniciarSesion(correo: String, contrasena: String): Result<Usuario> =
        runCatching {
            val resultado = auth.signInWithEmailAndPassword(correo, contrasena).await()
            val usuarioFirebase = resultado.user ?: error("Usuario o contraseña inválidos.")
            val invocador = obtenerNombreInvocador(usuarioFirebase.uid)
            Usuario(
                id = usuarioFirebase.uid,
                correo = usuarioFirebase.email.orEmpty(),
                nombreInvocador = invocador
            )
        }

    fun cerrarSesion() = auth.signOut()

    private suspend fun guardarUsuarioEnFirestore(uid: String, correo: String, invocador: String) {
        val datos = mapOf(
            "correo" to correo,
            "nombreInvocador" to invocador,
            "region" to "euw1",
            "creadoEn" to System.currentTimeMillis(),
            "idioma" to "es",
            "tema" to "oscuro",
            "opciones" to emptyMap<String, Any>()
        )
        firestore.collection("usuarios").document(uid).set(datos).await()
    }

    private suspend fun obtenerNombreInvocador(uid: String): String {
        val snapshot = firestore.collection("usuarios").document(uid).get().await()
        return snapshot.getString("nombreInvocador").orEmpty()
    }
}
