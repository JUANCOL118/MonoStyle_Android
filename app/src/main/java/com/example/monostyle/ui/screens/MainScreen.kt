package com.example.monostyle.ui.screens

import AgregarVehiculoScreen
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import com.example.monostyle.data.network.ClienteApi
import com.example.monostyle.data.network.RetrofitClient
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

sealed class BottomNavItem(val ruta: String, val titulo: String, val icono: ImageVector) {
    object Inicio : BottomNavItem("inicio", "Inicio", Icons.Default.Home)
    object Citas : BottomNavItem("citas", "Citas", Icons.Default.CalendarMonth)
    object Servicios : BottomNavItem("servicios", "Servicios", Icons.Default.Build)
    object Perfil : BottomNavItem("perfil", "Perfil", Icons.Default.Person)
}

@Composable
fun MainScreen(
    onLogout: () -> Unit
) {
    var tabSeleccionada by remember { mutableIntStateOf(0) }
    var mostrarVehiculos by remember { mutableStateOf(false) }
    var mostrarAgendar by remember { mutableStateOf(false) }
    var mostrarAgregarVehiculo by remember { mutableStateOf(false) }
    var guardandoMoto by remember { mutableStateOf(false) }
    val vehiculos = remember { mutableStateListOf<Vehiculo>() }
    val citas = remember { mutableStateListOf<CitaItem>() }

    val items = listOf(
        BottomNavItem.Inicio,
        BottomNavItem.Citas,
        BottomNavItem.Servicios,
        BottomNavItem.Perfil
    )
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val azulElectrico = Color(0xFF0055FF)

    val prefs = context.getSharedPreferences("MonoStylePrefs", Context.MODE_PRIVATE)
    val nombreCompleto = prefs.getString("NOMBRE_USUARIO", "") ?: ""
    val primerNombre = nombreCompleto.substringBefore(" ").ifBlank { "Cliente" }

    // Carga las motos guardadas en la base de datos
    LaunchedEffect(Unit) {
        try {
            val api = RetrofitClient.getClient(context).create(ClienteApi::class.java)
            val response = api.misMotos()
            if (response.isSuccessful) {
                vehiculos.clear()
                response.body().orEmpty().forEach {
                    vehiculos.add(Vehiculo(it.marca, it.modelo, it.placa, it.anio.toString(), it.color))
                }
            }
        } catch (e: Exception) {
            // sin conexión: se queda la lista vacía
        }
    }

    // Carga las citas al abrir la pestaña Citas y al volver de agendar
    LaunchedEffect(tabSeleccionada, mostrarAgendar) {
        if (tabSeleccionada == 1 && !mostrarAgendar) {
            try {
                val api = RetrofitClient.getClient(context).create(ClienteApi::class.java)
                val response = api.misCitas()
                if (response.isSuccessful) {
                    citas.clear()
                    response.body().orEmpty().forEach {
                        citas.add(
                            CitaItem("${it.fecha} - ${it.hora}", it.servicio, it.vehiculo, it.estado)
                        )
                    }
                }
            } catch (e: Exception) {
                // sin conexión: se queda la lista actual
            }
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.Black.copy(alpha = 0.95f),
                contentColor = Color.White
            ) {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(item.icono, contentDescription = item.titulo) },
                        label = { Text(item.titulo) },
                        selected = tabSeleccionada == index && !mostrarVehiculos,
                        onClick = {
                            mostrarVehiculos = false
                            mostrarAgendar = false
                            mostrarAgregarVehiculo = false
                            tabSeleccionada = index
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = azulElectrico,
                            selectedTextColor = azulElectrico,
                            unselectedIconColor = Color.LightGray.copy(alpha = 0.6f),
                            unselectedTextColor = Color.LightGray.copy(alpha = 0.6f),
                            indicatorColor = azulElectrico.copy(alpha = 0.2f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            if (mostrarAgregarVehiculo) {
                AgregarVehiculoScreen(
                    onBack = { mostrarAgregarVehiculo = false },
                    onGuardar = guardar@{ nuevo ->
                        if (guardandoMoto) return@guardar
                        guardandoMoto = true
                        scope.launch {
                            try {
                                val json = JSONObject().apply {
                                    put("marca", nuevo.marca)
                                    put("modelo", nuevo.modelo)
                                    put("placa", nuevo.placa)
                                    put("anio", nuevo.anio.toInt())
                                    put("color", nuevo.color)
                                }
                                val body = json.toString()
                                    .toRequestBody("application/json".toMediaType())
                                val api = RetrofitClient.getClient(context)
                                    .create(ClienteApi::class.java)
                                val response = api.agregarMoto(body)
                                guardandoMoto = false

                                if (response.isSuccessful) {
                                    vehiculos.add(nuevo)
                                    mostrarAgregarVehiculo = false
                                    Toast.makeText(context, "Vehículo guardado", Toast.LENGTH_SHORT).show()
                                } else {
                                    val msg = if (response.code() == 409) "Esa placa ya está registrada"
                                    else "No se pudo guardar (${response.code()})"
                                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                                }
                            } catch (e: Exception) {
                                guardandoMoto = false
                                Toast.makeText(
                                    context,
                                    "Error de red: ${e.localizedMessage}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                )
            } else if (mostrarVehiculos) {
                VehiculosScreen(
                    vehiculos = vehiculos,
                    onNavigateToRegistrarMoto = { mostrarAgregarVehiculo = true },
                    onBack = { mostrarVehiculos = false }
                )
            } else if (mostrarAgendar) {
                AgendarCitaScreen(
                    onCitaAgendadaExito = {
                        mostrarAgendar = false
                        tabSeleccionada = 1
                    },
                    onBack = { mostrarAgendar = false },
                    onRegistrarVehiculo = {
                        mostrarAgendar = false
                        mostrarAgregarVehiculo = true
                    },
                    vehiculos = vehiculos.map { "${it.marca} ${it.modelo} - ${it.placa}" }
                )
            } else {
                when (tabSeleccionada) {
                    0 -> HomeScreen(
                        nombreUsuario = primerNombre,
                        onNavigateToAgendar = { mostrarAgendar = true },
                        onNavigateToServicios = { tabSeleccionada = 2 },
                        onNavigateToVehiculos = { mostrarVehiculos = true },
                        onContactarTaller = {
                            Toast.makeText(
                                context,
                                "Teléfono Taller: +57 300 000 0000",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    )
                    1 -> MisCitasScreen(
                        citas = citas,
                        onAgendar = { mostrarAgendar = true }
                    )
                    2 -> ServiciosScreen()
                    3 -> PerfilScreen(onLogout = { onLogout() })
                }
            }
        }
    }
}

@Composable
fun PantallaEnConstruccion(nombre: String) {
    MonoStyleBackground {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = nombre, color = Color.White, style = MaterialTheme.typography.headlineMedium)
        }
    }
}