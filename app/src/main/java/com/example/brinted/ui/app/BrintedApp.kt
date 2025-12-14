package com.example.brinted.ui.app

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.brinted.core.AppContainer
import com.example.brinted.data.mock.MockData
import com.example.brinted.navigation.Ruta
import com.example.brinted.navigation.itemsNavegacion
import com.example.brinted.ui.auth.AuthViewModel
import com.example.brinted.ui.auth.LoginScreen
import com.example.brinted.ui.auth.RegistroScreen
import com.example.brinted.ui.home.HomeViewModel
import com.example.brinted.ui.screens.AnalisisScreen
import com.example.brinted.ui.screens.BienvenidaScreen
import com.example.brinted.ui.screens.CampeonesScreen
import com.example.brinted.ui.screens.DashboardScreen
import com.example.brinted.ui.screens.EsportsScreen
import com.example.brinted.ui.screens.HistorialScreen
import com.example.brinted.ui.screens.PartidaDetalleScreen
import com.example.brinted.ui.theme.FondoElevado
import com.example.brinted.ui.theme.FondoNav
import com.example.brinted.ui.theme.GrisTexto
import com.example.brinted.ui.theme.Morado
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Button
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.Alignment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrintedApp() {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }

    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModel.factory(AppContainer.authRepository))
    val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModel.factory(AppContainer.riotProvider))

    val authState by authViewModel.estado.collectAsStateWithLifecycle()
    val datosState by homeViewModel.estado.collectAsStateWithLifecycle()

    val backStack by navController.currentBackStackEntryAsState()
    val rutaActual = backStack?.destination?.route
    val mostrarBottomBar = itemsNavegacion.any { it.ruta.ruta == rutaActual }
    val context = LocalContext.current

    LaunchedEffect(datosState.avisoMock) {
        datosState.avisoMock?.let { mensaje -> snackbarHostState.showSnackbar(mensaje) }
    }
    LaunchedEffect(datosState.error) {
        datosState.error?.let { snackbarHostState.showSnackbar(it) }
    }

    LaunchedEffect(authState.usuario) {
        authState.usuario?.let { usuario ->
            homeViewModel.cargarTodo(usuario.nombreInvocador)
            navController.navigate(Ruta.Dashboard.ruta) {
                popUpTo(navController.graph.findStartDestination().id) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (mostrarBottomBar) {
                BarraNavegacion(rutaActual) { destino ->
                    navController.navigate(destino.ruta) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Ruta.Bienvenida.ruta,
            modifier = Modifier.padding(padding)
        ) {
            composable(Ruta.Bienvenida.ruta) {
                BienvenidaScreen(
                    onLogin = { navController.navigate(Ruta.Login.ruta) },
                    onRegistro = { navController.navigate(Ruta.Registro.ruta) }
                )
            }
            composable(Ruta.Login.ruta) {
                LoginScreen(
                    cargando = authState.cargando,
                    error = authState.error,
                    onBack = { navController.popBackStack() },
                    onLogin = { correo, contrasena ->
                        authViewModel.iniciarSesion(correo, contrasena)
                    },
                    onCrearCuenta = { navController.navigate(Ruta.Registro.ruta) }
                )
            }
            composable(Ruta.Registro.ruta) {
                RegistroScreen(
                    cargando = authState.cargando,
                    error = authState.error,
                    onBack = { navController.popBackStack() },
                    onRegistro = { correo, contrasena, invocador ->
                        authViewModel.registrar(correo, contrasena, invocador)
                    },
                    onYaTengoCuenta = { navController.navigate(Ruta.Login.ruta) }
                )
            }
            composable(Ruta.Dashboard.ruta) {
                DashboardScreen(
                    estado = datosState,
                    onVerPartida = {},
                    onRefresh = {
                        authState.usuario?.let {
                            homeViewModel.cargarTodo(it.nombreInvocador)
                        }
                    },
                    onLogout = {
                        authViewModel.cerrarSesion()
                        navController.navigate(Ruta.Bienvenida.ruta) {
                            popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable(Ruta.Historial.ruta) {
                HistorialScreen(
                    partidas = datosState.historial,
                    cargando = datosState.cargando,
                    onClickPartida = { partida ->
                        homeViewModel.cargarDetalle(partida.id)
                        navController.navigate("detallePartida/${partida.id}")
                    }
                )
            }
            composable(Ruta.Analisis.ruta) {
                AnalisisScreen(
                    analisis = datosState.analisis,
                    cargando = datosState.cargando
                )
            }
            composable(Ruta.Campeones.ruta) {
                CampeonesScreen(
                    campeones = datosState.campeones,
                    cargando = datosState.cargando
                )
            }
            composable(Ruta.Esports.ruta) {
                EsportsScreen(
                    noticias = datosState.noticias,
                    cargando = datosState.cargando,
                    onVerNoticia = { noticia ->
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(noticia.url))
                        context.startActivity(intent)
                    }
                )
            }
            composable(
                route = Ruta.DetallePartida.ruta,
                arguments = listOf(navArgument("partidaId") { type = NavType.StringType })
            ) { backStack ->
                val partidaId = backStack.arguments?.getString("partidaId").orEmpty()
                val detalle = datosState.detallePartida
                val detalleError = datosState.detalleError
                val detalleCargando = datosState.detalleCargando
                var usarFallback by remember { mutableStateOf(false) }

                LaunchedEffect(partidaId) {
                    if (detalle == null && !detalleCargando) {
                        homeViewModel.cargarDetalle(partidaId)
                    }
                }

                when {
                    detalleCargando && detalle == null && !usarFallback -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) { CircularProgressIndicator(color = Morado) }
                    }
                    detalleError != null && detalle == null && !usarFallback -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(detalleError, color = GrisTexto)
                                Button(onClick = { homeViewModel.cargarDetalle(partidaId) }) {
                                    Text("Reintentar")
                                }
                                Button(onClick = { usarFallback = true }) {
                                    Text("Usar datos de prueba")
                                }
                            }
                        }
                    }
                    else -> {
                        val datoSeguro = detalle ?: MockData.detallePorId(partidaId)
                        PartidaDetalleScreen(datoSeguro) { navController.popBackStack() }
                    }
                }
            }
        }
    }
}

@Composable
private fun BarraNavegacion(rutaActual: String?, onNavigate: (Ruta) -> Unit) {
    NavigationBar(containerColor = FondoNav) {
        itemsNavegacion.forEach { item ->
            val seleccionado = rutaActual == item.ruta.ruta
            NavigationBarItem(
                selected = seleccionado,
                onClick = { onNavigate(item.ruta) },
                icon = { androidx.compose.material3.Icon(item.icono, contentDescription = item.etiqueta) },
                label = { Text(item.etiqueta, maxLines = 1) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Morado,
                    selectedTextColor = Morado,
                    unselectedIconColor = GrisTexto,
                    unselectedTextColor = GrisTexto,
                    indicatorColor = FondoElevado
                )
            )
        }
    }
}
