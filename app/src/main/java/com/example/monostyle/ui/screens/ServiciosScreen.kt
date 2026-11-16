package com.example.monostyle.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Description
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.monostyle.data.network.ClienteApi
import com.example.monostyle.data.network.RetrofitClient
import kotlinx.coroutines.launch

data class ServicioItem(
    val nombre: String,
    val fecha: String,
    val vehiculo: String,
    val descripcion: String,
    val costo: String,
    val estado: String   // "En curso" | "Completada"
)

@Composable
fun ServiciosScreen(
    servicios: List<ServicioItem> = emptyList()   // perfil nuevo: sin servicios
) {
    var seleccionado by remember { mutableStateOf<ServicioItem?>(null) }

    if (seleccionado != null) {
        DetalleServicioScreen(
            servicio = seleccionado!!,
            onBack = { seleccionado = null }
        )
    } else {
        ListaServiciosScreen(
            servicios = servicios,
            onSeleccionar = { seleccionado = it }
        )
    }
}

@Composable
private fun ListaServiciosScreen(
    servicios: List<ServicioItem>,
    onSeleccionar: (ServicioItem) -> Unit
) {
    val azul = Color(0xFF0055FF)
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var numeroOrden by remember { mutableStateOf("") }
    var cargando by remember { mutableStateOf(false) }

    fun consultar(esFactura: Boolean) {
        val id = numeroOrden.trim().toLongOrNull()
        if (id == null) {
            Toast.makeText(context, "Escribe un número de orden válido", Toast.LENGTH_SHORT).show()
            return
        }
        cargando = true
        coroutineScope.launch {
            try {
                val api = RetrofitClient.getClient(context).create(ClienteApi::class.java)
                val response = if (esFactura) api.verDetalleFactura(id) else api.consultarEstadoOrden(id)
                cargando = false
                if (response.isSuccessful) {
                    Toast.makeText(context, response.body() ?: "Sin información", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "No se encontró la orden", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                cargando = false
                Toast.makeText(context, "Error de red: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    MonoStyleBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Text(
                "Mis Servicios",
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 20.dp)
            )

            if (servicios.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Filled.Build, null, tint = azul, modifier = Modifier.size(64.dp))
                    Spacer(Modifier.height(14.dp))
                    Text(
                        "Aún no tienes servicios",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Cuando el taller atienda tu moto, tus servicios aparecerán aquí.",
                        color = Color.LightGray,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(servicios) { s -> ServicioCard(s, azul) { onSeleccionar(s) } }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Consulta por número de orden (conserva la lógica original)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = Color.Black.copy(alpha = 0.9f),
                border = BorderStroke(1.5.dp, azul)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Consultar orden", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = numeroOrden,
                        onValueChange = { numeroOrden = it.filter { c -> c.isDigit() } },
                        placeholder = { Text("Número de orden (Ej: 1)", color = Color.LightGray) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF2F80FF),
                            unfocusedBorderColor = azul,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color(0xFF2F80FF)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        BotonConsulta("Estado", true, !cargando, Modifier.weight(1f)) { consultar(false) }
                        BotonConsulta("Factura", false, !cargando, Modifier.weight(1f)) { consultar(true) }
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun BotonConsulta(
    texto: String,
    relleno: Boolean,
    habilitado: Boolean,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(42.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(
                if (relleno) Brush.horizontalGradient(listOf(Color(0xFF1E5BFF), Color(0xFF4DA3FF)))
                else Brush.horizontalGradient(listOf(Color.Transparent, Color.Transparent))
            )
            .then(if (!relleno) Modifier.clip(RoundedCornerShape(22.dp)) else Modifier)
            .clickable(enabled = habilitado) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(texto, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}

@Composable
private fun ServicioCard(s: ServicioItem, azul: Color, onClick: () -> Unit) {
    val enCurso = s.estado == "En curso"
    Surface(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        color = Color.Black.copy(alpha = 0.9f),
        border = BorderStroke(1.5.dp, azul)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Build, null, tint = Color(0xFF2F80FF), modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(s.nombre, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.CalendarMonth, null, tint = Color.White, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(s.fecha, color = Color.White, fontSize = 14.sp, modifier = Modifier.weight(1f))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (enCurso) Color(0xFF1E5BFF) else Color(0xFF1DB954))
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                    ) {
                        Text(s.estado, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = Color.LightGray)
        }
    }
}

@Composable
private fun DetalleServicioScreen(servicio: ServicioItem, onBack: () -> Unit) {
    val azul = Color(0xFF0055FF)
    val enCurso = servicio.estado == "En curso"

    MonoStyleBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = Color.White)
                }
                Text(
                    "Detalle del Servicio",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Spacer(Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.Black.copy(alpha = 0.85f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Build, null, tint = Color(0xFF2F80FF), modifier = Modifier.size(70.dp))
            }

            Spacer(Modifier.height(10.dp))
            Text(servicio.nombre, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (enCurso) Color(0xFF1E5BFF) else Color(0xFF1DB954))
                    .padding(horizontal = 12.dp, vertical = 3.dp)
            ) {
                Text(servicio.estado, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(20.dp))

            FilaDetalle(Icons.Filled.CalendarMonth, "Fecha:", servicio.fecha, azul)
            Spacer(Modifier.height(10.dp))
            FilaDetalle(Icons.Filled.TwoWheeler, "Vehículo:", servicio.vehiculo, azul)
            Spacer(Modifier.height(10.dp))
            FilaDetalle(Icons.Filled.Description, "Descripción:", servicio.descripcion, azul)
            Spacer(Modifier.height(10.dp))
            FilaDetalle(Icons.Filled.AttachMoney, "Costo:", servicio.costo, azul)
        }
    }
}

@Composable
private fun FilaDetalle(icono: ImageVector, etiqueta: String, valor: String, azul: Color) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color.Black.copy(alpha = 0.9f),
        border = BorderStroke(1.5.dp, azul)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icono, null, tint = Color.White, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(12.dp))
            Text(etiqueta, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(Modifier.width(6.dp))
            Text(valor, color = Color.White, fontSize = 14.sp)
        }
    }
}