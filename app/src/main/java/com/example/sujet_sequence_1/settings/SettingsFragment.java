package com.example.sujet_sequence_1.settings;


import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.example.sujet_sequence_1.R;

/**
 * Fragment pour la gestion des préférences.
 */
public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        setPreferencesFromResource(R.xml.settings, s);
    }
}
