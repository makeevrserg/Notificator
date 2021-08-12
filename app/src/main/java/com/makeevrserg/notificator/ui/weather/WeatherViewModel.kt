package com.makeevrserg.notificator.ui.weather

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.makeevrserg.notificator.network.WeatherAPI
import com.makeevrserg.notificator.network.WeatherService
import com.makeevrserg.notificator.network.response.weather.Weather
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class WeatherViewModel : ViewModel() {

    private val _weather = MutableLiveData<Weather>()
    public val weather: LiveData<Weather>
        get() = _weather

    /**
     * Загружаем погоду из WeatherService
     * -> после загрузки пользователь видит погоду на экране
     */
    init {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _weather.postValue(WeatherAPI.retrofitService.getCurrentWeather().await())
            } catch (e: Exception) {
                //Поддерживает несколько запросов в секунду. Если больше - будет BadRequest
            }
        }
    }


}