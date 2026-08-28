package com.lz.telefonoprolz.ui.settings

import android.os.Bundle
import android.widget.Toast
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.lz.telefonoprolz.R
import com.lz.telefonoprolz.call.DefaultDialerHelper
import com.lz.telefonoprolz.call.PowerButtonEndCallHelper

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        findPreference<Preference>("pref_default_dialer")?.setOnPreferenceClickListener {
            if (DefaultDialerHelper.isDefaultDialer(requireContext())) {
                Toast.makeText(requireContext(), R.string.pref_default_dialer_summary, Toast.LENGTH_SHORT).show()
            } else {
                DefaultDialerHelper.requestDefaultDialer(requireActivity())
            }
            true
        }

        findPreference<SwitchPreferenceCompat>("pref_high_contrast")?.setOnPreferenceChangeListener { _, _ ->
            requireActivity().recreate()
            true
        }

        findPreference<SwitchPreferenceCompat>("pref_power_end_call")?.setOnPreferenceChangeListener { pref, newValue ->
            val enabled = newValue as Boolean
            val success = PowerButtonEndCallHelper.setEndCallOnPower(requireContext(), enabled)
            if (!success) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.pref_power_end_summary),
                    Toast.LENGTH_LONG
                ).show()
            }
            success
        }
    }
}
