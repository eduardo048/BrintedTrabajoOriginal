package com.example.brinted.data.mock

import com.example.brinted.data.model.AnalisisResumen
import com.example.brinted.data.model.CampeonDetalle
import com.example.brinted.data.model.CompaneroReciente
import com.example.brinted.data.model.DashboardResumen
import com.example.brinted.data.model.EstadisticaClave
import com.example.brinted.data.model.EstadisticasAvanzadas
import com.example.brinted.data.model.JugadorPartida
import com.example.brinted.data.model.NoticiaEsport
import com.example.brinted.data.model.PartidaDetalle
import com.example.brinted.data.model.PartidaResumen
import com.example.brinted.data.model.ResultadoPartida
import com.example.brinted.data.model.Usuario

object MockData {
    val usuarioDemo = Usuario(
        id = "demo",
        correo = "demo@correo.com",
        nombreInvocador = "InvocadorMaestro"
    )

    val estadisticasDemo = EstadisticasAvanzadas(
        kdaPromedio = 3.5,
        csPorMin = 6.8,
        oroPromedio = 14500,
        duracionPromedio = "32m 15s",
        rachaVictorias = 5,
        mejorKda = "18/2/12",
        tasaVictorias = 58,
        nivel = 345
    )

    val companerosDemo = listOf(
        CompaneroReciente("XGamerProX", "Jungla", "https://ddragon.leagueoflegends.com/cdn/img/champion/splash/LeeSin_0.jpg"),
        CompaneroReciente("MidLaneKing", "Mid", "https://ddragon.leagueoflegends.com/cdn/img/champion/splash/Ahri_0.jpg"),
        CompaneroReciente("BotCarry", "ADC", "https://ddragon.leagueoflegends.com/cdn/img/champion/splash/Ezreal_0.jpg"),
        CompaneroReciente("SupportGG", "Soporte", "https://ddragon.leagueoflegends.com/cdn/img/champion/splash/Thresh_0.jpg"),
    )

    val campeonesDemo = listOf(
        CampeonDetalle("Ahri", 120, 65, kda = "4.2/2.1/7.8", imagen = "https://ddragon.leagueoflegends.com/cdn/img/champion/splash/Ahri_0.jpg"),
        CampeonDetalle("Lee Sin", 98, 59, kda = "3.8/3.5/9.2", imagen = "https://ddragon.leagueoflegends.com/cdn/img/champion/splash/LeeSin_0.jpg"),
        CampeonDetalle("Jinx", 86, 62, kda = "5.1/2.9/9.1", imagen = "https://ddragon.leagueoflegends.com/cdn/img/champion/splash/Jinx_0.jpg"),
        CampeonDetalle("Ezreal", 110, 60, kda = "4.9/3.1/8.4", imagen = "https://ddragon.leagueoflegends.com/cdn/img/champion/splash/Ezreal_0.jpg"),
    )

    val partidasDemo = listOf(
        PartidaResumen("1", "Kai'Sa", ResultadoPartida.VICTORIA, "12/3/8", "35m 20s", "hace 2h", "https://ddragon.leagueoflegends.com/cdn/img/champion/splash/Kaisa_0.jpg"),
        PartidaResumen("2", "Sett", ResultadoPartida.DERROTA, "5/7/10", "28m 15s", "hace 5h", "https://ddragon.leagueoflegends.com/cdn/img/champion/splash/Sett_0.jpg"),
        PartidaResumen("3", "Lux", ResultadoPartida.VICTORIA, "8/2/15", "40m 05s", "ayer", "https://ddragon.leagueoflegends.com/cdn/img/champion/splash/Lux_0.jpg"),
        PartidaResumen("4", "Ezreal", ResultadoPartida.VICTORIA, "10/1/9", "30m 45s", "hace 2 dias", "https://ddragon.leagueoflegends.com/cdn/img/champion/splash/Ezreal_0.jpg"),
    )

    val analisisDemo = AnalisisResumen(
        kdaPorPartida = listOf(3.5, 4.1, 2.3, 4.8, 5.5, 3.9, 4.2, 2.8, 5.7, 4.6),
        metricas = listOf(
            EstadisticaClave("Tasa de Victoria Reciente", "58%", tendencia = "↑"),
            EstadisticaClave("KDA Promedio", "3.9"),
            EstadisticaClave("CS/min", "6.7"),
            EstadisticaClave("Duracion Media", "32 min"),
            EstadisticaClave("Racha mas Larga", "7"),
            EstadisticaClave("Mejor KDA", "5.5"),
            EstadisticaClave("Mejor Campeon", "Ezreal"),
            EstadisticaClave("Dano por Partida", "25.3K"),
        )
    )

    val jugadoresAzulDemo = listOf(
        JugadorPartida("Jugador00", "Top", campeon = "Ornn", kda = "5/2/7", dano = "12.3K"),
        JugadorPartida("Jugador01", "Jungla", campeon = "Vi", kda = "4/5/9", dano = "11.1K"),
        JugadorPartida("Jugador02", "Mid", campeon = "Ahri", kda = "9/3/10", dano = "18.2K"),
        JugadorPartida("Jugador03", "ADC", campeon = "Xayah", kda = "12/2/6", dano = "22.4K"),
        JugadorPartida("Jugador04", "Soporte", campeon = "Thresh", kda = "1/4/18", dano = "8.3K")
    )

    val jugadoresRojoDemo = listOf(
        JugadorPartida("Jugador10", "Top", campeon = "Garen", kda = "2/6/4", dano = "9.7K"),
        JugadorPartida("Jugador11", "Jungla", campeon = "Lee Sin", kda = "3/8/6", dano = "10.2K"),
        JugadorPartida("Jugador12", "Mid", campeon = "Zed", kda = "7/5/7", dano = "15.5K"),
        JugadorPartida("Jugador13", "ADC", campeon = "Ashe", kda = "6/4/5", dano = "14.8K"),
        JugadorPartida("Jugador14", "Soporte", campeon = "Lulu", kda = "0/7/12", dano = "6.1K")
    )

    val detalleDemo = PartidaDetalle(
        id = "1",
        campeonPrincipal = "Kai'Sa",
        resultado = ResultadoPartida.VICTORIA,
        kda = "12/3/8",
        duracion = "35m 20s",
        icono = "https://ddragon.leagueoflegends.com/cdn/img/champion/splash/Kaisa_0.jpg",
        equipoAzul = jugadoresAzulDemo,
        equipoRojo = jugadoresRojoDemo,
        metricasGlobales = listOf(
            EstadisticaClave("Oro total", "72.4K"),
            EstadisticaClave("Torres", "9 vs 3"),
            EstadisticaClave("Objetivos", "3 Dragones, 1 Baron")
        )
    )

    fun detallePorId(partidaId: String): PartidaDetalle {
        val resumen = partidasDemo.firstOrNull { it.id == partidaId } ?: partidasDemo.first()
        return detalleDemo.copy(
            id = partidaId,
            campeonPrincipal = resumen.campeon,
            resultado = resumen.resultado,
            kda = resumen.kda,
            duracion = resumen.duracion,
            icono = resumen.icono
        )
    }

    val noticiasDemo = listOf(
        NoticiaEsport(
            id = "1",
            titulo = "T1 se corona campeon de la LCK Primavera 2024",
            descripcion = "T1 demuestra su dominio con una actuacion impecable, asegurando su lugar en el MSI.",
            imagen = "https://images.unsplash.com/photo-1489515217757-5fd1be406fef?auto=format&fit=crop&w=1600&q=80&sat=-40&blend=7c3aed&blend-mode=multiply&blend-alpha=35",
            url = "https://lolesports.com/"
        ),
        NoticiaEsport(
            id = "2",
            titulo = "MSI 2024: Guia completa de equipos y enfrentamientos",
            descripcion = "Conoce a los equipos y horarios clave del MSI 2024 en esta guia rapida.",
            imagen = "https://images.unsplash.com/photo-1581291518857-4e27b48ff24e?auto=format&fit=crop&w=1600&q=80&sat=-30&blend=7c3aed&blend-mode=multiply&blend-alpha=35",
            url = "https://lolesports.com/"
        )
    )

    val dashboard = DashboardResumen(
        invocador = usuarioDemo,
        estadisticas = estadisticasDemo,
        companeros = companerosDemo,
        campeones = campeonesDemo,
        partidas = partidasDemo
    )
}
