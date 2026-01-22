package com.example.brinted.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val fuenteGeneral = FontFamily.SansSerif

val Tipografia = Typography(
    displayMedium = TextStyle(
        fontFamily = fuenteGeneral,
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = fuenteGeneral,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = fuenteGeneral,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = fuenteGeneral,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = fuenteGeneral,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = fuenteGeneral,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = fuenteGeneral,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = fuenteGeneral,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = fuenteGeneral,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = fuenteGeneral,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
    )
)
