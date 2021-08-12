package com.makeevrserg.notificator.network



import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.makeevrserg.notificator.network.response.address.Place
import kotlinx.coroutines.Deferred
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


const val API_KEY = "pk.bbacc9946a4f85dac8af6df0ccf12e5f"

//https://api.locationiq.com/v1/autocomplete.php?key=pk.bbacc9946a4f85dac8af6df0ccf12e5f&q=Rostov&tag=place:city


//http://api.apixu.com/v1/current.json?key=89e8bd89085b41b7a4b142029180210&q=London&lang=en
interface CityService {


    /**
     * Легкий аналог GooglePlaces.
     * При вводе локации пользователю возвращается результат городов,
     * которые содержат последовательность символов, введенную польльзователем.
     * @param q введенная пользователем локация
     * @param tag здесь указаны только города
     */
    @GET("autocomplete.php")
    fun getCurrentCity(
        @Query("q") location: String,
        @Query("tag") tag: String = "place:city"
    ): Deferred<Place>

}

private val requestInterceptor = Interceptor { chain ->

    val url = chain.request()
        .url()
        .newBuilder()
        .addQueryParameter("key", API_KEY)
        .build()
    val request = chain.request()
        .newBuilder()
        .url(url)
        .build()

    return@Interceptor chain.proceed(request)
}

private val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(requestInterceptor)
    .build()


private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .client(okHttpClient)
    .baseUrl("https://api.locationiq.com/v1/")
    .build()

object CityAPI {
    val retrofitService: CityService by lazy { retrofit.create(CityService::class.java) }
}