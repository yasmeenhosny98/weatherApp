package com.example.wheather.data.repository

import WeatherResponse
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.preference.PreferenceManager
import com.example.weatherapp.db.CityDAO
import com.example.wheather.data.model.Alert
import com.example.wheather.data.model.City
import com.example.wheather.data.retrofit.RetrofitService
import com.example.wheather.utils.ConstantsVals
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

class Repository(private val cityDao: CityDAO, private val retrofitService: RetrofitService, private val context: Context) {

    private lateinit var unit : String
    private lateinit var language : String
    private lateinit var lat: String
    private lateinit var lon: String
    private var sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    init {
        getSetting()
    }

    suspend fun getAllWeatherData(
        lat: String = this.lat,
        lon: String = this.lon,
        exclude: String = "minutely",
        units: String = unit,
        lang: String = language
    ): Response<WeatherResponse> {
        getSetting()
        return retrofitService.getAllWeatherData(lat, lon, exclude, units, lang)
    }

    private fun getSetting(){
        val languageSys = sharedPreferences.getString("language", "English").toString()
        if (languageSys.equals("English")) {
            language = "en"
        } else if (languageSys.equals("Arabic")) {
            language = "ar"
        }
        Log.v(ConstantsVals.log,languageSys)
        val units = sharedPreferences.getString("Temp" , "temp_c").toString()
        if(units.equals("temp_c")){
            unit = "metric"
        }else if(units.equals("temp_f")){
            unit = "imperial"
        }
        Log.v(ConstantsVals.log,units)
        lat = "31.7114"
        lon = "30.3173"
    }
    val favCities: LiveData<List<City>> = cityDao.getAlphabetizedWords()


    suspend fun insert(city: City) {
        cityDao.insert(city)
    }

    suspend fun delete(city: City) {
        cityDao.delete(city)
    }

    val getAlerts: LiveData<List<Alert>> = cityDao.getAlerts()

    suspend fun insert(alert: Alert) {
        cityDao.alarmInsert(alert)
    }

    suspend fun delete(alert: Alert) {
        cityDao.deleteAlerts(alert)
    }
}