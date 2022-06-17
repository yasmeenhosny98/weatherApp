package com.example.wheather.home.view

import Daily
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wheather.R
import com.example.wheather.utils.convertLongToStringDate

class DaysAdapter (var daysData: List<Daily>, val context : Context?) :
    RecyclerView.Adapter<DaysAdapter.DaysViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DaysAdapter.DaysViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.row_day, parent, false)
        return DaysViewHolder(view)
    }

    override fun onBindViewHolder(holder: DaysAdapter.DaysViewHolder, position: Int) {
        var day = daysData[position]
        holder.tv_day.text = convertLongToStringDate(day.dt, "EEE, MMM dd")
        holder.tv_daily_temp.text = "${day.temp.max}, ${day.temp.min}"
        holder.tv_daily_desc.text = day.weather[0].description
        Glide.with(context!!)
            .load("https://openweathermap.org/img/wn/${day.weather[0].icon}.png")
            .into(holder.iv_daily_icon)
    }

    override fun getItemCount(): Int = daysData.size

    class DaysViewHolder( val itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv_day : TextView = itemView.findViewById(R.id.tv_day)
        val tv_daily_temp : TextView = itemView.findViewById(R.id.tv_daily_temp)
        val tv_daily_desc : TextView = itemView.findViewById(R.id.tv_daily_desc)
        val iv_daily_icon : ImageView = itemView.findViewById(R.id.iv_daily_icon)
    }
}