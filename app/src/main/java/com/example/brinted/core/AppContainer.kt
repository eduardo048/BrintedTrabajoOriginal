package com.example.brinted.core

import com.example.brinted.data.firebase.AuthRepository
import com.example.brinted.data.riot.RiotRepository
import com.example.brinted.data.riot.RiotRepositoryFactory

//Clase que actúa como contenedor de dependencias para la aplicación
//Proporciona instancias de los repositorios necesarios
//para la autenticación y la interacción con la API de Riot Games
//Utiliza el patrón Singleton para asegurar que solo haya una instancia de cada repositorio
object AppContainer {

    private const val BASE_FUNCTIONS = "https://riot-api-proxy.vercel.app/api/"  //URL base de las funciones en la nube

    //Repositorio de autenticación
    val authRepository: AuthRepository by lazy {
        AuthRepository() //Instancia del repositorio de autenticación
    }

    //Repositorio de Riot Games
    val riotRepository: RiotRepository by lazy {
        RiotRepositoryFactory.crear(BASE_FUNCTIONS) //Instancia del repositorio de Riot Games
    }
}
