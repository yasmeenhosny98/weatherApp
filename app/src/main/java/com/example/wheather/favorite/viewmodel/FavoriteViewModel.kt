package com.example.wheather.favorite.viewmodel

import WeatherResponse
import android.util.Log
import androidx.lifecycle.*
import com.example.wheather.data.model.City
import com.example.wheather.data.repository.Repository
import com.example.wheather.utils.ConstantsVals
import kotlinx.coroutines.*

class FavoriteViewModel (private val repository: Repository) : ViewModel() {

    private val weatherData = MutableLiveData<WeatherResponse>()
    private val errorMessage = MutableLiveData<String>()
    var job: Job? = null
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        errorMessage.postValue("Exception handled: ${throwable.localizedMessage}")
    }


    val allCities: LiveData<List<City>> = repository.favCities

    fun delete(city: City) = viewModelScope.launch {
        repository.delete(city)
    }

    fun getAllWeatherData(
        lat: String = "33.44",
        lon: String = "94.04"
    ) {
        Log.i(ConstantsVals.log, "we are in view model BEFORE job")
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            Log.i(ConstantsVals.log, "we are in view model AFTER job")
            val response = repository.getAllWeatherData(lat, lon, "minutely,hourly,daily,alerts")
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
}