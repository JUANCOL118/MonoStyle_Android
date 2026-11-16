package com.example.monostyle.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.monostyle.data.network.ClienteApi
import com.example.monostyle.data.network.RetrofitClient
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

private val AZUL = Color(0xFF0055FF)
private val AZUL_CLARO = Color(0xFF2F80FF)
private const val PREFS = "MonoStylePrefs"

@Composable
fun PerfilScreen(onLogout: () -> Unit) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences(PREFS, Context.MODE_PRIVATE) }

    var pantalla by remember { mutableIntStateOf(0) }

    var nombre by remember { mutableStateOf(prefs.getString("NOMBRE_USUARIO", "") ?: "") }
    var correo by remember { mutableStateOf(prefs.getString("EMAIL_USUARIO", "") ?: "") }
    var telefono by remember { mutableStateOf(prefs.getString("TELEFONO_USUARIO", "") ?: "") }

    when (pantalla) {
        0 -> PerfilMenu(
            nombre = nombre,
            onMisDatos = { pantalla = 1 },
            onCambiarClave = { pantalla = 2 },
            onLogout = {
                prefs.edit().remove("JWT_TOKEN").apply()
                onLogout()
            }
        )
        1 -> MisDatosScreen(
            nombre = nombre,
            correo = correo,
            telefono = telefono,
            onBack = { pantalla = 0 },
            onGuardar = { n, c, t ->
                nombre = n; correo = c; telefono = t
                prefs.edit()
                    .putString("NOMBRE_USUARIO", n)
                    .putString("EMAIL_USUARIO", c)
                    .putString("TELEFONO_USUARIO", t)
                    .apply()
                Toast.makeText(context, "Datos actualizados", Toast.LENGTH_SHORT).show()
                pantalla = 0
            }
        )
        else -> CambiarClaveScreen(onBack = { pantalla = 0 })
    }
}

@Composable
private fun Encabezado(titulo: String, onBack: (() -> Unit)?) {
    Box(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
        if (onBack != null) {
            IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = Color.White)
            }
        }
        Text(
            titulo,
            color = Color.White,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
private fun Avatar() {
    Box(
        modifier = Modifier
            .size(100.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.85f))
            .then(Modifier.padding(2.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(Icons.Filled.Person, null, tint = AZUL_CLARO, modifier = Modifier.size(64.dp))
    }
}

@Composable
private fun PerfilMenu(
    nombre: String,
    onMisDatos: () -> Unit,
    onCambiarClave: () -> Unit,
    onLogout: () -> Unit
) {
    MonoStyleBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Encabezado("Mi Perfil", null)
            Spacer(Modifier.height(24.dp))
            Avatar()
            Spacer(Modifier.height(12.dp))
            Text(
                nombre.ifBlank { "Mi cuenta" },
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(28.dp))

            ItemMenu(Icons.Filled.Person, "Mis datos", Color.White, AZUL, onMisDatos)
            Spacer(Modifier.height(12.dp))
            ItemMenu(Icons.Filled.Lock, "Cambiar Contraseña", Color.White, AZUL, onCambiarClave)
            Spacer(Modifier.height(12.dp))
            ItemMenu(
                Icons.AutoMirrored.Filled.Logout, "Cerrar Sesión",
                Color(0xFFFF4D4D), Color(0xFFFF4D4D), onLogout
            )
        }
    }
}

@Composable
private fun ItemMenu(
    icono: ImageVector,
    texto: String,
    colorTexto: Color,
    colorBorde: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth().height(56.dp).clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = Color.Black.copy(alpha = 0.9f),
        border = BorderStroke(1.5.dp, colorBorde)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icono, null, tint = colorTexto, modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(14.dp))
            Text(texto, color = colorTexto, fontSize = 16.sp, fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f))
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = colorTexto)
        }
    }
}

@Composable
private fun MisDatosScreen(
    nombre: String,
    correo: String,
    telefono: String,
    onBack: () -> Unit,
    onGuardar: (String, String, String) -> Unit
) {
    val context = LocalContext.current
    var n by remember { mutableStateOf(nombre) }
    var c by remember { mutableStateOf(correo) }
    var t by remember { mutableStateOf(telefono) }

    MonoStyleBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Encabezado("Mis Datos", onBack)
            Spacer(Modifier.height(20.dp))
            Avatar()
            Spacer(Modifier.height(10.dp))
            Text("Información Personal", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            Text("Consulta y actualiza tus datos", color = Color.LightGray, fontSize = 13.sp)
            Spacer(Modifier.height(20.dp))

            CampoDato("Nombre Completo", n, { n = it }, Icons.Filled.Person)
            Spacer(Modifier.height(12.dp))
            CampoDato("Correo electrónico", c, { c = it }, Icons.Filled.Email, KeyboardType.Email)
            Spacer(Modifier.height(12.dp))
            CampoDato("Teléfono", t, { t = it.filter { ch -> ch.isDigit() }.take(10) },
                Icons.Filled.Phone, KeyboardType.Phone)

            Spacer(Modifier.height(24.dp))
            BotonDegradado("Guardar Cambios") {
                if (n.isBlank()) {
                    Toast.makeText(context, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
                } else {
                    onGuardar(n.trim(), c.trim(), t.trim())
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun CampoDato(
    etiqueta: String,
    valor: String,
    onChange: (String) -> Unit,
    icono: ImageVector,
    teclado: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onChange,
        label = { Text(etiqueta, color = Color.LightGray) },
        leadingIcon = { Icon(icono, null, tint = Color.White) },
        trailingIcon = { Icon(Icons.Filled.Edit, null, tint = AZUL_CLARO, modifier = Modifier.size(18.dp)) },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(keyboardType = teclado),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AZUL_CLARO,
            unfocusedBorderColor = AZUL,
            focusedContainerColor = Color.Black.copy(alpha = 0.85f),
            unfocusedContainerColor = Color.Black.copy(alpha = 0.85f),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedLabelColor = AZUL_CLARO,
            cursorColor = AZUL_CLARO
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun CambiarClaveScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var actual by remember { mutableStateOf("") }
    var nueva by remember { mutableStateOf("") }
    var confirmar by remember { mutableStateOf("") }
    var cargando by remember { mutableStateOf(false) }

    MonoStyleBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Encabezado("Cambiar Contraseña", onBack)
            Spacer(Modifier.height(24.dp))
            Icon(Icons.Filled.Lock, null, tint = AZUL_CLARO, modifier = Modifier.size(84.dp))
            Spacer(Modifier.height(10.dp))
            Text("Actualiza tu contraseña", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            Text(
                "Ingresa tu contraseña actual y la nueva contraseña que deseas usar",
                color = Color.LightGray, fontSize = 13.sp, textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
            )

            CampoClave("Contraseña actual", actual) { actual = it }
            Spacer(Modifier.height(12.dp))
            CampoClave("Nueva contraseña", nueva) { nueva = it }
            Spacer(Modifier.height(12.dp))
            CampoClave("Confirmar nueva contraseña", confirmar) { confirmar = it }

            Spacer(Modifier.height(24.dp))
            BotonDegradado(if (cargando) "Guardando..." else "Guardar Contraseña") {
                if (cargando) return@BotonDegradado
                when {
                    actual.isBlank() || nueva.isBlank() || confirmar.isBlank() ->
                        Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                    nueva.length < 6 ->
                        Toast.makeText(context, "Mínimo 6 caracteres", Toast.LENGTH_SHORT).show()
                    nueva != confirmar ->
                        Toast.makeText(context, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                    else -> {
                        cargando = true
                        scope.launch {
                            try {
                                val json = JSONObject().apply {
                                    put("claveActual", actual)
                                    put("claveNueva", nueva)
                                }
                                val body = json.toString().toRequestBody("application/json".toMediaType())
                                val api = RetrofitClient.getClient(context).create(ClienteApi::class.java)
                                val response = api.cambiarClave(body)
                                cargando = false
                                if (response.isSuccessful) {
                                    Toast.makeText(context, "Contraseña actualizada", Toast.LENGTH_LONG).show()
                                    onBack()
                                } else {
                                    Toast.makeText(
                                        context,
                                        if (response.code() == 500 || response.code() == 400)
                                            "Contraseña actual incorrecta"
                                        else "Error (${response.code()})",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            } catch (e: Exception) {
                                cargando = false
                                Toast.makeText(context, "Error de red: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun CampoClave(placeholder: String, valor: String, onChange: (String) -> Unit) {
    var visible by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = valor,
        onValueChange = onChange,
        placeholder = { Text(placeholder, color = Color.LightGray) },
        leadingIcon = { Icon(Icons.Filled.Lock, null, tint = Color.White) },
        trailingIcon = {
            IconButton(onClick = { visible = !visible }) {
                Icon(
                    if (visible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                    null, tint = Color.White
                )
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AZUL_CLARO,
            unfocusedBorderColor = AZUL,
            focusedContainerColor = Color.Black.copy(alpha = 0.85f),
            unfocusedContainerColor = Color.Black.copy(alpha = 0.85f),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            cursorColor = AZUL_CLARO
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun BotonDegradado(texto: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(Brush.horizontalGradient(listOf(Color(0xFF1E5BFF), Color(0xFF4DA3FF))))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(texto, color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
    }
}