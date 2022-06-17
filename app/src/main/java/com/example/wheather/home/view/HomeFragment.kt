package com.example.wheather.home.view

import WeatherResponse
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wheather.MyApplication
import com.example.wheather.R
import com.example.wheather.data.repository.Repository
import com.example.wheather.data.retrofit.RetrofitService
import com.example.wheather.home.viewmodel.HomeViewModel
import com.example.wheather.home.viewmodel.HomeViewModelFactory
import com.example.wheather.utils.*
import com.google.android.gms.location.*

class HomeFragment : Fragment() {

    //view model vars
    lateinit var viewModelFactory: HomeViewModelFactory
    lateinit var viewModel: HomeViewModel
    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    lateinit var locationProvider: LocationProvider

    //hours & days recycler view var
    lateinit var rv_days: RecyclerView
    lateinit var rv_hours: RecyclerView
    lateinit var daysAdapter: DaysAdapter
    lateinit var hoursAdapter: HoursAdapter

    //XML elements
    lateinit var fragment: View
    lateinit var tv_address: TextView
    lateinit var tv_temp: TextView
    lateinit var tv_desc: TextView
    lateinit var tv_date: TextView
    lateinit var tv_humidity: TextView
    lateinit var tv_perssure: TextView
    lateinit var tv_wind_speed: TextView
    lateinit var iv_main_icon: ImageView
    lateinit var progressBar: ProgressBar

    //location vars
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    lateinit var longitude: String
    lateinit var latitude: String
    var readableAddress: String = ""
    var askForLocationFlag: Boolean = true
    var mapFlag: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragment = inflater.inflate(R.layout.fragment_home, container, false)
        setupViewModel()
        setupLocationProvider()
        mapFlag = requireArguments().getBoolean("mapFlag")
        if (mapFlag){
            latitude = requireArguments().getString("lat")!!
            longitude = requireArguments().getString("lon")!!
            Log.i(ConstantsVals.log, "onCreateView: $longitude, $latitude")
        }
        return fragment
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupXML(view)
        setupHoursRecyclerView()
        setupDaysRecyclerView()
        if (sharedPreferences.getBoolean(ConstantsVals.askForLocationFlag, false)){
            editor.putBoolean(ConstantsVals.askForLocationFlag, false)
            editor.commit()
            askForLocation()
        }
    }

    override fun onStart() {
        super.onStart()
        if(mapFlag){
            if(isConnectedToInternet(requireContext())){
                viewModel.getAllWeatherData("$latitude", "$longitude")
                readableAddress = getCity(latitude, longitude, requireContext())
            }
            else{
                Toast.makeText(requireContext(), "There is no internet, check your connection please", Toast.LENGTH_LONG)
            }
        } else {
            getLocationUsingGPS()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ConstantsVals.LOCATION_PERMISSION_ID) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getUpdatedLocation()
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
        viewModelFactory = HomeViewModelFactory((requireActivity().application!! as MyApplication).repository)
        this.viewModel = ViewModelProvider(this, viewModelFactory).get(HomeViewModel::class.java)
        viewModel.observableWeatherData().observe(viewLifecycleOwner, weatherData)
        viewModel.observableErrorMessage().observe(viewLifecycleOwner) {
            Log.i(ConstantsVals.log, "there is some error : ${it}")
        }
    }

    private fun setupHoursRecyclerView() {
        hoursAdapter = HoursAdapter(emptyList(), requireActivity().applicationContext)
        rv_hours.apply {
            adapter = hoursAdapter
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        }
    }

    private fun setupDaysRecyclerView() {
        daysAdapter = DaysAdapter(emptyList(), requireActivity().applicationContext)
        rv_days.apply {
            adapter = daysAdapter
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        }
    }

    private fun setupXML(view: View) {
        rv_hours = view.findViewById(R.id.rv_hourly)
        rv_days = view.findViewById(R.id.rv_daily)
        tv_temp = view.findViewById(R.id.tv_main_temp)
        tv_date = view.findViewById(R.id.tv_date)
        tv_desc = view.findViewById(R.id.tv_desc)
        tv_address = view.findViewById(R.id.tv_address)
        tv_humidity = view.findViewById(R.id.tv_humidity)
        tv_perssure = view.findViewById(R.id.tv_pressure)
        tv_wind_speed = view.findViewById(R.id.tv_wind_speed)
        iv_main_icon = view.findViewById(R.id.iv_main_icon)
        progressBar = view.findViewById(R.id.progressBar)
    }

    private val weatherData: (it: WeatherResponse) -> Unit = { it: WeatherResponse ->
        run {
            Log.i(ConstantsVals.log, "we are in weatherData")
            hoursAdapter.hoursData = it.hourly
            hoursAdapter.notifyDataSetChanged()
            daysAdapter.daysData = it.daily
            daysAdapter.notifyDataSetChanged()
            Glide.with(this)
                .load("https://openweathermap.org/img/wn/${it.current.weather[0].icon}.png")
                .into(iv_main_icon)
            tv_date.text = "${convertLongToStringDate(it.current.dt, "EEE, MMM dd")}"
            tv_desc.text = "${it.current.weather[0].description}"
            if (readableAddress.isNullOrEmpty()) {
                tv_address.text = it.timezone
            } else {
                tv_address.text = readableAddress
            }
            tv_wind_speed.text = "${it.current.wind_speed}"
            tv_perssure.text = "${it.current.pressure}"
            tv_humidity.text = "${it.current.humidity}"
            tv_temp.text = "${it.current.temp}"
        }
    }

    private fun setupLocationProvider() {
        locationProvider = LocationProvider(requireActivity())
        sharedPreferences =
            requireActivity().getSharedPreferences(
                ConstantsVals.SharedPreferencesFileName,
                AppCompatActivity.MODE_PRIVATE
            )
        editor = sharedPreferences.edit()
    }

    private fun askForLocation() {
        askForLocationFlag = false
        val dialogBuilder = AlertDialog.Builder(requireActivity())
        val inflater = this.layoutInflater
        val dialogView: View = inflater.inflate(R.layout.dailog_loction, null)
        val rb_gps: RadioButton = dialogView.findViewById(R.id.r_gps)
        val rb_map: RadioButton = dialogView.findViewById(R.id.r_maps)

        dialogBuilder.setView(dialogView)
        dialogBuilder.setIcon(R.drawable.ic_ask_for_location)
        dialogBuilder.setTitle("Enter your location")
        dialogBuilder.setPositiveButton("OK")
        { _, _ ->
            onRadioButtonClicked(rb_gps)
            onRadioButtonClicked((rb_map))
        }
        dialogBuilder.setNegativeButton("Cancel") { dialogInterface, i -> }
        val b = dialogBuilder.create()
        b.show()
    }

    private fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            val checked = view.isChecked
            when (view.getId()) {
                R.id.r_gps ->
                    if (checked) {
                        getLocationUsingGPS()
                    }
                R.id.r_maps ->
                    if (checked) {
                        Navigation.findNavController(fragment)
                            .navigate(R.id.action_fragment_home_to_fragment_map, Bundle().apply {
                                putBoolean("favFlag", false)
                            })
                        Log.i(ConstantsVals.log, "onRadioButtonClicked: you need a map fragment")
                    }
            }
        }
    }

    private fun getLocationUsingGPS() {
        if (locationProvider.checkLocationPermissions()) {
            progressBar.visibility = View.VISIBLE
            getUpdatedLocation()
            progressBar.visibility = View.GONE
        } else {
            locationProvider.requestLocationPermissions()
        }
    }

    @SuppressLint("MissingPermission")
    fun getUpdatedLocation() {
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 1

        //todo debug here for activity
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()!!
        )
        fusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
            val location: Location? = task.result
            if (location != null) {
                longitude = "${location?.latitude}"
                latitude = "${location?.longitude}"
                //todo add shared preferences here
                if(isConnectedToInternet(requireContext())){
                    viewModel.getAllWeatherData(latitude, longitude)
                    readableAddress = getCity(latitude, longitude, requireContext())
                }
                else{
                    Toast.makeText(requireContext(), "There is no internet, check your connection please", Toast.LENGTH_LONG)
                }

            }
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            longitude = "${locationResult.lastLocation.longitude}"
            latitude = "${locationResult.lastLocation.latitude}"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        editor.putBoolean(ConstantsVals.askForLocationFlag, true)
        editor.commit()
    }
}