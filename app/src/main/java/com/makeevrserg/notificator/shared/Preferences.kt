package com.makeevrserg.notificator.shared


import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.makeevrserg.notificator.R
import java.lang.Exception


/**
 * Класс с настройками, где хранится имя пользователя
 */
class Preferences(private val application: Application) {

    private fun getSharedPref(): SharedPreferences? {
        return try {
            application.getSharedPreferences(
                application.getString(R.string.preferences),
                Context.MODE_PRIVATE
            )
        } catch (e: Exception) {
            return null
        }
    }

    /**
     * Сохраненеие пользователя
     */
    fun saveUser(user: String) {
        val sharedPref = getSharedPref() ?: return
        with(sharedPref.edit()) {
            putString(application.getString(R.string.user_name), user)
            apply()
        }
    }


    /**
     * Загрузка ползователя
     */
    fun loadUser(): String? {
        val sharedPref = getSharedPref() ?: return null
        return sharedPref.getString(application.getString(R.string.user_name), null)

    }

}