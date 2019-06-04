package com.example.todo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChoixListActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ChoixListActivity";

    //Widgets
    private EditText edtListTitle;
    private Button btnAddList;

    //Var
    private String pseudo;
    private ArrayList<ListeToDo> mList = new ArrayList<ListeToDo>(); //Liste des listes to do
    private ProfilListeToDo profil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choix_list);
        Log.d(TAG, "onCreate: started.");

        //Get pseudo active
        Bundle data = this.getIntent().getExtras();
        assert data != null;
        pseudo = data.getString("pseudo", "inconnu");
        Log.d(TAG, "pseudo : " + pseudo);

        //Get todo lists from the pseudo
        loadData();

        //Init widgets
        edtListTitle = findViewById(R.id.edtListTitle);
        btnAddList = findViewById(R.id.btnAddList);

        //Add listener
        btnAddList.setOnClickListener(this);

        //Init reclyclerView
        initRecyclerView();
        refreshRecyclerView();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAddList:
                Log.d(TAG, "onClick: clicked on : Ok.");
                String title = edtListTitle.getText().toString();
                if (!title.isEmpty()) {
                    addListeToDo(title);
                }
        }
    }

    private void initRecyclerView() {
        Log.d(TAG, "initRecyclerView: init RecyclerView");
        RecyclerView recyclerView = findViewById(R.id.reclycler_view_list);
        RecyclerViewAdapterList adapter = new RecyclerViewAdapterList(this, mList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void refreshRecyclerView() {
        mList.clear();
        mList.addAll(profil.getMesListeToDo());
        RecyclerView recyclerView = findViewById(R.id.reclycler_view_list);
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    private void addListeToDo(String title) {
        profil.ajouteListe(new ListeToDo(title));
        saveData();
        Log.d(TAG, "addListeToDo: " + profil.toString());
        refreshRecyclerView();
    }

    private void loadData() {
        Log.d(TAG, "loadData: loading.");

        Gson gson = new GsonBuilder()
                .serializeNulls()
                .disableHtmlEscaping()
                .setPrettyPrinting()
                .create();

        String filename = pseudo + ".json";
        String fileContents = "";
        FileReader fileReader;
        try {
            fileReader = new FileReader(getFilesDir() + "/" + filename);
            BufferedReader reader = new BufferedReader(fileReader);
            String line;
            while ((line = reader.readLine()) != null) {
                fileContents += line;
            }
            reader.close();
            fileReader.close();
        } catch (FileNotFoundException e) {
            Log.i(TAG, "loadData: user unknown, creating new user.");
            profil = new ProfilListeToDo();
            profil.setLogin(pseudo);
        } catch (Exception e) {
            Log.e(TAG, "loadData: error while reading save file : ", e);
            ;
        }
        profil = gson.fromJson(fileContents, ProfilListeToDo.class);
        if (profil == null) {
            Log.i(TAG, "loadData: No data from this profil. Creating new one.");
            profil = new ProfilListeToDo();
            profil.setLogin(pseudo);
        }
        Log.d(TAG, "loadData: profil charge : " + profil.toString());
        saveData();
    }

    private void saveData() {
        Log.d(TAG, "saveData: saving.");

        Gson gson = new GsonBuilder()
                .serializeNulls()
                .disableHtmlEscaping()
                .setPrettyPrinting()
                .create();

        String filename = pseudo + ".json";
        Log.d(TAG, "saveData: " + filename);
        String fileContents = gson.toJson(profil);
        FileWriter fileWriter;
        try {
            fileWriter = new FileWriter(getFilesDir() + "/" + filename);
            fileWriter.write(fileContents);
            fileWriter.close();
        } catch (Exception e) {
            Log.e(TAG, "saveData: error while writing save file : ", e);
        }
    }

}