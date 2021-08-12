package com.makeevrserg.notificator.network



import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.makeevrserg.notificator.network.response.address.Place
import com.makeevrserg.notificator.network.response.weather.Weather
import kotlinx.coroutines.Deferred
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query



//Ключ от API
const val WEATHER_KEY = "3e3eddc996fca6b3281a545d47b5463c"

//https://api.locationiq.com/v1/autocomplete.php?key=pk.bbacc9946a4f85dac8af6df0ccf12e5f&q=Rostov&tag=place:city



interface WeatherService {

    /**
    * Получаем погоду. Задаем параметры.
     * Возвращаем результат в Deferred объект Weather
     * @param location город
     * @param tag система счисления
     * @param lang язык
    */
    @GET("weather")
    fun getCurrentWeather(
        @Query("q") location: String="Rostov-on-Don,%20Rostov%20Oblast,%20Russia",
        @Query("units") tag: String = "metric",
        @Query("lang") lang:String ="ru"
    ): Deferred<Weather>


}

private val requestInterceptor = Interceptor { chain ->

    val url = chain.request()
        .url()
        .newBuilder()
        .addQueryParameter("appid", WEATHER_KEY)
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
    .baseUrl("https://api.openweathermap.org/data/2.5/")
    .build()

object WeatherAPI {
    val retrofitService: WeatherService by lazy { retrofit.create(WeatherService::class.java) }
}



