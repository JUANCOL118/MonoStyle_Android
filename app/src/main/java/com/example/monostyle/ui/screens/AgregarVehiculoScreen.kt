import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.monostyle.ui.screens.MonoStyleBackground
import com.example.monostyle.ui.screens.Vehiculo

@Composable
fun AgregarVehiculoScreen(
    onBack: () -> Unit,
    onGuardar: (Vehiculo) -> Unit
) {
    val marcas = listOf(
        "Yamaha", "Honda", "Suzuki", "Kawasaki", "Bajaj", "AKT",
        "TVS", "KTM", "Hero", "Auteco", "Victory", "Otra"
    )

    var marca by remember { mutableStateOf("") }
    var modelo by remember { mutableStateOf("") }
    var placa by remember { mutableStateOf("") }
    var anio by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("") }
    var expandido by remember { mutableStateOf(false) }

    // Estados para la foto de la moto
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedImageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var showPhotoDialog by remember { mutableStateOf(false) }

    // Launchers para Galería y Cámara
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            selectedImageBitmap = null
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            selectedImageBitmap = bitmap
            selectedImageUri = null
        }
    }

    val azul = Color(0xFF0055FF)
    val context = LocalContext.current

    val colores = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Color(0xFF2F80FF),
        unfocusedBorderColor = azul,
        focusedContainerColor = Color.Black.copy(alpha = 0.85f),
        unfocusedContainerColor = Color.Black.copy(alpha = 0.85f),
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        cursorColor = Color(0xFF2F80FF)
    )

    @Composable
    fun campo(
        valor: String,
        onChange: (String) -> Unit,
        placeholder: String,
        icono: ImageVector,
        teclado: KeyboardType = KeyboardType.Text
    ) {
        OutlinedTextField(
            value = valor,
            onValueChange = onChange,
            placeholder = { Text(placeholder, color = Color.LightGray) },
            leadingIcon = { Icon(icono, null, tint = Color.White) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = teclado),
            colors = colores,
            modifier = Modifier.fillMaxWidth()
        )
    }

    MonoStyleBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = Color.White)
                }
                Text(
                    "Agregar Vehículo",
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Spacer(Modifier.height(20.dp))

            // Recuadro interactivo para la foto (Abre el diálogo de cámara/galería)
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier
                        .size(130.dp)
                        .clickable { showPhotoDialog = true },
                    shape = RoundedCornerShape(16.dp),
                    color = Color.Black.copy(alpha = 0.85f),
                    border = BorderStroke(1.5.dp, Color.Gray)
                ) {
                    when {
                        selectedImageUri != null -> {
                            Image(
                                painter = rememberAsyncImagePainter(selectedImageUri),
                                contentDescription = "Foto moto",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        selectedImageBitmap != null -> {
                            Image(
                                bitmap = selectedImageBitmap!!.asImageBitmap(),
                                contentDescription = "Foto moto",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        else -> {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(Icons.Filled.TwoWheeler, null, tint = Color.White, modifier = Modifier.size(44.dp))
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    "Sube una foto\n(opcional)",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Marca (desplegable)
            Box {
                Surface(
                    modifier = Modifier.fillMaxWidth().height(56.dp).clickable { expandido = true },
                    shape = RoundedCornerShape(12.dp),
                    color = Color.Black.copy(alpha = 0.85f),
                    border = BorderStroke(1.5.dp, azul)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.TwoWheeler, null, tint = Color.White)
                        Spacer(Modifier.width(12.dp))
                        Text(
                            marca.ifBlank { "Marca" },
                            color = if (marca.isBlank()) Color.LightGray else Color.White,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(Icons.Filled.KeyboardArrowDown, null, tint = Color.White)
                    }
                }
                DropdownMenu(expanded = expandido, onDismissRequest = { expandido = false }) {
                    marcas.forEach { m ->
                        DropdownMenuItem(
                            text = { Text(m) },
                            onClick = { marca = m; expandido = false }
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))
            campo(modelo, { modelo = it }, "Modelo", Icons.Filled.Settings)
            Spacer(Modifier.height(14.dp))
            campo(placa, { placa = it.uppercase().take(7) }, "Placa", Icons.Filled.Info)
            Spacer(Modifier.height(14.dp))
            campo(anio, { anio = it.filter { c -> c.isDigit() }.take(4) }, "Año", Icons.Filled.DateRange, KeyboardType.Number)
            Spacer(Modifier.height(14.dp))
            campo(color, { color = it }, "Color", Icons.Filled.Edit)

            Spacer(Modifier.height(28.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(Brush.horizontalGradient(listOf(Color(0xFF1E5BFF), Color(0xFF4DA3FF))))
                    .clickable {
                        if (marca.isBlank() || modelo.isBlank() || placa.isBlank() ||
                            anio.isBlank() || color.isBlank()
                        ) {
                            Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                            return@clickable
                        }
                        val a = anio.toIntOrNull() ?: 0
                        if (a < 1980 || a > 2030) {
                            Toast.makeText(context, "Año no válido", Toast.LENGTH_SHORT).show()
                            return@clickable
                        }
                        onGuardar(
                            Vehiculo(
                                marca = marca,
                                modelo = modelo.trim(),
                                placa = placa.trim(),
                                anio = anio,
                                color = color.trim(),
                                fotoUri = selectedImageUri,
                                fotoBitmap = selectedImageBitmap
                            )
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Text("Guardar Vehículo", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(24.dp))
        }
    }

    // Diálogo para escoger entre Galería y Cámara
    if (showPhotoDialog) {
        AlertDialog(
            onDismissRequest = { showPhotoDialog = false },
            containerColor = Color(0xFF111827),
            titleContentColor = Color.White,
            textContentColor = Color.LightGray,
            title = { Text("Foto del vehículo", fontWeight = FontWeight.Bold) },
            text = { Text("¿Cómo deseas agregar la foto?") },
            confirmButton = {
                TextButton(onClick = {
                    showPhotoDialog = false
                    galleryLauncher.launch("image/*")
                }) {
                    Text("Galería", color = Color(0xFF4DA3FF), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showPhotoDialog = false
                    cameraLauncher.launch(null)
                }) {
                    Text("Cámara", color = Color(0xFF4DA3FF), fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}