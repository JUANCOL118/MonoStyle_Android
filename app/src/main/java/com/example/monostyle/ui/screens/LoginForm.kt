package com.example.monostyle.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.monostyle.R

@Composable
fun LoginForm(
    correo: String,
    onCorreoChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    isLoading: Boolean,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onForgotClick: () -> Unit = {},
    onGoogleClick: () -> Unit = {},
    onFacebookClick: () -> Unit = {}
) {
    val azul = Color(0xFF0055FF)
    val azulClaro = Color(0xFF2F80FF)
    var passwordVisible by remember { mutableStateOf(false) }
    var recordarme by remember { mutableStateOf(false) }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = azulClaro,
        unfocusedBorderColor = azul,
        focusedContainerColor = Color.Black.copy(alpha = 0.85f),
        unfocusedContainerColor = Color.Black.copy(alpha = 0.85f),
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        focusedLabelColor = azulClaro,
        unfocusedLabelColor = Color.LightGray,
        focusedLeadingIconColor = Color.White,
        unfocusedLeadingIconColor = Color.White,
        cursorColor = azulClaro
    )

    MonoStyleBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Image(
                painter = painterResource(id = R.drawable.logo_monostyle),
                contentDescription = "Logo MonoStyle",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .height(150.dp)
                    .fillMaxWidth()
            )

            Text(
                text = buildAnnotatedString {
                    withStyle(
                        SpanStyle(
                            brush = Brush.verticalGradient(
                                listOf(Color(0xFF5BB0FF), Color(0xFF0055FF))
                            )
                        )
                    ) { append("MONO") }
                    withStyle(
                        SpanStyle(
                            brush = Brush.verticalGradient(
                                listOf(Color.White, Color(0xFF9AA3AD))
                            )
                        )
                    ) { append("STYLE") }
                },
                fontSize = 40.sp,
                fontWeight = FontWeight.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Bienvenido",
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Inicia Sesión en tu cuenta",
                color = Color.LightGray,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = correo,
                onValueChange = onCorreoChange,
                placeholder = { Text("Correo electrónico o usuario", color = Color.LightGray) },
                leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                colors = fieldColors,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
                placeholder = { Text("Contraseña", color = Color.LightGray) },
                leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.Visibility
                            else Icons.Filled.VisibilityOff,
                            contentDescription = "Ver contraseña",
                            tint = Color.White
                        )
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                visualTransformation = if (passwordVisible) VisualTransformation.None
                else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                colors = fieldColors,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = recordarme,
                        onCheckedChange = { recordarme = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = azul,
                            uncheckedColor = Color.LightGray,
                            checkmarkColor = Color.White
                        )
                    )
                    Text("Recordarme", color = Color.White, fontSize = 13.sp)
                }
                Text(
                    text = "¿Olvidaste tu contraseña?",
                    color = azulClaro,
                    fontSize = 13.sp,
                    modifier = Modifier.clickable { onForgotClick() }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Botón con degradado
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(
                        Brush.horizontalGradient(listOf(Color(0xFF1E5BFF), Color(0xFF4DA3FF)))
                    )
                    .clickable(enabled = !isLoading) { onLoginClick() },
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        "Inicia Sesión",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Separador
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(azul)
                )
                Box(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(azul)
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(azul)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            SocialButton(
                texto = "Continuar con Google",
                inicial = "G",
                colorInicial = Color(0xFFEA4335),
                onClick = onGoogleClick
            )
            Spacer(modifier = Modifier.height(12.dp))
            SocialButton(
                texto = "Continuar con Facebook",
                inicial = "f",
                colorInicial = Color(0xFF1877F2),
                onClick = onFacebookClick
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("¿No tienes una cuenta?", color = Color.White, fontSize = 13.sp)
                Text(
                    text = "Regístrate aquí",
                    color = azulClaro,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onRegisterClick() }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SocialButton(
    texto: String,
    inicial: String,
    colorInicial: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(26.dp),
        color = Color.Black.copy(alpha = 0.9f),
        border = BorderStroke(1.5.dp, Color(0xFF0055FF))
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Text(inicial, color = colorInicial, fontWeight = FontWeight.Black, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(texto, color = Color.White, fontSize = 15.sp)
        }
    }
}