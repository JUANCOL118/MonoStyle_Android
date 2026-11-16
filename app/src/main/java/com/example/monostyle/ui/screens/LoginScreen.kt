package com.example.monostyle.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.example.monostyle.data.model.LoginRequest
import com.example.monostyle.data.network.AuthApi
import com.example.monostyle.data.network.RetrofitClient
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var usuario by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    LoginForm(
        correo = usuario,
        onCorreoChange = { usuario = it },
        password = password,
        onPasswordChange = { password = it },
        isLoading = isLoading,
        onLoginClick = {
            if (usuario.isBlank() || password.isBlank()) {
                Toast.makeText(context, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                return@LoginForm
            }

            isLoading = true
            coroutineScope.launch {
                try {
                    val api = RetrofitClient.getClient(context).create(AuthApi::class.java)
                    val request = LoginRequest(nombre = usuario.trim(), credenciales = password)
                    val response = api.login(request)

                    isLoading = false

                    if (response.isSuccessful && response.body() != null) {
                        val authResponse = response.body()!!

                        val sharedPrefs = context.getSharedPreferences("MonoStylePrefs", Context.MODE_PRIVATE)
                        sharedPrefs.edit()
                            .putString("JWT_TOKEN", authResponse.token)
                            .putLong("ID_CLIENTE", authResponse.idCliente ?: -1L)
                            .putString("NOMBRE_USUARIO", authResponse.nombre ?: "")
                            .putString("EMAIL_USUARIO", authResponse.email ?: "")
                            .putString("TELEFONO_USUARIO", authResponse.telefono ?: "")
                            .apply()

                        Toast.makeText(context, "¡Bienvenido!", Toast.LENGTH_SHORT).show()
                        onLoginSuccess()
                    } else {
                        Toast.makeText(context, "Error: Credenciales incorrectas", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    isLoading = false
                    Toast.makeText(context, "Error de conexión: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            }
        },
        onRegisterClick = onNavigateToRegister,
        onGoogleClick = {
            coroutineScope.launch {
                try {
                    val cm = androidx.credentials.CredentialManager.create(context)
                    val option = com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
                        .Builder("TU_WEB_CLIENT_ID.apps.googleusercontent.com").build()
                    val request = androidx.credentials.GetCredentialRequest.Builder()
                        .addCredentialOption(option).build()

                    val result = cm.getCredential(context, request)
                    val google = com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
                        .createFrom(result.credential.data)

                    val body = org.json.JSONObject().put("idToken", google.idToken).toString()
                        .toRequestBody("application/json".toMediaType())
                    val api = RetrofitClient.getClient(context).create(AuthApi::class.java)
                    val response = api.loginGoogle(body)

                    if (response.isSuccessful && response.body() != null) {
                        val a = response.body()!!
                        context.getSharedPreferences("MonoStylePrefs", Context.MODE_PRIVATE).edit()
                            .putString("JWT_TOKEN", a.token)
                            .putLong("ID_CLIENTE", a.idCliente ?: -1L)
                            .putString("NOMBRE_USUARIO", a.nombre ?: "")
                            .putString("EMAIL_USUARIO", a.email ?: "")
                            .putString("TELEFONO_USUARIO", a.telefono ?: "")
                            .apply()
                        onLoginSuccess()
                    } else {
                        Toast.makeText(context, "No se pudo iniciar con Google (${response.code()})", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Google: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            }
        },
        onFacebookClick = {
            Toast.makeText(context, "Próximamente", Toast.LENGTH_SHORT).show()
        }
    )
}