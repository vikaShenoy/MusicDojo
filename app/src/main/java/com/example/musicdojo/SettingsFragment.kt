package com.example.musicdojo

import android.os.Bundle
import android.text.InputType
import androidx.preference.DropDownPreference
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import com.example.musicdojo.util.MODES

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        // User can select number of questions used in the ear training game
        findPreference<EditTextPreference>("num_questions")?.let {
            it.setOnBindEditTextListener { editor ->
                editor.inputType = InputType.TYPE_CLASS_NUMBER
            }
        }

        // User can select the mode to play the game with
        findPreference<DropDownPreference>("selected_mode").let {
            it?.entries = MODES.keys.toTypedArray()
        }
    }
}