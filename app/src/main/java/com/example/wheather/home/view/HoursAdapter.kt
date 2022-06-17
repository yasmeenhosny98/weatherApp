package com.example.wheather.home.view

import Hourly
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wheather.R
import com.example.wheather.utils.convertLongToStringTime

class HoursAdapter(var hoursData: List<Hourly>, val context : Context?) :
    RecyclerView.Adapter<HoursAdapter.HourViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.row_hour, parent, false)
        return HourViewHolder(view)
    }

    override fun onBindViewHolder(holder: HourViewHolder, position: Int) {
        var hour = hoursData[position]
        holder.tv_time.text = convertLongToStringTime(hour.dt, "HH:mm aa")
        holder.tv_temp.text = "${hour.temp}ْْْْْْْْْْْْ"
        Glide.with(context!!)
            .load("https://openweathermap.org/img/wn/${hour.weather[0].icon}.png")
            .into(holder.iv_icon)
    }

    override fun getItemCount(): Int = hoursData.size

    class HourViewHolder(val itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv_time: TextView = itemView.findViewById(R.id.tv_time)
        val tv_temp: TextView = itemView.findViewById(R.id.tv_temp)
        val iv_icon: ImageView = itemView.findViewById(R.id.iv_icon)
    }
}