package com.example.brinted.core

import com.example.brinted.data.firebase.AuthRepository
import com.example.brinted.data.riot.RiotRepository
import com.example.brinted.data.riot.RiotRepositoryFactory

 // Clase de contenedor de dependencias que se inicializan perezosamente para toda la aplicación
object AppContainer {

    private const val BASE_FUNCTIONS = "https://riotproxy-gmcqwu52ta-uc.a.run.app/" // URL base para las funciones en la nube

    // Repositorio de autenticacióninicializado perezosamente
    val authRepository: AuthRepository by lazy {
        AuthRepository()
    }

    // Repositorio de Riot inicializado perezosamente usando la fábrica
    val riotRepository: RiotRepository by lazy {
        RiotRepositoryFactory.crear(BASE_FUNCTIONS)
    }
}
