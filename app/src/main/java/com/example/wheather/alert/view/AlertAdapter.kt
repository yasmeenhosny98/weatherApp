package com.example.wheather.alert.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wheather.R
import com.example.wheather.data.model.Alert
import com.example.wheather.favorite.view.CitiesAdapter
import com.example.wheather.utils.convertLongToStringDate
import com.example.wheather.utils.getCity

class AlertAdapter (
    val deleteCity: (alert: Alert) -> Unit,
    var alerts: List<Alert>,
    val context : Context?) :
    RecyclerView.Adapter<AlertAdapter.AlertViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.row_alert, parent, false)
        return AlertViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlertViewHolder, position: Int) {
        var alert = alerts[position]
        holder.tv_to.text = convertLongToStringDate(alert.endDate, "EEE, MMM dd")
        holder.tv_from.text = convertLongToStringDate(alert.startDate, "EEE, MMM dd")
        holder.tv_event.text = alert.event
        holder.iv_delete_alert.setOnClickListener {
            deleteCity(alert)
        }
    }

    override fun getItemCount(): Int = alerts.size

    class AlertViewHolder(val itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv_to: TextView = itemView.findViewById(R.id.tv_to)
        val tv_from: TextView = itemView.findViewById(R.id.tv_from)
        val tv_event: TextView = itemView.findViewById(R.id.tv_event)
        val iv_delete_alert: ImageView = itemView.findViewById(R.id.iv_delet_alert)
    }
}