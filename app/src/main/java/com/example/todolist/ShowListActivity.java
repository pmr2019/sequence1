package com.example.todolist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ShowListActivity extends AppCompatActivity {

    // le fichier myJsonStringListeTodo correspond au fichier Json associé à la liste des profils "lesListTodo"
    private String myJsonString;
    private List<ProfilListeToDo> listUsers;
    private String pseudo;
    private int positionUser;
    private int positionSelectionnee;

    private RecyclerView mRecyclerView;
    private PseudoItemTask mAdapter;
    private EditText edtAjouterTask;
    private RecyclerView.LayoutManager mLayoutManager;
    private Button btnAjouterTask;
    private SharedPreferences mSettings;
    private SharedPreferences.Editor mEditor;
    GsonBuilder builder = new GsonBuilder();
    Gson gson = builder.create();
    private Bundle data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_list);
        btnAjouterTask = findViewById(R.id.btnAjouterTask);
        edtAjouterTask = findViewById(R.id.textAjouterTask);

        data = this.getIntent().getExtras();
        positionSelectionnee = data.getInt("positionSelectionnee");
        positionUser = data.getInt("positionUser");
        pseudo = data.getString("pseudo","");


        mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        myJsonString = mSettings.getString("myJsonString","");

        //on récupère la liste des Users
        listUsers = gson.fromJson(myJsonString, new TypeToken<List<ProfilListeToDo>>(){}.getType());

        // on mets en place le recyclerView spécial pour les Todoliste (sans checkbox)
        mRecyclerView =  findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(this);

        // on ajoute a l'adapter les todolist propre au pseudo précédemment séléctionné

        mAdapter = new PseudoItemTask(listUsers.get(positionUser).getMesListToDo().get(positionSelectionnee).getLesItems());

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        btnAjouterTask.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(v.getId()==R.id.btnAjouterTask){
                    String description = edtAjouterTask.getText().toString();
                    //ajouter l'item a la liste des items associées a la todoo associés au pseudo en cours
                    ItemToDo itemToDo  = new ItemToDo(description);
                    listUsers.get(positionUser).getMesListToDo().get(positionSelectionnee).getLesItems().add(itemToDo);
                    edtAjouterTask.setText("");
                    myJsonString = gson.toJson(listUsers);
                    majJson();
                    majAffichage();
                }
            }
        });
        mAdapter.setOnClickListener(new PseudoItemTask.OnItemClickListener() {
            @Override
            public void onItemClick(int position, List<ItemToDo> mList) {}

            @Override
            public void onCheckBoxClick(int position) {
                /*
                Log.i("checked","checked");
                if (listUsers.get(positionUser).getMesListToDo().get(positionSelectionnee).getLesItems().get(position).getFait()==false) {
                    listUsers.get(positionUser).getMesListToDo().get(positionSelectionnee).getLesItems().get(position).setFait(true);
                }
                else {
                    listUsers.get(positionUser).getMesListToDo().get(positionSelectionnee).getLesItems().get(position).setFait(false);
                }
                */
            }
        });


    }



    private void majAffichage() {
        //On récupère les infos sauvegardées dans les preferences
        mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        ////myJsonStringListeTodo = mSettings.getString("myJsonStringListeTodo","");
        // on récupère la liste des todolist
        listUsers = gson.fromJson(myJsonString, new TypeToken<List<ProfilListeToDo>>(){}.getType());

        // on mets en place le recyclerView spécial pour les Todoliste (sans checkbox)
        mRecyclerView =  findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        // on ajoute a l'adapter les todolist propre au pseudo précédemment séléctionné
        mAdapter = new PseudoItemTask(listUsers.get(positionUser).getMesListToDo().get(positionSelectionnee).getLesItems());

    }

    private void majJson() {
        mEditor = mSettings.edit();
        mEditor.clear();
        mEditor.putString("myJsonString", myJsonString);
        mEditor.commit();
        mAdapter = new PseudoItemTask(listUsers.get(positionUser).getMesListToDo().get(positionSelectionnee).getLesItems());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

}
