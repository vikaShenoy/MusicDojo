package com.example.musicdojo

import android.os.Bundle
import android.text.InputType
import androidx.preference.DropDownPreference
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        findPreference<EditTextPreference>("num_questions")?.let {
            it.setOnBindEditTextListener {editor ->
                editor.inputType = InputType.TYPE_CLASS_NUMBER
            }
        }

//        findPreference<DropDownPreference>("selected_mode").let {
//            it?.entries = MODES.keys.toTypedArray()
//        }
    }
}