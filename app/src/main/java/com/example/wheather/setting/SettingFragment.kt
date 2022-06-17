package com.example.wheather.setting

import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.example.wheather.R
import java.util.*

class SettingFragment : PreferenceFragmentCompat() {

    var language: ListPreference? = null
    lateinit var SP: SharedPreferences


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        SP = PreferenceManager.getDefaultSharedPreferences(requireContext())
        language = findPreference("language")
        language?.setOnPreferenceChangeListener { preference, newValue ->
            chooseLanguage(preference, newValue as String)
            true
        }
    }
    private fun setLocale(activity: Activity, languageCode: String?) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val resources = activity.resources
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }
    private fun chooseLanguage(preference: Preference?, newValue: String){
        val item: String = newValue
        if (preference!!.key.equals("language")) {
            when (item) {
                "Arabic" -> {
                    setLocale(context as Activity,"ar")
                    requireActivity().recreate()
                }
                "English" -> {
                    setLocale(context as Activity,"en")
                    requireActivity().recreate()
                }
            }
        }
    }
}