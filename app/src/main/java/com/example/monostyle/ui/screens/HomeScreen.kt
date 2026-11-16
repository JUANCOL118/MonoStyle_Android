package com.example.monostyle.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.monostyle.R

@Composable
fun HomeScreen(
    nombreUsuario: String = "Cliente",
    onNavigateToAgendar: () -> Unit,
    onNavigateToServicios: () -> Unit,
    onNavigateToVehiculos: () -> Unit,
    onContactarTaller: () -> Unit
) {
    val azulElectrico = Color(0xFF0055FF)

    MonoStyleBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_monostyle),
                    contentDescription = "Logo MonoStyle",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .height(150.dp)
                        .fillMaxWidth()
                )

                val brushMono = Brush.verticalGradient(
                    listOf(Color(0xFF5BB0FF), Color(0xFF0055FF))
                )
                val brushStyle = Brush.verticalGradient(
                    listOf(Color.White, Color(0xFF9AA3AD))
                )

                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(brush = brushMono)) { append("MONO") }
                        withStyle(SpanStyle(brush = brushStyle)) { append("STYLE") }
                    },
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(
                        width = 1.5.dp,
                        color = azulElectrico.copy(alpha = 0.8f),
                        shape = RoundedCornerShape(16.dp)
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.92f))
                        .padding(horizontal = 18.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "¡Hola, $nombreUsuario!",
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(95.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.fondo_tarjeta_ini),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.matchParentSize()
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(Color.Black.copy(alpha = 0.45f))
                    )
                    Text(
                        text = "Gestiona tus citas, servicios y vehículos desde la app",
                        color = Color.White,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 24.sp,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(horizontal = 18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    DashboardCard(
                        titulo = "Agendar\nCita",
                        icono = Icons.Default.CalendarMonth,
                        colorAcento = azulElectrico,
                        onClick = onNavigateToAgendar,
                        modifier = Modifier.weight(1f)
                    )
                    DashboardCard(
                        titulo = "Mis\nServicios",
                        icono = Icons.Default.Build,
                        colorAcento = azulElectrico,
                        onClick = onNavigateToServicios,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    DashboardCard(
                        titulo = "Mis\nVehículos",
                        icono = Icons.Default.TwoWheeler,
                        colorAcento = azulElectrico,
                        onClick = onNavigateToVehiculos,
                        modifier = Modifier.weight(1f)
                    )
                    DashboardCard(
                        titulo = "Contacto\nTaller",
                        icono = Icons.Default.HeadsetMic,
                        colorAcento = azulElectrico,
                        onClick = onContactarTaller,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun DashboardCard(
    titulo: String,
    icono: ImageVector,
    colorAcento: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(135.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = Color.Black.copy(alpha = 0.9f),
        border = BorderStroke(1.5.dp, colorAcento)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icono,
                contentDescription = titulo,
                tint = colorAcento,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = titulo,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )
        }
    }
}