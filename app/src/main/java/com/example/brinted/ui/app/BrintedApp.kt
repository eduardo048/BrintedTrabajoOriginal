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
import androidx.compose.runtime.remember
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
// Función principal de la aplicación Brinted que configura la navegación y la interfaz de usuario
fun BrintedApp() {
    val navController = rememberNavController() // Controlador de navegación
    val snackbarHostState = remember { SnackbarHostState() } // Estado del host de Snackbar

    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModel.factory(AppContainer.authRepository)) // ViewModel de autenticación
    val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModel.factory(AppContainer.riotRepository)) // ViewModel de la pantalla principal

    val authState by authViewModel.estado.collectAsStateWithLifecycle() // Estado de autenticación
    val datosState by homeViewModel.estado.collectAsStateWithLifecycle() // Estado de datos de la pantalla principal

    val backStack by navController.currentBackStackEntryAsState() // Entrada actual en la pila de navegación
    val rutaActual = backStack?.destination?.route // Ruta actual
    val mostrarBottomBar = itemsNavegacion.any { it.ruta.ruta == rutaActual } // Determina si se debe mostrar la barra de navegación inferior
    val context = LocalContext.current // Contexto de la aplicación

    // Efectos secundarios para mostrar mensajes de Snackbar basados en el estado
    LaunchedEffect(datosState.avisoMock) {
        datosState.avisoMock?.let { mensaje -> snackbarHostState.showSnackbar(mensaje) } // Muestra un Snackbar con el mensaje de aviso
    }
    // Efecto para mostrar errores de autenticación
    LaunchedEffect(datosState.error) {
        datosState.error?.let { snackbarHostState.showSnackbar(it) } // Muestra un Snackbar con el mensaje de error
    }

    // Efecto para cargar datos y navegar al dashboard después de la autenticación
    LaunchedEffect(authState.usuario) { // Se activa cuando cambia el usuario autenticado
        authState.usuario?.let { usuario -> // Si el usuario está autenticado
            homeViewModel.cargarTodo(usuario.nombreInvocador, usuario.region) // Carga todos los datos necesarios
            navController.navigate(Ruta.Dashboard.ruta) { // Navega al dashboard
                popUpTo(navController.graph.findStartDestination().id) {  // Limpia la pila de navegación
                    inclusive = true // Elimina todas las entradas anteriores
                }
                launchSingleTop = true // Evita múltiples instancias
            }
        }
    }

    // Configuración de la estructura principal de la interfaz de usuario con Scaffold
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }, // Host de Snackbar
        bottomBar = { // Barra de navegación inferior
            if (mostrarBottomBar) { // Mostrar solo si la ruta actual está en los elementos de navegación
                BarraNavegacion(rutaActual) { destino -> // Función de navegación
                    navController.navigate(destino.ruta) { // Navegar al destino seleccionado
                        popUpTo(navController.graph.findStartDestination().id) {  //Evitar múltiples instancias
                            saveState = true // Guardar el estado de la pantalla
                        }
                        launchSingleTop = true // Evitar múltiples instancias
                        restoreState = true // Restaurar el estado guardado
                    }
                }
            }
        }
    ) { padding ->
        NavHost( // Host de navegación
            navController = navController, // Controlador de navegación
            startDestination = Ruta.Bienvenida.ruta, // Destino inicial
            modifier = Modifier.fillMaxSize() // Modificador para llenar el tamaño disponible
        ) {
            composable(Ruta.Bienvenida.ruta) { // Pantalla de bienvenida
                BienvenidaScreen( // Composable de la pantalla de bienvenida
                    onLogin = { navController.navigate(Ruta.Login.ruta) }, // Navegar a la pantalla de inicio de sesión
                    onRegistro = { navController.navigate(Ruta.Registro.ruta) } // Navegar a la pantalla de registro
                )
            }
            composable(Ruta.Login.ruta) { // Pantalla de inicio de sesión
                Box(modifier = Modifier.padding(padding)) { // Contenedor con padding
                    LoginScreen( // Composable de la pantalla de inicio de sesión
                        cargando = authState.cargando, // Estado de carga
                        error = authState.error, // Estado de error
                        onBack = { navController.popBackStack() }, // Acción para volver atrás
                        onLogin = { correo, contrasena -> // Acción para iniciar sesión
                            authViewModel.iniciarSesion(correo, contrasena) // Llama al ViewModel para iniciar sesión
                        },
                        onCrearCuenta = { navController.navigate(Ruta.Registro.ruta) } // Navegar a la pantalla de registro
                    )
                }
            }

            // Pantalla de registro
            composable(Ruta.Registro.ruta) { // Composable de la pantalla de registro
                Box(modifier = Modifier.padding(padding)) { // Contenedor con padding
                    RegistroScreen( // Composable de la pantalla de registro
                        cargando = authState.cargando, // Estado de carga
                        error = authState.error, // Estado de error
                        onBack = { navController.popBackStack() }, // Acción para volver atrás
                        onRegistro = { correo, contrasena, invocador, region -> // Acción para registrar una cuenta
                            authViewModel.registrar(correo, contrasena, invocador, region) // Llama al ViewModel para registrar una nueva cuenta
                        },
                        onYaTengoCuenta = { navController.navigate(Ruta.Login.ruta) } // Navegar a la pantalla de inicio de sesión
                    )
                }
            }

            // Pantalla del dashboard
            composable(Ruta.Dashboard.ruta) { // Composable de la pantalla del dashboard
                Box(modifier = Modifier.padding(padding)) { // Contenedor con padding
                    DashboardScreen( // Composable de la pantalla del dashboard
                        estado = datosState, // Estado de datos
                        onVerPartida = {}, // Acción para ver una partida (vacía por ahora)
                        onRefresh = { // Acción para refrescar los datos
                            authState.usuario?.let { // Si el usuario está autenticado
                                homeViewModel.cargarTodo(it.nombreInvocador, it.region) // Carga todos los datos necesarios
                            }
                        },
                        onLogout = { // Acción para cerrar sesión
                            authViewModel.cerrarSesion() // Llama al ViewModel para cerrar sesión
                            navController.navigate(Ruta.Bienvenida.ruta) { // Navega a la pantalla de bienvenida
                                popUpTo(navController.graph.findStartDestination().id) { inclusive = true } // Limpia la pila de navegación
                                launchSingleTop = true // Evita múltiples instancias
                            }
                        }
                    )
                }
            }

            // Pantalla del historial de partidas
            composable(Ruta.Historial.ruta) { // Composable de la pantalla del historial de partidas
                Box(modifier = Modifier.padding(padding)) { // Contenedor con padding
                    HistorialScreen( // Composable de la pantalla del historial de partidas
                        partidas = datosState.historial, // Lista de partidas del historial
                        cargando = datosState.cargando, // Estado de carga
                        onClickPartida = { partida -> // Acción al hacer clic en una partida
                            val region = authState.usuario?.region ?: "euw1" // Obtiene la región del usuario o usa "euw1" por defecto
                            val invocador = authState.usuario?.nombreInvocador ?: "" // Obtiene el nombre del invocador del usuario o usa una cadena vacía por defecto
                            homeViewModel.cargarDetalle(partida.id, region, invocador) // Carga el detalle de la partida seleccionada
                            navController.navigate("detallePartida/${partida.id}") // Navega a la pantalla de detalle de la partida
                        }
                    )
                }
            }

            // Pantalla de análisis
            composable(Ruta.Analisis.ruta) { // Composable de la pantalla de análisis
                Box(modifier = Modifier.padding(padding)) { // Contenedor con padding
                    AnalisisScreen( // Composable de la pantalla de análisis
                        analisis = datosState.analisis, // Datos de análisis
                        cargando = datosState.cargando // Estado de carga
                    )
                }
            }

            // Pantalla de campeones
            composable(Ruta.Campeones.ruta) { // Composable de la pantalla de campeones
                Box(modifier = Modifier.padding(padding)) { // Contenedor con padding
                    CampeonesScreen( // Composable de la pantalla de campeones
                        campeones = datosState.campeones, // Lista de campeones
                        cargando = datosState.cargando // Estado de carga
                    )
                }
            }

            // Pantalla de esports
            composable(Ruta.Esports.ruta) { // Composable de la pantalla de esports
                Box(modifier = Modifier.padding(padding)) { // Contenedor con padding
                    EsportsScreen( // Composable de la pantalla de esports
                        noticias = datosState.noticias, // Lista de noticias de esports
                        cargando = datosState.cargando, // Estado de carga
                        onVerNoticia = { noticia -> // Acción al ver una noticia
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(noticia.url)) // Crea un intent para abrir la URL de la noticia
                            context.startActivity(intent) // Inicia la actividad para ver la noticia en el navegador
                        }
                    )
                }
            }

            // Pantalla de detalle de partida
            composable(
                route = Ruta.DetallePartida.ruta, // Ruta de la pantalla de detalle de partida
                arguments = listOf(navArgument("partidaId") { type = NavType.StringType }) // Argumento de la ruta: ID de la partida
            ) { backStack -> // Composable de la pantalla de detalle de partida
                val partidaId = backStack.arguments?.getString("partidaId").orEmpty() // Obtiene el ID de la partida de los argumentos
                val detalle = datosState.detallePartida // Detalle de la partida desde el estado de datos
                val detalleError = datosState.detalleError // Error al cargar el detalle de la partida desde el estado de datos
                val detalleCargando = datosState.detalleCargando // Estado de carga del detalle de la partida desde el estado de datos
                LaunchedEffect(partidaId) { // Efecto secundario para cargar el detalle de la partida cuando cambia el ID
                    if (detalle == null && !detalleCargando) { // Si no hay detalle cargado y no está cargando
                        val region = authState.usuario?.region ?: "euw1" // Obtiene la región del usuario o usa "euw1" por defecto
                        val invocador = authState.usuario?.nombreInvocador ?: "" // Obtiene el nombre del invocador del usuario o usa una cadena vacía por defecto
                        homeViewModel.cargarDetalle(partidaId, region, invocador) // Carga el detalle de la partida
                    }
                }

                // Contenido de la pantalla de detalle de partida
                Box(modifier = Modifier.padding(padding)) { // Contenedor con padding
                    when { // Manejo de diferentes estados de carga y error
                        detalleCargando && detalle == null -> { // Si está cargando y no hay detalle
                            Box( // Contenedor centrado
                                modifier = Modifier.fillMaxSize(), // Llena el tamaño disponible
                                contentAlignment = Alignment.Center // Centra el contenido
                            ) { CircularProgressIndicator(color = Morado) } // Indicador de carga circular
                        }
                        detalleError != null && detalle == null -> { // Si hay un error y no hay detalle
                            Box(
                                modifier = Modifier.fillMaxSize(), // Llena el tamaño disponible
                                contentAlignment = Alignment.Center // Centra el contenido
                            ) {
                                Column( // Columna para mostrar el error y el botón de reintento
                                    horizontalAlignment = Alignment.CenterHorizontally, // Alineación horizontal centrada
                                    verticalArrangement = Arrangement.spacedBy(12.dp) // Espaciado vertical entre elementos
                                ) {
                                    Text(detalleError, color = GrisTexto) // Muestra el mensaje de error
                                    Button(onClick = { // Acción al hacer clic en el botón de reintento
                                        val region = authState.usuario?.region ?: "euw1" // Obtiene la región del usuario o usa "euw1" por defecto
                                        val invocador = authState.usuario?.nombreInvocador ?: "" // Obtiene el nombre del invocador del usuario o usa una cadena vacía por defecto
                                        homeViewModel.cargarDetalle(partidaId, region, invocador) // Vuelve a cargar el detalle de la partida
                                    }) {
                                        Text("Reintentar") // Texto del botón de reintento
                                    }
                                }
                            }
                        }
                        else -> { // Estado exitoso, muestra el detalle de la partida
                            if (detalle != null) { // Si el detalle está disponible
                                PartidaDetalleScreen(detalle) { navController.popBackStack() } // Muestra el detalle de la partida con una acción para volver atrás
                            } else if (detalleError == null) { // Si no hay detalle ni error
                                Box( // Contenedor centrado
                                    modifier = Modifier.fillMaxSize(), // Llena el tamaño disponible
                                    contentAlignment = Alignment.Center // Centra el contenido
                                ) { Text("No hay datos disponibles.", color = GrisTexto) } // Muestra un mensaje indicando que no hay datos disponibles
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
// Composable para la barra de navegación inferior
private fun BarraNavegacion(rutaActual: String?, onNavigate: (Ruta) -> Unit) { // Función de navegación al seleccionar un elemento
    NavigationBar(containerColor = FondoNav) { // Barra de navegación con color de fondo personalizado
        itemsNavegacion.forEach { item -> // Itera sobre los elementos de navegación
            val seleccionado = rutaActual == item.ruta.ruta // Verifica si el elemento está seleccionado
            NavigationBarItem( // Elemento de la barra de navegación
                selected = seleccionado, // Estado seleccionado
                onClick = { onNavigate(item.ruta) }, // Acción al hacer clic en el elemento
                icon = { androidx.compose.material3.Icon(item.icono, contentDescription = item.etiqueta) }, // Icono del elemento
                label = { Text(item.etiqueta, maxLines = 1) }, // Etiqueta del elemento
                colors = NavigationBarItemDefaults.colors( // Colores personalizados para el elemento
                    selectedIconColor = Morado, // Color del icono seleccionado
                    selectedTextColor = Morado, // Color del texto seleccionado
                    unselectedIconColor = GrisTexto, // Color del icono no seleccionado
                    unselectedTextColor = GrisTexto, // Color del texto no seleccionado
                    indicatorColor = FondoElevado // Color del indicador de selección
                )
            )
        }
    }
}
