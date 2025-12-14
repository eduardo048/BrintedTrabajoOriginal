package com.example.brinted.navigation

sealed class Ruta(val ruta: String, val titulo: String) {
    data object Bienvenida : Ruta("bienvenida", "Brinted")
    data object Login : Ruta("login", "Iniciar sesion")
    data object Registro : Ruta("registro", "Registrarse")
    data object Dashboard : Ruta("dashboard", "Dashboard")
    data object Historial : Ruta("historial", "Historial")
    data object Analisis : Ruta("analisis", "Analisis")
    data object Campeones : Ruta("campeones", "Campeones")
    data object Esports : Ruta("esports", "eSports")
    data object DetallePartida : Ruta("detallePartida/{partidaId}", "Detalle partida")
}

val RutasTabs = listOf(
    Ruta.Dashboard,
    Ruta.Historial,
    Ruta.Analisis,
    Ruta.Campeones,
    Ruta.Esports
)
