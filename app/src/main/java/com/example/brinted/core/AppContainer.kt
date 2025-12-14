package com.example.brinted.core

import com.example.brinted.data.firebase.AuthRepository
import com.example.brinted.data.riot.RiotFallbackProvider
import com.example.brinted.data.riot.RiotRepository
import com.example.brinted.data.riot.RiotRepositoryFactory
import com.example.brinted.data.riot.RiotRepositoryMock

object AppContainer {
    // Cambia la URL cuando despliegues tus Cloud Functions (ej. us-central1)
    private const val BASE_FUNCTIONS =
        "https://riotproxy-gmcqwu52ta-uc.a.run.app/"

    val authRepository: AuthRepository by lazy { AuthRepository() }

    private val riotRepoRemoto: RiotRepository by lazy {
        RiotRepositoryFactory.crear(BASE_FUNCTIONS, usarMock = false)
    }
    private val riotRepoMock: RiotRepository by lazy { RiotRepositoryMock() }

    // Activamos fallback para que la app muestre mock si la API de Riot falla.
    val riotProvider: RiotFallbackProvider by lazy { RiotFallbackProvider(riotRepoRemoto, riotRepoMock, permitirFallback = true) }
}
