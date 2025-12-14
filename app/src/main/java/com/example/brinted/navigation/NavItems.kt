package com.example.brinted.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Newspaper
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.ui.graphics.vector.ImageVector

data class NavItem(val ruta: Ruta, val icono: ImageVector, val etiqueta: String)

val itemsNavegacion = listOf(
    NavItem(Ruta.Historial, Icons.Outlined.History, "Historial"),
    NavItem(Ruta.Analisis, Icons.Outlined.Analytics, "Análisis"),
    NavItem(Ruta.Dashboard, Icons.Outlined.Category, "Inicio"),
    NavItem(Ruta.Campeones, Icons.Outlined.Psychology, "Campeones"),
    NavItem(Ruta.Esports, Icons.Outlined.Newspaper, "eSports")
)
