package com.makeevrserg.notificator.ui.city

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.makeevrserg.notificator.network.CityService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CityViewModel : ViewModel() {

    //Список для AutoCompleteTextView
    private val _days = MutableLiveData(arrayOf<String>())
    public val days: LiveData<Array<String>>
        get() = _days

    //После ввода текста через Retrofit получаем новые адреса.
    //Может выпасть Exception. Можно только 2 запроса в секунду делать из-за бесплатной версии ключа API
    fun onTextChanged(text: CharSequence?) {
        text ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val cities = CityService().getCurrentCity(text.toString()).await()
                val list = mutableListOf<String>()
                for (city in cities)
                    list.add(city.display_name)
                _days.postValue(list.toTypedArray())
            } catch (e: Exception) {
                //Стоит ограничение на 2 запросов в секунду.
            }
        }
    }

    init {
        //Подписываемся на топик weather чтобы получать уведомления.
        Firebase.messaging.subscribeToTopic("weather")
    }


}