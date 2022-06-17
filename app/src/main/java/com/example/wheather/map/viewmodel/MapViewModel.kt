package com.example.wheather.map.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wheather.data.model.City
import com.example.wheather.data.repository.Repository
import kotlinx.coroutines.launch

class MapViewModel (private val repository: Repository) : ViewModel() {

    fun insert(city: City) = viewModelScope.launch {
        repository.insert(city)
    }
}