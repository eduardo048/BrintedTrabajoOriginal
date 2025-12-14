package com.example.brinted.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.brinted.data.mock.MockData
import com.example.brinted.data.model.NoticiaEsport
import com.example.brinted.ui.components.NoticiaCard
import com.example.brinted.ui.theme.Fondo
import com.example.brinted.ui.theme.Tipografia

/** Pantalla de eSports: lista noticias y permite abrir la URL origen. */
@Composable
fun EsportsScreen(
    noticias: List<NoticiaEsport>,
    cargando: Boolean,
    onVerNoticia: (NoticiaEsport) -> Unit
) {
    val lista = if (noticias.isNotEmpty()) noticias else MockData.noticiasDemo
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Fondo)
            .padding(16.dp)
    ) {
        item { Text("eSports", style = Tipografia.headlineMedium, color = Color.White) }
        if (cargando) {
            item {
                Spacer(modifier = Modifier.height(6.dp))
                Text("Cargando noticias...", style = Tipografia.bodyMedium, color = com.example.brinted.ui.theme.GrisTexto)
            }
        }
        items(lista) { noticia ->
            NoticiaCard(noticia = noticia, onClick = onVerNoticia)
        }
        item { Spacer(modifier = Modifier.height(32.dp)) }
    }
}
