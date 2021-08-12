package com.makeevrserg.notificator.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.makeevrserg.notificator.network.response.flask.FlaskResponse
import com.makeevrserg.notificator.network.response.weather.Weather
import kotlinx.coroutines.Deferred
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * 'Серверная часть' для отправки уведомлений и их получения.
 * Написана на Flask. Приложена к GitHub в папке server
 */
interface FlaskService {


    /**
     * Запрос на создание пользователя. Необходим токен и имя.
     * При отсутствии параметров пользователю придет уведомление с ошибкой
     */
    @POST("register")
    public fun createUser(
        @Query("name") name: String?,
        @Query("token") token:String?
    ):Deferred<FlaskResponse>


    /**
     * Запрос на отправку уведомления с ошибкой.
     * Отправялется когда проверка на клиентской части дала отрицательный результат.
     */
    @POST("error")
    public fun sendError(
        @Query("title") title: String?,
        @Query("body") body: String?,
        @Query("data") data:Map<String,String>?,
        @Query("token") token: String?
    ):Deferred<FlaskResponse>



    /**
     * Пользователю отправляется уведомление с его именем. Если имени нет, придет уведомление с ошиьбкой.
     */
    @POST("getName")
    public fun getName(
        @Query("name") name: String,
        @Query("token") token: String?
    ):Deferred<FlaskResponse>


    /**
     * Отправляется запрос на сервер на получение актуальной погоды в Ростове
     */
    @POST("getWeather")
    public fun getWeather(
        @Query("token") token:String?
    ):Deferred<FlaskResponse>

}

/**
 * Нужно не забыть указать верный адрес сервера
 */
private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl("http://192.168.1.142:5000/")
    .build()

object FlaskAPI {
    val retrofitService: FlaskService by lazy { retrofit.create(FlaskService::class.java) }
}
