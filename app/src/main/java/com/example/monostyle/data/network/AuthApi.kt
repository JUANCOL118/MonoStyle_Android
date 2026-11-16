package com.example.monostyle.data.network

import com.example.monostyle.data.model.AuthResponse
import com.example.monostyle.data.model.LoginRequest
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("api/auth/olvide-clave")
    suspend fun olvideClave(@Body request: RequestBody): Response<Void>

    @POST("api/auth/restablecer-clave")
    suspend fun restablecerClave(@Body request: RequestBody): Response<Void>

    @POST("api/auth/google")
    suspend fun loginGoogle(@Body request: RequestBody): Response<AuthResponse>
}