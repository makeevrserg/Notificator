package com.makeevrserg.notificator.ui.city

import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging
import com.makeevrserg.notificator.R
import com.makeevrserg.notificator.network.CityAPI
import com.makeevrserg.notificator.network.CityService
import com.makeevrserg.notificator.network.FlaskAPI
import com.makeevrserg.notificator.network.FlaskService
import com.makeevrserg.notificator.shared.Preferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class CityViewModel(application: Application) : AndroidViewModel(application) {
    val _application = application

    /**
     * Список для AutoCompleteTextView, в котором пользователь вводит город
     */
    private val _days = MutableLiveData(arrayOf<String>())
    public val days: LiveData<Array<String>>
        get() = _days

    /**
     * Настройки, которые хранят пользоватльское имя
     */
    private val preferences = Preferences(application)


    /**
     * Поле, которое изменяется при вводе имени в TextInputEditText
     */
    private var userName: String? = null

    /**
     * Токен пользователя от Firebase
     *
     * Нужен чтобы отправлять уведомление именно ему, а не группе пользователей, которые подписаны на topic
     */
    private var token: String? = null
    private val getToken: String?
        get() = token

    /**
     * После ввода получаем через Retrofit новые адреса
     *
     * Может выпатException. Можно только 2 запроса в секунду делать из-за бесплатной версии ключа API
     */
    fun onTextChanged(text: CharSequence?) {
        text ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val cities = CityAPI.retrofitService.getCurrentCity(text.toString()).await()
                val list = mutableListOf<String>()
                for (city in cities)
                    list.add(city.display_name)
                _days.postValue(list.toTypedArray())
            } catch (e: Exception) {
                //Стоит ограничение на 2 запросов в секунду.
            }
        }
    }


    /**
     * При нажатии на кнопку посылается запрос на сервер и пользователь получает уведомление
     */
    public fun onGetWeatherClicked() {
        viewModelScope.launch(Dispatchers.IO) {
            FlaskAPI.retrofitService.getWeather(getToken).await()
        }

    }

    /**
     * При нажатии на получение собственного имени берется имя из SharedPrefs.
     * Если имени там нет - пользователь получает уведомление с ошибкой
     */
    public fun onGetCurrentNameClicked() {
        val name = preferences.loadUser()
        if (name == null) {
            viewModelScope.launch(Dispatchers.IO) {
                FlaskAPI.retrofitService.sendError(
                    "Ошибка!",
                    "Не удалось получить имя пользователя!",
                    null,
                    getToken
                )
            }
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                FlaskAPI.retrofitService.getName(name, getToken)

            }
        }
    }


    /**
     * Созданге нового пользователя. Если пользователь уже существует - сервер пришлет уведомление.
     * Если пользователь новый - сервер пришлет результат ok, после чего можно будет сохранить новое имя
     *
     * Если имя не введено - посылается запрос на сервер для уведомления пользователя об ошибке.
     */
    public fun onRegisterClicked() {

        if (userName == null) {
            viewModelScope.launch(Dispatchers.IO) {
                FlaskAPI.retrofitService.sendError(
                    "Ошибка!",
                    "Вы не ввели имя пользователя!",
                    null,
                    getToken
                )
            }
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                val _userName = userName
                val response = FlaskAPI.retrofitService.createUser(_userName, token).await()
                if (response.result == "ok")
                    preferences.saveUser(_userName ?: return@launch)


            }
        }
    }

    /**
     * Уведомление с прогрессом.
     * Ничего не загружает, просто показывает уведомление.
     */
    public fun onDownloadButtonClicked() {
        viewModelScope.launch(Dispatchers.Main) {
            val builder = NotificationCompat.Builder(
                _application,
                _application.getString(R.string.default_channel)
            )
                .setContentTitle("Симуляция загрузки")
                .setContentText("В процессе")
                .setSmallIcon(R.drawable.ic_stat_ic_notification)
                .setPriority(NotificationCompat.PRIORITY_LOW)

            val notificationManager =
                _application.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager



            for (i in 0..100) {
                builder.setProgress(100, i, false)
                notificationManager.notify(7777, builder.build())
                delay(100)
            }
            builder.setContentText("Симуляция завершена").setProgress(0, 0, false)
            notificationManager.notify(7777, builder.build())
        }


    }

    /**
     * После вводе в TextInputEditText записывается в name новая последовательность символов
     */
    public fun notifyNameChanged(name: CharSequence?) {
        name ?: return
        userName = name.toString()
    }

    /**
     * Функция загрузки токена пользователя. Инициализируется при запуске.
     * В документации было сказано, что токен может измениться при некоторых условиях
     */
    private fun loadToken() = FirebaseMessaging.getInstance().token.addOnCompleteListener {
        token = it.result
        Log.d("CityViewModel", "loadToken: ${token}")
    }

    init {
        //Подписываемся на топик weather чтобы получать уведомления.
        Firebase.messaging.subscribeToTopic("weather")
        viewModelScope.launch(Dispatchers.IO) {
            loadToken()
        }
    }


}