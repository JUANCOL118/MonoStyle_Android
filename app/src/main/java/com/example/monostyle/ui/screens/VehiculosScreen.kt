package com.example.monostyle.ui.screens

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter

data class Vehiculo(
    val marca: String,
    val modelo: String,
    val placa: String,
    val anio: String,
    val color: String,
    val fotoUri: Uri? = null,
    val fotoBitmap: Bitmap? = null
)

@Composable
fun VehiculosScreen(
    vehiculos: List<Vehiculo> = emptyList(),
    onNavigateToRegistrarMoto: () -> Unit,
    onBack: (() -> Unit)? = null
) {
    val azul = Color(0xFF0055FF)

    MonoStyleBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                if (onBack != null) {
                    IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = Color.White)
                    }
                }
                Text(
                    "Mis Vehículos",
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            Text(
                "Gestiona tus motocicletas registradas",
                color = Color.LightGray,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 20.dp)
            )

            if (vehiculos.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Filled.TwoWheeler, null, tint = azul, modifier = Modifier.size(64.dp))
                    Spacer(Modifier.height(14.dp))
                    Text(
                        "Aún no tienes vehículos registrados",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Agrega tu primera moto para poder agendar citas.",
                        color = Color.LightGray,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(vehiculos) { v -> VehiculoCard(v, azul) }
                }
            }

            Spacer(Modifier.height(14.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(Brush.horizontalGradient(listOf(Color(0xFF1E5BFF), Color(0xFF4DA3FF))))
                    .clickable { onNavigateToRegistrarMoto() },
                contentAlignment = Alignment.Center
            ) {
                Text("+  Agregar Vehículo", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun VehiculoCard(v: Vehiculo, azul: Color) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = Color.Black.copy(alpha = 0.9f),
        border = BorderStroke(1.5.dp, azul)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(azul.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                when {
                    v.fotoUri != null -> {
                        Image(
                            painter = rememberAsyncImagePainter(v.fotoUri),
                            contentDescription = "Foto ${v.modelo}",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    v.fotoBitmap != null -> {
                        Image(
                            bitmap = v.fotoBitmap.asImageBitmap(),
                            contentDescription = "Foto ${v.modelo}",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    else -> {
                        Icon(Icons.Filled.TwoWheeler, null, tint = Color(0xFF4DA3FF))
                    }
                }
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("${v.marca} ${v.modelo}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Año: ${v.anio} • ${v.color}", color = Color.LightGray, fontSize = 13.sp)
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF1B2433))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(v.placa, color = Color(0xFF3DDC84), fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }
}