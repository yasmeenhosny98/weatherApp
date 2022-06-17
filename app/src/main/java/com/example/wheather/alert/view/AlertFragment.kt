package com.example.wheather.alert.view

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
import com.example.wheather.alert.viewmodel.AlertViewModel
import com.example.wheather.alert.viewmodel.AlertViewModelFactory
import com.example.wheather.data.model.Alert
import com.example.wheather.data.model.City
import com.example.wheather.favorite.view.CitiesAdapter
import com.example.wheather.favorite.viewmodel.FavoriteViewModel
import com.example.wheather.favorite.viewmodel.FavoriteViewModelFactory
import com.example.wheather.utils.ConstantsVals
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AlertFragment : Fragment() {

    //view model vars
    lateinit var viewModelFactory: AlertViewModelFactory
    lateinit var viewModel: AlertViewModel

    //XML vars
    lateinit var fab: FloatingActionButton
    lateinit var rv_alerts: RecyclerView
    lateinit var alertAdapter: AlertAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_alert, container, false)
        setupViewModel()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupFAB(view)
        setupAlertsRecyclerView(view)
        viewModel.getAlert.observe(viewLifecycleOwner){
            alertAdapter.alerts = it
            alertAdapter.notifyDataSetChanged()
        }
    }

    private fun setupViewModel() {
        viewModelFactory =
            AlertViewModelFactory((requireActivity().application!! as MyApplication).repository)
        this.viewModel =
            ViewModelProvider(this, viewModelFactory).get(AlertViewModel::class.java)
    }

    private fun setupFAB(view: View) {
        fab = view.findViewById(R.id.fab_add_alert)
        fab.setOnClickListener {
            Log.i(ConstantsVals.log, "setupFAB: add alert")
            AddAlertDailog().show(requireFragmentManager(), ConstantsVals.add_alert_tag)
        }
    }

    private fun setupAlertsRecyclerView(view : View){
        rv_alerts = view.findViewById(R.id.rv_alerts)
        alertAdapter = AlertAdapter(deleteAlert, emptyList(), requireActivity().applicationContext)
        rv_alerts.apply {
            adapter = alertAdapter
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        }
    }

    private val deleteAlert:(it: Alert)->Unit = { it: Alert -> viewModel.delete(it) }
}