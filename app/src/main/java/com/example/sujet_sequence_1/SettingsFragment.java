package com.example.sujet_sequence_1;


import android.app.ActionBar;
import android.os.Bundle;
import androidx.preference.PreferenceFragmentCompat;


public class SettingsFragment extends PreferenceFragmentCompat {


    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        setPreferencesFromResource(R.xml.settings, s);
    }
}
