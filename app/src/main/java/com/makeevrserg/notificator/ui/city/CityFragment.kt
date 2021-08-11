package com.makeevrserg.notificator.ui.city

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import com.makeevrserg.notificator.R
import com.makeevrserg.notificator.databinding.WeatherFragmentBinding
import kotlinx.android.synthetic.main.weather_fragment.*


class CityFragment : Fragment() {

    private val viewModel: CityViewModel by lazy {
        ViewModelProvider(this).get(CityViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Binding
        val binding: WeatherFragmentBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.weather_fragment,
            container,
            false
        )
        //ViewModel
        val application = requireNotNull(this.activity).application
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel


        binding.atv.doOnTextChanged { text, start, before, count ->
            viewModel.onTextChanged(text)
        }




        viewModel.days.observe(viewLifecycleOwner, {
            (binding.atv as AutoCompleteTextView).setAdapter(
                ArrayAdapter<String>(
                    application,
                    android.R.layout.simple_list_item_1,
                    it
                )
            )
        })


        return binding.root
    }


}