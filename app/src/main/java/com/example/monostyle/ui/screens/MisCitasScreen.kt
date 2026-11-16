package com.example.monostyle.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class CitaItem(
    val fecha: String,
    val servicio: String,
    val moto: String,
    val estado: String
)

@Composable
fun MisCitasScreen(
    onBack: (() -> Unit)? = null,
    onAgendar: () -> Unit = {},
    citas: List<CitaItem> = emptyList()   // perfil nuevo: sin citas
) {
    val azul = Color(0xFF0055FF)
    var tabSeleccionado by remember { mutableIntStateOf(0) }

    val listaFiltrada = if (tabSeleccionado == 0)
        citas.filter { it.estado == "Pendiente" || it.estado == "Confirmada" }
    else
        citas.filter { it.estado == "Completada" || it.estado == "Cancelada" }

    MonoStyleBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
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
                    text = "Mis Citas",
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.9f))
                    .padding(2.dp)
            ) {
                listOf("Próximas", "Historial").forEachIndexed { index, titulo ->
                    val activo = tabSeleccionado == index
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (activo) Brush.horizontalGradient(
                                    listOf(Color(0xFF1E5BFF), Color(0xFF4DA3FF))
                                ) else Brush.horizontalGradient(
                                    listOf(Color.Transparent, Color.Transparent)
                                )
                            )
                            .clickable { tabSeleccionado = index },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(titulo, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (listaFiltrada.isEmpty()) {
                EstadoVacioCitas(
                    esProximas = tabSeleccionado == 0,
                    azul = azul,
                    onAgendar = onAgendar
                )
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    items(listaFiltrada) { cita -> CitaCard(cita, azul) }
                }
            }
        }
    }
}

@Composable
private fun EstadoVacioCitas(esProximas: Boolean, azul: Color, onAgendar: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Filled.CalendarMonth,
            contentDescription = null,
            tint = azul,
            modifier = Modifier.size(64.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = if (esProximas) "Aún no tienes citas agendadas" else "Todavía no tienes historial de citas",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = if (esProximas) "Cuando agendes un servicio, aparecerá aquí."
            else "Tus citas completadas o canceladas aparecerán aquí.",
            color = Color.LightGray,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
        if (esProximas) {
            Spacer(Modifier.height(24.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(26.dp))
                    .background(Brush.horizontalGradient(listOf(Color(0xFF1E5BFF), Color(0xFF4DA3FF))))
                    .clickable { onAgendar() }
                    .padding(horizontal = 28.dp, vertical = 12.dp)
            ) {
                Text("Agendar cita", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun CitaCard(cita: CitaItem, azul: Color) {
    val colorEstado = when (cita.estado) {
        "Completada" -> Color(0xFF1DB954)
        "Pendiente" -> Color(0xFFE0A93B)
        "Cancelada" -> Color(0xFFE5484D)
        else -> Color(0xFF4DA3FF)
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = Color.Black.copy(alpha = 0.9f),
        border = BorderStroke(1.5.dp, azul)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.CalendarMonth, null, tint = Color.White, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(cita.fecha, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Build, null, tint = Color.White, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(cita.servicio, color = Color.White, fontSize = 15.sp)
            }
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.TwoWheeler, null, tint = Color.White, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(cita.moto, color = Color.White, fontSize = 15.sp)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(colorEstado)
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(cita.estado, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}