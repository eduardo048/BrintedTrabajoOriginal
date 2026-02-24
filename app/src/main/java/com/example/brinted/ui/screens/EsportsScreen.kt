package com.example.brinted.ui.screens

import androidx.compose.foundation.background
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
import com.example.brinted.data.model.NoticiaEsport
import com.example.brinted.ui.components.NoticiaCard
import com.example.brinted.ui.theme.Fondo
import com.example.brinted.ui.theme.GrisTexto
import com.example.brinted.ui.theme.Tipografia

// Pantalla de noticias de Esports que muestra una lista de noticias de Esports
@Composable
fun EsportsScreen( // Pantalla de noticias de Esports
    noticias: List<NoticiaEsport>, // Lista de noticias de Esports
    cargando: Boolean, // Indicador de carga
    onVerNoticia: (NoticiaEsport) -> Unit // Acción a realizar al hacer clic en una noticia
) {
    LazyColumn( // Lista perezosa para el contenido
        modifier = Modifier // Aplicación del modificador
            .fillMaxSize() // Ocupa todo el espacio disponible
            .background(Fondo) // Fondo de la pantalla
            .padding(16.dp) // Espaciado interno
    ) {
        item { Text("eSports", style = Tipografia.headlineMedium, color = Color.White) } // Título de la pantalla
        
        if (cargando) { // Si está cargando datos
            item { // Mensaje de carga
                Spacer(modifier = Modifier.height(6.dp)) // Espacio entre el título y el mensaje
                Text("Cargando noticias...", style = Tipografia.bodyMedium, color = GrisTexto) // Texto de carga
            }
        }
        
        if (noticias.isEmpty() && !cargando) { // Si no hay noticias y no está cargando
            item { // Mensaje de no hay noticias
                Spacer(modifier = Modifier.height(6.dp)) // Espacio entre el título y el mensaje
                Text("No hay noticias disponibles.", style = Tipografia.bodyMedium, color = GrisTexto) // Texto de no hay noticias
            }
        } else { // Si hay noticias
            items(noticias) { noticia -> // Itera sobre cada noticia
                NoticiaCard(noticia = noticia, onClick = onVerNoticia) // Componente de tarjeta de noticia
            }
        }
        
        item { Spacer(modifier = Modifier.height(32.dp)) } // Espacio inferior
    }
}
