package com.example.monostyle.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.monostyle.data.model.SolicitudServicioDto
import com.example.monostyle.data.network.ClienteApi
import com.example.monostyle.data.network.RetrofitClient
import kotlinx.coroutines.launch
import java.util.Calendar
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

@Composable
fun AgendarCitaScreen(
    onCitaAgendadaExito: () -> Unit,
    onBack: (() -> Unit)? = null,
    onRegistrarVehiculo: () -> Unit = {},
    vehiculos: List<String> = emptyList()
) {
    val servicios = listOf(
        "Cambio de aceite", "Revisión general", "Cambio de frenos",
        "Cambio de llantas", "Sincronización", "Otro"
    )
    val meses = listOf("Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic")

    var vehiculo by remember { mutableStateOf("") }
    var servicio by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }
    var hora by remember { mutableStateOf("") }
    var observaciones by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val azul = Color(0xFF0055FF)
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    MonoStyleBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                if (onBack != null) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = Color.White)
                    }
                }
                Text(
                    text = "Agendar Cita",
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            var expandidoVehiculo by remember { mutableStateOf(false) }
            Box {
                CampoSelector(
                    icono = Icons.Filled.TwoWheeler,
                    placeholder = "Vehículos",
                    valor = vehiculo,
                    azul = azul,
                    onClick = { expandidoVehiculo = true },
                    trailing = { Icon(Icons.Filled.KeyboardArrowDown, null, tint = Color.White) }
                )
                DropdownMenu(
                    expanded = expandidoVehiculo,
                    onDismissRequest = { expandidoVehiculo = false }
                ) {
                    if (vehiculos.isEmpty()) {
                        DropdownMenuItem(
                            text = { Text("No hay vehículos registrados", color = Color.Gray) },
                            enabled = false,
                            onClick = {}
                        )
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "Registrar vehículo",
                                    color = Color(0xFF2F80FF),
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            onClick = {
                                expandidoVehiculo = false
                                onRegistrarVehiculo()
                            }
                        )
                    } else {
                        vehiculos.forEach { opcion ->
                            DropdownMenuItem(
                                text = { Text(opcion) },
                                onClick = {
                                    vehiculo = opcion
                                    expandidoVehiculo = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            var expandidoServicio by remember { mutableStateOf(false) }
            Box {
                CampoSelector(
                    icono = Icons.Filled.Build,
                    placeholder = "Tipo de servicio",
                    valor = servicio,
                    azul = azul,
                    onClick = { expandidoServicio = true },
                    trailing = { Icon(Icons.Filled.KeyboardArrowDown, null, tint = Color.White) }
                )
                DropdownMenu(
                    expanded = expandidoServicio,
                    onDismissRequest = { expandidoServicio = false }
                ) {
                    servicios.forEach { opcion ->
                        DropdownMenuItem(
                            text = { Text(opcion) },
                            onClick = {
                                servicio = opcion
                                expandidoServicio = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            CampoSelector(
                icono = Icons.Filled.CalendarMonth,
                placeholder = "Fecha",
                valor = fecha,
                azul = azul,
                onClick = {
                    val c = Calendar.getInstance()
                    val dialog = DatePickerDialog(
                        context,
                        { _, y, m, d -> fecha = "%02d %s %d".format(d, meses[m], y) },
                        c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)
                    )
                    dialog.datePicker.minDate = System.currentTimeMillis() - 1000
                    dialog.show()
                }
            )

            Spacer(modifier = Modifier.height(14.dp))


            CampoSelector(
                icono = Icons.Filled.AccessTime,
                placeholder = "Hora",
                valor = hora,
                azul = azul,
                onClick = {
                    val c = Calendar.getInstance()
                    TimePickerDialog(
                        context,
                        { _, h, min -> hora = "%02d:%02d".format(h, min) },
                        c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true
                    ).show()
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = observaciones,
                onValueChange = { observaciones = it },
                placeholder = { Text("Observaciones (opcional)", color = Color.LightGray) },
                leadingIcon = { Icon(Icons.Filled.Edit, null, tint = Color.White) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF2F80FF),
                    unfocusedBorderColor = azul,
                    focusedContainerColor = Color.Black.copy(alpha = 0.85f),
                    unfocusedContainerColor = Color.Black.copy(alpha = 0.85f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color(0xFF2F80FF)
                ),
                modifier = Modifier.fillMaxWidth().height(120.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(
                        Brush.horizontalGradient(listOf(Color(0xFF1E5BFF), Color(0xFF4DA3FF)))
                    )
                    .clickable(enabled = !isLoading) {
                        if (vehiculo.isBlank()) {
                            Toast.makeText(
                                context,
                                if (vehiculos.isEmpty()) "Primero registra un vehículo"
                                else "Selecciona un vehículo",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@clickable
                        }
                        if (servicio.isBlank() || fecha.isBlank() || hora.isBlank()) {
                            Toast.makeText(
                                context,
                                "Selecciona servicio, fecha y hora",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@clickable
                        }

                        isLoading = true
                        coroutineScope.launch {
                            try {
                                val json = JSONObject().apply {
                                    put("vehiculo", vehiculo)
                                    put("servicio", servicio)
                                    put("fecha", fecha)
                                    put("hora", hora)
                                    put("observaciones", observaciones.trim())
                                }
                                val body = json.toString().toRequestBody("application/json".toMediaType())
                                val api = RetrofitClient.getClient(context).create(ClienteApi::class.java)
                                val response = api.agendarCita(body)
                                isLoading = false

                                if (response.isSuccessful) {
                                    Toast.makeText(context, "¡Cita agendada con éxito!", Toast.LENGTH_LONG).show()
                                    onCitaAgendadaExito()
                                } else {
                                    Toast.makeText(context, "Error al agendar (${response.code()})", Toast.LENGTH_LONG).show()
                                }
                            } catch (e: Exception) {
                                isLoading = false
                                Toast.makeText(context, "Error de red: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                } else {
                    Text("Agendar Cita", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun CampoSelector(
    icono: ImageVector,
    placeholder: String,
    valor: String,
    azul: Color,
    onClick: () -> Unit,
    trailing: @Composable (() -> Unit)? = null
) {
    Surface(
        modifier = Modifier.fillMaxWidth().height(56.dp).clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = Color.Black.copy(alpha = 0.85f),
        border = BorderStroke(1.5.dp, azul)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icono, null, tint = Color.White, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(12.dp))
            Text(
                text = valor.ifBlank { placeholder },
                color = if (valor.isBlank()) Color.LightGray else Color.White,
                fontSize = 15.sp,
                modifier = Modifier.weight(1f)
            )
            trailing?.invoke()
        }
    }
}