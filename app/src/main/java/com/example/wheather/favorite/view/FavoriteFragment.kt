package com.example.wheather.favorite.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wheather.MyApplication
import com.example.wheather.R
import com.example.wheather.data.model.City
import com.example.wheather.favorite.viewmodel.FavoriteViewModel
import com.example.wheather.favorite.viewmodel.FavoriteViewModelFactory
import com.example.wheather.home.view.HoursAdapter
import com.example.wheather.home.viewmodel.HomeViewModel
import com.example.wheather.home.viewmodel.HomeViewModelFactory
import com.example.wheather.utils.ConstantsVals
import com.google.android.material.floatingactionbutton.FloatingActionButton

class FavoriteFragment : Fragment() {

    //view model vars
    lateinit var viewModelFactory: FavoriteViewModelFactory
    lateinit var viewModel: FavoriteViewModel

    //XML vars
    lateinit var fab: FloatingActionButton
    lateinit var rv_cities : RecyclerView
    lateinit var citiesAdapter: CitiesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view =  inflater.inflate(R.layout.fragment_favorite, container, false)
        setupViewModel()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupFAB(view)
        setupCitiesRecyclerView(view)
        viewModel.allCities.observe(viewLifecycleOwner){
            citiesAdapter.favCities = it
            citiesAdapter.notifyDataSetChanged()
        }
    }

    private fun setupViewModel() {
        viewModelFactory =
            FavoriteViewModelFactory((requireActivity().application!! as MyApplication).repository)
        this.viewModel =
            ViewModelProvider(this, viewModelFactory).get(FavoriteViewModel::class.java)
    }

    private fun setupFAB(view : View){
        fab = view.findViewById(R.id.fab_add_Fav_location)
        fab.setOnClickListener {
            Navigation.findNavController(view)
                .navigate(R.id.action_fragment_favorite_to_fragment_map, Bundle().apply {
                    putBoolean("favFlag" , true)
                })
        }
    }

    private fun setupCitiesRecyclerView(view : View){
        rv_cities = view.findViewById(R.id.rv_cities)
        citiesAdapter = CitiesAdapter(deleteCity, itemClick, emptyList(), requireActivity().applicationContext)
        rv_cities.apply {
            adapter = citiesAdapter
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        }
    }

    private val itemClick:(it:City)->Unit = { it:City ->
        Log.i(ConstantsVals.log, "city is ${it.lon}, ${it.lat}: ")
        Navigation.findNavController(requireView()).navigate(R.id.action_fragment_favorite_to_cityWeatherDailog, Bundle().apply {
            putString("lat", it.lat)
            putString("lon", it.lon)
            putString("city", it.name)
        })
    }

    private val deleteCity:(it: City)->Unit = { it: City -> viewModel.delete(it) }
}