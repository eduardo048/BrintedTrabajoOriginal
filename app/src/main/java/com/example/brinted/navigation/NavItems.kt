package com.example.brinted.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Newspaper
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.ui.graphics.vector.ImageVector

// Definición de los elementos de navegación con sus rutas, iconos y etiquetas
// Cada elemento representa una sección diferente de la aplicación
// utilizando iconos predeterminados de Material Icons
data class NavegacionItem(val ruta: Ruta, val icono: ImageVector, val etiqueta: String) // Clase de datos para un elemento de navegación

val itemsNavegacion = listOf(
    NavegacionItem(Ruta.Historial, Icons.Outlined.History, "Historial"), // Elemento de navegación para el historial
    NavegacionItem(Ruta.Analisis, Icons.Outlined.Analytics, "Análisis"), // Elemento de navegación para el análisis
    NavegacionItem(Ruta.Dashboard, Icons.Outlined.Category, "Inicio"), // Elemento de navegación para el dashboard
    NavegacionItem(Ruta.Campeones, Icons.Outlined.Psychology, "Campeones"), // Elemento de navegación para los campeones
    NavegacionItem(Ruta.Esports, Icons.Outlined.Newspaper, "eSports") // Elemento de navegación para eSports
)
