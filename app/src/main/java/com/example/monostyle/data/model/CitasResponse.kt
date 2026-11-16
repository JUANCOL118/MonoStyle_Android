package com.example.monostyle.data.model

data class CitaResponse(
    val vehiculo: String,
    val servicio: String,
    val fecha: String,
    val hora: String,
    val observaciones: String? = null,
    val estado: String
)