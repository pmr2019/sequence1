package com.example.todo;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.EditTextPreference;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, TextView.OnEditorActionListener {
    private static final String TAG = "MainActivity";

    //Widgets
    private EditText edtPseudo;
    private Button btnOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Find widgets
        edtPseudo = findViewById(R.id.edtPseudo);
        btnOk = findViewById(R.id.btnOk);

        //Attach listener
        edtPseudo.setOnEditorActionListener(this);
        btnOk.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Recuperation du dernier pseudo
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        edtPseudo.setText(settings.getString("pseudo",""));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings: {
                Intent toSettings = new Intent(this, SettingsActivity.class);
                startActivity(toSettings);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public static String jsonToPrettyFormat(String jsonString) {
        JSONObject json = null;
        try {
            json = new JSONObject(jsonString);
            Gson gson = new GsonBuilder()
                    .serializeNulls()
                    .disableHtmlEscaping()
                    .setPrettyPrinting()
                    .create();

            return gson.toJson(json);
        } catch (JSONException e) {
            e.printStackTrace();
            return "chaine impossible à interpréter";
        }

    }

    @SuppressLint("ApplySharedPref")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnOk :
                Log.d(TAG, "onClick: clicked on : Ok.");

                //Add pseudo to preferences
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = settings.edit();
                String pseudo = edtPseudo.getText().toString();
                editor.putString("pseudo",pseudo);
                editor.commit();

                //Intent to ChoixListActivity
                Intent toSecondAct = new Intent(this,ChoixListActivity.class);
                Bundle data = new Bundle();
                data.putString("pseudo",pseudo);
                toSecondAct.putExtras(data);
                startActivity(toSecondAct);
                break;
        }
    }

    //Button validate on the on-screen keyboard = click on btnOk.
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            btnOk.performClick();
        }
        return false;
    }
}
