package com.example.wheather.data.retrofit

import WeatherResponse
import android.util.Log
import com.example.wheather.utils.ConstantsVals
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitService {

    var _lat: String
    var _lon: String
    var _exclude: String
    var _units: String
    var _lang: String

    @GET("onecall")
    suspend fun getAllWeatherData(
        @Query("lat", encoded = true) lat: String,
        @Query("lon", encoded = true) lon: String,
        @Query("exclude", encoded = true) exclude: String,
        @Query("units", encoded = true) units: String,
        @Query("lang", encoded = true) lang: String,
        @Query("appid", encoded = true) appid: String = ConstantsVals.API_key
    ): Response<WeatherResponse>

    companion object {
        var retrofitService: RetrofitService? = null
        fun getInstance(): RetrofitService {
            if (retrofitService == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://api.openweathermap.org/data/2.5/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                retrofitService = retrofit.create(RetrofitService::class.java)
                Log.i(ConstantsVals.log, "retrofit service is created")
            }
            return retrofitService!!
        }

    }
}