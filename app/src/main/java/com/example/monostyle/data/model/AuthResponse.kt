package com.example.monostyle.data.model

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    val token: String,
    @SerializedName("tipoToken") val tipo: String? = null,
    val idCliente: Long? = null,
    val nombre: String? = null,
    val email: String? = null,
    val telefono: String? = null
)