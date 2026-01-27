package com.example.brinted.navigation

// Definición de las rutas de la aplicación
//Sirve para navegar entre las diferentes pantallas de la app
sealed class Ruta(val ruta: String, val titulo: String) {
    data object Bienvenida : Ruta("bienvenida", "Brinted")
    data object Login : Ruta("login", "Iniciar sesión")
    data object Registro : Ruta("registro", "Registrarse")
    data object Dashboard : Ruta("dashboard", "Dashboard")
    data object Historial : Ruta("historial", "Historial")
    data object Analisis : Ruta("analisis", "Análisis")
    data object Campeones : Ruta("campeones", "Campeones")
    data object Esports : Ruta("esports", "eSports")
    data object DetallePartida : Ruta("detallePartida/{partidaId}", "Detalle partida")
}
