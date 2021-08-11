package com.makeevrserg.notificator.ui.weather

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.AttributeSet
import android.view.View
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.makeevrserg.notificator.R
import com.makeevrserg.notificator.databinding.ActivityWeatherBinding
import com.makeevrserg.notificator.ui.city.CityViewModel

class WeatherActivity : AppCompatActivity() {
    private val viewModel: WeatherViewModel by lazy {
        ViewModelProvider(this).get(WeatherViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)
        val binding:ActivityWeatherBinding = DataBindingUtil.setContentView(this,R.layout.activity_weather)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
    }


}