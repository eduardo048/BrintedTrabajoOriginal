package com.example.brinted.core

import com.example.brinted.data.firebase.autentificacionRepo
import com.example.brinted.data.riot.RiotRepositorio
import com.example.brinted.data.riot.RiotRepositorioFactorizar

//Clase que actúa como contenedor de dependencias para la aplicación
//Proporciona instancias de los repositorios necesarios
//para la autenticación y la interacción con la API de Riot Games
//Utiliza el patrón Singleton para asegurar que solo haya una instancia de cada repositorio
object AppContainer {

    private const val BASE_FUNCIONES = "https://riot-api-proxy.vercel.app/api/"  //URL base de las funciones en la nube

    //Repositorio de autenticación
    val autentificacionRepo: autentificacionRepo by lazy {
        autentificacionRepo() //Instancia del repositorio de autenticación
    }

    //Repositorio de Riot Games
    val riotRepositorio: RiotRepositorio by lazy {
        RiotRepositorioFactorizar.crear(BASE_FUNCIONES) //Instancia del repositorio de Riot Games
    }
}
