package com.example.wheather

import android.app.Application
import com.example.weatherapp.db.AppRoomDatabase
import com.example.wheather.data.repository.Repository
import com.example.wheather.data.retrofit.RetrofitService

class MyApplication : Application() {
    private val database by lazy { AppRoomDatabase.getDatabase(this) }
    private val retrofit by lazy { RetrofitService.getInstance() }
    val repository by lazy {Repository(database.dao(), retrofit, this)}
}