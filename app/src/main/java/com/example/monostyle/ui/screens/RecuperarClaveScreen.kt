package com.example.monostyle.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.monostyle.data.network.AuthApi
import com.example.monostyle.data.network.RetrofitClient
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

@Composable
fun RecuperarClaveScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var paso by remember { mutableIntStateOf(1) }
    var email by remember { mutableStateOf("") }
    var codigo by remember { mutableStateOf("") }
    var nueva by remember { mutableStateOf("") }
    var confirmar by remember { mutableStateOf("") }
    var cargando by remember { mutableStateOf(false) }

    val azul = Color(0xFF0055FF)
    val colores = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Color(0xFF2F80FF),
        unfocusedBorderColor = azul,
        focusedContainerColor = Color.Black.copy(alpha = 0.85f),
        unfocusedContainerColor = Color.Black.copy(alpha = 0.85f),
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        cursorColor = Color(0xFF2F80FF)
    )

    fun json(vararg p: Pair<String, String>) =
        JSONObject().apply { p.forEach { put(it.first, it.second) } }
            .toString().toRequestBody("application/json".toMediaType())

    MonoStyleBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(Modifier.fillMaxWidth().padding(top = 16.dp)) {
                IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = Color.White)
                }
                Text("Recuperar contraseña", color = Color.White, fontSize = 22.sp,
                    fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Center))
            }

            Spacer(Modifier.height(32.dp))
            Text(
                if (paso == 1) "Escribe tu correo y te enviaremos un código de 6 dígitos."
                else "Revisa tu correo (también spam) y escribe el código con tu nueva contraseña.",
                color = Color.LightGray, fontSize = 14.sp, textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = email, onValueChange = { email = it },
                enabled = paso == 1,
                placeholder = { Text("Correo electrónico", color = Color.LightGray) },
                singleLine = true, shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = colores, modifier = Modifier.fillMaxWidth()
            )

            if (paso == 2) {
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = codigo, onValueChange = { codigo = it.filter(Char::isDigit).take(6) },
                    placeholder = { Text("Código de 6 dígitos", color = Color.LightGray) },
                    singleLine = true, shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = colores, modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = nueva, onValueChange = { nueva = it },
                    placeholder = { Text("Nueva contraseña", color = Color.LightGray) },
                    singleLine = true, shape = RoundedCornerShape(12.dp),
                    visualTransformation = PasswordVisualTransformation(),
                    colors = colores, modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = confirmar, onValueChange = { confirmar = it },
                    placeholder = { Text("Confirmar contraseña", color = Color.LightGray) },
                    singleLine = true, shape = RoundedCornerShape(12.dp),
                    visualTransformation = PasswordVisualTransformation(),
                    colors = colores, modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(24.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth().height(54.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(Brush.horizontalGradient(listOf(Color(0xFF1E5BFF), Color(0xFF4DA3FF))))
                    .clickable(enabled = !cargando) {
                        if (paso == 1) {
                            if (!email.contains("@")) {
                                Toast.makeText(context, "Escribe un correo válido", Toast.LENGTH_SHORT).show()
                                return@clickable
                            }
                            cargando = true
                            scope.launch {
                                try {
                                    val api = RetrofitClient.getClient(context).create(AuthApi::class.java)
                                    val r = api.olvideClave(json("email" to email.trim()))
                                    cargando = false
                                    if (r.isSuccessful) {
                                        paso = 2
                                        Toast.makeText(context, "Si el correo existe, te enviamos un código", Toast.LENGTH_LONG).show()
                                    } else Toast.makeText(context, "Error (${r.code()})", Toast.LENGTH_LONG).show()
                                } catch (e: Exception) {
                                    cargando = false
                                    Toast.makeText(context, "Error de red: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                                }
                            }
                        } else {
                            when {
                                codigo.length != 6 -> Toast.makeText(context, "El código tiene 6 dígitos", Toast.LENGTH_SHORT).show()
                                nueva.length < 6 -> Toast.makeText(context, "Mínimo 6 caracteres", Toast.LENGTH_SHORT).show()
                                nueva != confirmar -> Toast.makeText(context, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                                else -> {
                                    cargando = true
                                    scope.launch {
                                        try {
                                            val api = RetrofitClient.getClient(context).create(AuthApi::class.java)
                                            val r = api.restablecerClave(
                                                json("email" to email.trim(), "codigo" to codigo, "claveNueva" to nueva)
                                            )
                                            cargando = false
                                            if (r.isSuccessful) {
                                                Toast.makeText(context, "Contraseña actualizada. Inicia sesión.", Toast.LENGTH_LONG).show()
                                                onBack()
                                            } else Toast.makeText(context, "Código incorrecto o vencido", Toast.LENGTH_LONG).show()
                                        } catch (e: Exception) {
                                            cargando = false
                                            Toast.makeText(context, "Error de red: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                }
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                if (cargando) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                else Text(if (paso == 1) "Enviar código" else "Cambiar contraseña",
                    color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}