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


const val WEATHER_KEY = "3e3eddc996fca6b3281a545d47b5463c"

//https://api.locationiq.com/v1/autocomplete.php?key=pk.bbacc9946a4f85dac8af6df0ccf12e5f&q=Rostov&tag=place:city



interface WeatherService {

    @GET("weather")
    fun getCurrentWeather(
        @Query("q") location: String="Rostov-on-Don,%20Rostov%20Oblast,%20Russia",
        @Query("units") tag: String = "metric",
        @Query("lang") lang:String ="ru"
    ): Deferred<Weather>



    companion object {
        operator fun invoke(
            //connectivityInterceptor: ConnectivityInterceptor
        ): WeatherService {
            val requestInterceptor = Interceptor { chain ->

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

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(requestInterceptor)
                .build()

            return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(WeatherService::class.java)
        }
    }
}