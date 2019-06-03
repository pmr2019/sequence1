package com.example.todolist;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.List;


public class SettingsActivity extends AppCompatActivity {

    private String myJsonString;
    private int positionUser;
    private List<ProfilListeToDo> listUsers;
    private RecyclerView mRecyclerView;
    private PseudoItemSettings mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private SharedPreferences mSettings;
    private SharedPreferences.Editor mEditor;
    GsonBuilder builder = new GsonBuilder();
    Gson gson = builder.create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        myJsonString = mSettings.getString("myJsonString","");

        // on récupère la liste des Users
        listUsers = gson.fromJson(myJsonString, new TypeToken<List<ProfilListeToDo>>(){}.getType());

        // on mets en place le recyclerView spécial pour les Todoliste (sans checkbox)
        mRecyclerView =  findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(this);

        // on ajoute a l'adapter les todolist propre au pseudo précédemment séléctionné
        mAdapter = new PseudoItemSettings(listUsers);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnClickListener(new PseudoItemSettings.OnItemClickListener() {
            @Override
            public void onItemClick(int position, List<ProfilListeToDo> mList) {
                positionUser = position;
                listUsers.remove(positionUser);
                myJsonString = gson.toJson(listUsers);
                majJson();
                /*
                Intent intent = getIntent();
                finish();
                startActivity(intent);
                */
            }
        });
/*
        btnSuppr.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(v.getId()==R.id.btnSuppr){
                    //on supprime
                    listUsers.remove(positionUser);
                    myJsonString = gson.toJson(listUsers);
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }
            }
        });

*/


    }

    private void majJson() {
        mEditor = mSettings.edit();
        mEditor.clear();
        mEditor.putString("myJsonString", myJsonString);
        mEditor.commit();
        mAdapter = new PseudoItemSettings(listUsers);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnClickListener(new PseudoItemSettings.OnItemClickListener() {
            @Override
            public void onItemClick(int position, List<ProfilListeToDo> mList) {
                positionUser = position;
                listUsers.remove(positionUser);
                myJsonString = gson.toJson(listUsers);
                majJson();
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });
    }
}