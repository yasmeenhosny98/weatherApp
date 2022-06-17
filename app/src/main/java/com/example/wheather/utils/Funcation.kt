package com.example.wheather.utils

import android.content.Context
import android.location.Geocoder
import android.net.ConnectivityManager
import java.text.SimpleDateFormat
import java.util.*

fun convertLongToStringDate(date: Long , pattern: String): String {
    var time = Date(date * 1000)
    val pattern = SimpleDateFormat(pattern, Locale.getDefault())
    return pattern.format(time)
}

fun convertLongToStringTime(time: Long, pattern: String): String {
    var time = Date(time * 1000)
    val pattern = SimpleDateFormat(pattern)
    return pattern.format(time)
}

fun getCity(lat: String, lon: String, context: Context): String {
    var myAddress = ""
    val geocoder = Geocoder(context, Locale.getDefault())
    val addressList = geocoder.getFromLocation(lat.toDouble(), lon.toDouble(), 1)
    if (addressList.size > 0) {
        val address = addressList[0]
        myAddress = "${address.subAdminArea}, ${address.countryName}"
    }
   return myAddress
}

fun isConnectedToInternet(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    val activeNetworkInfo = connectivityManager!!.activeNetworkInfo
    return activeNetworkInfo != null && activeNetworkInfo.isConnected
}