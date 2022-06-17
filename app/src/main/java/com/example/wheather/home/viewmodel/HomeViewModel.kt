package com.example.wheather.home.viewmodel

import Weather
import WeatherResponse
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wheather.data.repository.Repository
import com.example.wheather.utils.ConstantsVals
import kotlinx.coroutines.*

class HomeViewModel constructor(private val repository: Repository) : ViewModel() {

    private val weatherData = MutableLiveData<WeatherResponse>()
    private val errorMessage = MutableLiveData<String>()
    var job: Job? = null
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        errorMessage.postValue("Exception handled: ${throwable.localizedMessage}")
    }


    fun getAllWeatherData(
        lat: String = "33.44",
        lon: String = "94.04"
    ) {
        Log.i(ConstantsVals.log, "we are in view model BEFORE job")
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            Log.i(ConstantsVals.log, "we are in view model AFTER job")
            val response = repository.getAllWeatherData(lat, lon)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    Log.i(ConstantsVals.log, "data in view model " + response.body())
                    weatherData.postValue(response.body())
                } else {
                    Log.i(ConstantsVals.log, "an error happain : " + response.errorBody())
                    errorMessage.postValue("Error : ${response.errorBody()} ")
                }
            }
        }
    }

    fun observableWeatherData(): LiveData<WeatherResponse> = weatherData

    fun observableErrorMessage(): LiveData<String> = errorMessage

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}