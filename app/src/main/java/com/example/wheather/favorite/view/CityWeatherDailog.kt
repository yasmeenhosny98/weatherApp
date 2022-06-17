package com.example.wheather.favorite.view

import WeatherResponse
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.wheather.MyApplication
import com.example.wheather.R
import com.example.wheather.favorite.viewmodel.FavoriteViewModel
import com.example.wheather.favorite.viewmodel.FavoriteViewModelFactory
import com.example.wheather.utils.ConstantsVals
import com.example.wheather.utils.convertLongToStringDate
import com.example.wheather.utils.isConnectedToInternet

class CityWeatherDailog : DialogFragment() {

    //view model vars
    lateinit var viewModelFactory: FavoriteViewModelFactory
    lateinit var viewModel: FavoriteViewModel

    //XML elements
    lateinit var tv_address: TextView
    lateinit var tv_temp: TextView
    lateinit var tv_desc: TextView
    lateinit var tv_date: TextView
    lateinit var tv_humidity: TextView
    lateinit var tv_perssure: TextView
    lateinit var tv_wind_speed: TextView
    lateinit var iv_main_icon: ImageView

    //parameters
    lateinit var lat: String
    lateinit var lon: String
    lateinit var cityName: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.dailog_city_weather, container, false)
        setupViewModel()
        lat = requireArguments().getString("lat")!!
        lon = requireArguments().getString("lon")!!
        cityName = requireArguments().getString("city")!!
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupXML(view)
    }

    override fun onStart() {
        super.onStart()
        if (isConnectedToInternet(requireContext())) {
            viewModel.getAllWeatherData(lat, lon)
        }
    }

    private fun setupViewModel() {
        viewModelFactory =
            FavoriteViewModelFactory((requireActivity().application!! as MyApplication).repository)
        this.viewModel =
            ViewModelProvider(this, viewModelFactory).get(FavoriteViewModel::class.java)
        viewModel.observableWeatherData().observe(viewLifecycleOwner, weatherData)
        viewModel.observableErrorMessage().observe(viewLifecycleOwner) {
            Log.i(ConstantsVals.log, "there is some error : ${it}")
        }
    }

    private fun setupXML(view: View) {
        tv_temp = view.findViewById(R.id.tv_main_temp)
        tv_date = view.findViewById(R.id.tv_date)
        tv_desc = view.findViewById(R.id.tv_desc)
        tv_address = view.findViewById(R.id.tv_address)
        tv_humidity = view.findViewById(R.id.tv_humidity)
        tv_perssure = view.findViewById(R.id.tv_pressure)
        tv_wind_speed = view.findViewById(R.id.tv_wind_speed)
        iv_main_icon = view.findViewById(R.id.iv_main_icon)
    }

    private val weatherData: (it: WeatherResponse) -> Unit = { it: WeatherResponse ->
        run {
            Glide.with(this)
                .load("https://openweathermap.org/img/wn/${it.current.weather[0].icon}.png")
                .into(iv_main_icon)
            tv_date.text = "${convertLongToStringDate(it.current.dt, "EEE, MMM dd")}"
            tv_desc.text = "${it.current.weather[0].description}"
            tv_address.text = cityName
            tv_wind_speed.text = "${it.current.wind_speed}"
            tv_perssure.text = "${it.current.pressure}"
            tv_humidity.text = "${it.current.humidity}"
            tv_temp.text = "${it.current.temp}"
        }


    }
}