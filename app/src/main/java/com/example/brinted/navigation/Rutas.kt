package com.example.brinted.navigation

// Definicion de las rutas de la aplicacion
sealed class Ruta(val ruta: String, val titulo: String) {
    data object Bienvenida : Ruta("bienvenida", "Brinted") // Pantalla de bienvenida
    data object Login : Ruta("login", "Iniciar sesion") // Pantalla de inicio de sesion
    data object Registro : Ruta("registro", "Registrarse") // Pantalla de registro
    data object Dashboard : Ruta("dashboard", "Dashboard") // Pantalla principal despues de iniciar sesion
    data object Historial : Ruta("historial", "Historial") // Pantalla de historial de partidas
    data object Analisis : Ruta("analisis", "Analisis") // Pantalla de analisis de partidas
    data object Campeones : Ruta("campeones", "Campeones") // Pantalla de campeones
    data object Esports : Ruta("esports", "eSports") // Pantalla de eSports
    data object DetallePartida : Ruta("detallePartida/{partidaId}", "Detalle partida") // Pantalla de detalle de partida
}

// Lista de rutas principales para las pestañas de navegacion
val RutasTabs = listOf(
    Ruta.Dashboard, // Pantalla principal
    Ruta.Historial, // Pantalla de historial
    Ruta.Analisis, // Pantalla de analisis
    Ruta.Campeones, // Pantalla de campeones
    Ruta.Esports // Pantalla de eSports
)
