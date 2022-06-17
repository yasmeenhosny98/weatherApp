package com.example.wheather.alert.view

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.wheather.MyApplication
import com.example.wheather.R
import com.example.wheather.alert.viewmodel.AlertViewModel
import com.example.wheather.alert.viewmodel.AlertViewModelFactory
import com.example.wheather.favorite.viewmodel.FavoriteViewModel
import com.example.wheather.favorite.viewmodel.FavoriteViewModelFactory
import com.example.wheather.utils.ConstantsVals
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

class AddAlertDailog : DialogFragment() {

    //view model vars
    lateinit var viewModelFactory: AlertViewModelFactory
    lateinit var viewModel: AlertViewModel

    //XML var
    lateinit var btn_add_alert: Button
    lateinit var sp_events: Spinner
    lateinit var iv_calender_from: ImageView
    lateinit var iv_clender_to: ImageView

    //alert model var
    lateinit var event: String
    var startDate: Long = 0
    var endDate: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.dailog_add_alert, container, false)
        setupViewModel()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupXML(view)
        setupSpinner()
        setupCalenders()
        setupAddButton()
    }

    private fun setupViewModel() {
        viewModelFactory =
            AlertViewModelFactory((requireActivity().application!! as MyApplication).repository)
        this.viewModel =
            ViewModelProvider(this, viewModelFactory).get(AlertViewModel::class.java)
    }

    private fun setupXML(view: View) {
        btn_add_alert = view.findViewById(R.id.btn_add_alert)
        sp_events = view.findViewById(R.id.sp_events)
        iv_calender_from = view.findViewById(R.id.iv_calender_from)
        iv_clender_to = view.findViewById(R.id.iv_calender_to)
    }

    private fun setupSpinner() {
        val events = resources.getStringArray(R.array.events)
        val myAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, events)
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sp_events.setAdapter(myAdapter)
        sp_events.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?, view: View, i: Int, l: Long
            ) {
                event = events[i]
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        })
    }

    private fun showCalender(callback: (Long) -> Unit) {
        Calendar.getInstance().apply {
            this.set(Calendar.SECOND, 0)
            this.set(Calendar.MILLISECOND, 0)
            DatePickerDialog(
                requireContext(),
                0,
                { _, year, month, day ->
                    this.set(Calendar.YEAR, year)
                    this.set(Calendar.MONTH, month)
                    this.set(Calendar.DAY_OF_MONTH, day)
                },
                this.get(Calendar.YEAR),
                this.get(Calendar.MONTH),
                this.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun setupCalenders(){
        iv_calender_from.setOnClickListener {
            showCalender {
                startDate = it
                Log.i(ConstantsVals.log, "setupCalenders: ${startDate}")
            }
        }
        iv_clender_to.setOnClickListener {
            showCalender {
                endDate = it
                Log.i(ConstantsVals.log, "setupCalenders: ${endDate}")
            }
        }
    }

    private fun setupAddButton(){
        btn_add_alert.setOnClickListener {
            if (startDate == 0L || endDate == 0L || event.equals("")) {
                Toast.makeText(context, "Date is required :(", Toast.LENGTH_LONG).show()
            } else {
                viewModel.addAlertToDB(startDate, endDate, event, 0)
            }
        }
    }
}