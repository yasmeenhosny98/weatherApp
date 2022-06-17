package com.example.wheather.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Looper
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import kotlinx.coroutines.runBlocking
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

class LocationProvider(var activity: Activity) {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest

    var longitude: Double = 0.0
    var latitude: Double = 0.0

    fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            ConstantsVals.LOCATION_PERMISSION_ID
        )
    }

    fun checkLocationPermissions(): Boolean {
        return (ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED)
    }
// TODO: done
    /*fun initializeLocationRequest() {
        locationRequest = LocationRequest.create().apply {
            interval = TimeUnit.MILLISECONDS.toMillis(1000)
            fastestInterval = TimeUnit.MILLISECONDS.toMillis(500)
            maxWaitTime = TimeUnit.MILLISECONDS.toMillis(100)
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }*/
//TODO:done
    /*  fun initializeLocationCallBack() {
          locationCallback = object : LocationCallback() {
              override fun onLocationResult(locationResult: LocationResult) {
                  super.onLocationResult(locationResult)
                  longitude = "${locationResult.lastLocation.longitude}"
                  latitude = "${locationResult.lastLocation.latitude}"
              }
          }
      }*/

    @SuppressLint("MissingPermission")
    fun getLastLocation() {
        if (checkLocationPermissions()) {
            if (true/*isLocationEnabled()*/) {
                var mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
                mFusedLocationClient.lastLocation.addOnCompleteListener(activity) { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        //getUpdatedLocation()
                    } else {
                        longitude = location?.latitude
                        latitude = location?.longitude
                        //todo add shared preferences here

                        Log.i(ConstantsVals.log, "get updated location: $longitude , $latitude")
                    }
                }
            } else {
                // locationNotEnable()
            }
        } else {
            // requestPermissions()
        }
    }




}


