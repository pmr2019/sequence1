package com.example.sujet_sequence_1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        //noinspection deprecation
        //addPreferencesFromResource(R.xml.settings); // traces de l'ancienne implémentation des settings
    }

}
