package com.example.brinted.core

import com.example.brinted.data.firebase.AuthRepository
import com.example.brinted.data.riot.RiotRepository
import com.example.brinted.data.riot.RiotRepositoryFactory

// Clase de contenedor de dependencias
object AppContainer {

    // Se ha añadido /api/ al final para que coincida con las rutas de Vercel
    private const val BASE_FUNCTIONS = "https://riot-api-proxy.vercel.app/api/" 

    val authRepository: AuthRepository by lazy {
        AuthRepository()
    }

    val riotRepository: RiotRepository by lazy {
        RiotRepositoryFactory.crear(BASE_FUNCTIONS)
    }
}
