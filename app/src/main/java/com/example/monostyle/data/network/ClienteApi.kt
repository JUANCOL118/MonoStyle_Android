package com.example.monostyle.data.network

import com.example.monostyle.data.model.CitaResponse
import com.example.monostyle.data.model.MotoResponse
import com.example.monostyle.data.model.SolicitudServicioDto
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ClienteApi {

    @POST("api/clientes")
    suspend fun registrarCliente(
        @Body request: RequestBody
    ): Response<Void>
    @POST("api/clientes/solicitar-servicio")
    suspend fun solicitarServicio(@Body request: SolicitudServicioDto): Response<Void>
    @GET("api/clientes/ordenes/{idOrden}/estado")
    suspend fun consultarEstadoOrden(@Path("idOrden") idOrden: Long): Response<String>

    @GET("api/clientes/ordenes/{idOrden}/factura")
    suspend fun verDetalleFactura(@Path("idOrden") idOrden: Long): Response<String>

    @PUT("api/clientes/cambiar-clave")
    suspend fun cambiarClave(@Body request: RequestBody): Response<Void>

    @GET("api/mis-motos")
    suspend fun misMotos(): Response<List<MotoResponse>>

    @POST("api/mis-motos")
    suspend fun agregarMoto(@Body request: RequestBody): Response<MotoResponse>

    @GET("api/mis-citas")
    suspend fun misCitas(): Response<List<CitaResponse>>

    @POST("api/mis-citas")
    suspend fun agendarCita(@Body request: RequestBody): Response<CitaResponse>
}