package com.example.wheather.favorite.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.wheather.R
import com.example.wheather.data.model.City
import com.example.wheather.utils.getCity

class CitiesAdapter (
    val deleteCity: (city: City) -> Unit,
    val showCityWeather: (city: City) -> Unit,
    var favCities: List<City>,
    val context : Context?) :
    RecyclerView.Adapter<CitiesAdapter.CitiesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CitiesViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.row_city, parent, false)
        return CitiesViewHolder(view)
    }

    override fun onBindViewHolder(holder: CitiesViewHolder, position: Int) {
        var city = favCities[position]
        holder.tv_city_name.text = getCity(city.lat, city.lon, context!!)
        holder.iv_deleteCity.setOnClickListener {
            deleteCity(city)
        }
        holder.cv_city.setOnClickListener {
            showCityWeather(city)
        }
    }

    override fun getItemCount(): Int = favCities.size

    class CitiesViewHolder(val itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv_city_name: TextView = itemView.findViewById(R.id.tv_city_name)
        val iv_deleteCity: ImageView = itemView.findViewById(R.id.iv_delet_city)
        val cv_city : CardView = itemView.findViewById(R.id.cv_city)
    }
}