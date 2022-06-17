package com.example.wheather.alert.viewmodel

import android.content.Context
import android.provider.ContactsContract
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wheather.data.model.Alert
import com.example.wheather.data.repository.Repository
import kotlinx.coroutines.launch

class AlertViewModel (private val repository: Repository) : ViewModel() {
    val getAlert: LiveData<List<Alert>> = repository.getAlerts
    fun delete(alert: Alert) = viewModelScope.launch {
        repository.delete(alert)
    }
    fun addAlertToDB(Start_date: Long, endDate: Long, event: String, requestCode: Int){
        viewModelScope.launch {
            repository.insert(Alert(Start_date, endDate, event, requestCode))
        }
    }
}