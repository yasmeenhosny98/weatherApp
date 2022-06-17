package com.example.wheather.map.view

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.wheather.MyApplication
import com.example.wheather.R
import com.example.wheather.data.model.City
import com.example.wheather.map.viewmodel.MapViewModel
import com.example.wheather.map.viewmodel.MapViewModelFactory
import com.example.wheather.utils.ConstantsVals
import com.example.wheather.utils.LocationProvider
import com.example.wheather.utils.getCity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : Fragment() {

    lateinit var mapFragment: SupportMapFragment
    lateinit var googleMap: GoogleMap
    lateinit var locationProvider: LocationProvider
    lateinit var btn_add_location: Button
    lateinit var location: LatLng
    var favFlag: Boolean = false

    //view model vars
    lateinit var viewModelFactory: MapViewModelFactory
    lateinit var viewModel: MapViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_map, container, false)
        setupViewModel()
        locationProvider = LocationProvider(requireActivity())
        mapFragment =
            childFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment
        mapFragment.getMapAsync {
            googleMap = it
            if (locationProvider.checkLocationPermissions()) {
                enableMyLocation()
            } else {
                locationProvider.requestLocationPermissions()
            }
            var location = LatLng(30.3173, 31.7114)
            googleMap.addMarker(
                MarkerOptions().position(location).title(R.string.currentLocation.toString())
            )
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 10f))
            googleMap.setOnMapClickListener {
                googleMap.clear()
                googleMap.addMarker(
                    MarkerOptions().position(it).title("${it.latitude}, ${it.longitude}")
                )
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(it, 10f))
                this.location = it
            }
        }
        favFlag = requireArguments().getBoolean("favFlag")
        Log.i(ConstantsVals.log, "onCreateView: $favFlag")
        return view
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ConstantsVals.LOCATION_PERMISSION_ID) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation()
            } else {
                Toast.makeText(
                    requireActivity(),
                    "we need your permission to be able get the weather data based on your current location",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setupViewModel() {
        viewModelFactory =
            MapViewModelFactory((requireActivity().application!! as MyApplication).repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MapViewModel::class.java)
    }

    @SuppressLint("MissingPermission")
    fun enableMyLocation() {
        googleMap.isMyLocationEnabled = true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_add_location = view.findViewById(R.id.btn_add_location)
        btn_add_location.setOnClickListener {
            if (favFlag) {
                var cityName =
                    getCity("${location.latitude}", "${location.longitude}", requireContext())
                var city = City(
                    System.currentTimeMillis(),
                    cityName,
                    "${location.latitude}",
                    "${location.longitude}"
                )
                viewModel.insert(city)
                Navigation.findNavController(view)
                    .navigate(R.id.action_fragment_map_to_fragment_favorite)
                Log.i(
                    ConstantsVals.log,
                    "onViewCreated:>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ${location.latitude}, ${location.longitude}"
                )
            } else {
                Navigation.findNavController(view)
                    .navigate(R.id.action_fragment_map_to_fragment_home, Bundle().apply {
                        putString("lat", "${location.latitude}")
                        putString("lon", "${location.longitude}")
                        putBoolean("mapFlag", true)
                    })
                Log.i(
                    ConstantsVals.log,
                    "onViewCreated: ${location.latitude}, ${location.longitude}"
                )
            }
        }
    }
}
