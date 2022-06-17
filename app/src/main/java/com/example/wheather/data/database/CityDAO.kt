package com.example.weatherapp.db

import android.provider.LiveFolders
import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.wheather.data.model.Alert
import com.example.wheather.data.model.City
import kotlinx.coroutines.flow.Flow


@Dao
interface CityDAO {
    @Query("SELECT * FROM cities_table ORDER BY city ASC")
    fun getAlphabetizedWords(): LiveData<List<City>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(city: City)

    @Delete
    suspend fun delete(city: City)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun alarmInsert(alert: Alert)

    @Query("SELECT * FROM alerts_table")
    fun getAlerts(): LiveData<List<Alert>>

    @Delete
    suspend fun deleteAlerts(alert : Alert)
}