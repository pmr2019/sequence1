package com.example.todolist;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.util.List;


public class ChoixListActivity extends AppCompatActivity{

    // le fichier myJsonStringListeTodo correspond au fichier Json associé à la liste des profils "lesListTodo"
    private String myJsonString;
    private List<ProfilListeToDo> listUsers;
    private String pseudo;
    private int positionUser;

    private RecyclerView mRecyclerView;
    private PseudoItem mAdapter;
    private EditText edtAjouterTodo;
    private RecyclerView.LayoutManager mLayoutManager;
    private Button btnAjouterTodo;
    private SharedPreferences mSettings;
    private SharedPreferences.Editor mEditor;
    GsonBuilder builder = new GsonBuilder();
    Gson gson = builder.create();
    public Bundle data;


    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choix_list);
        btnAjouterTodo = findViewById(R.id.btnAjouterTodo);
        edtAjouterTodo = findViewById(R.id.textAjouterTodo);


        data = this.getIntent().getExtras();
        pseudo = data.getString("pseudo","");
        positionUser = data.getInt("positionUser");

        mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        myJsonString = mSettings.getString("myJsonString","");

        // on récupère la liste des Users
        listUsers = gson.fromJson(myJsonString, new TypeToken<List<ProfilListeToDo>>(){}.getType());

        // on mets en place le recyclerView spécial pour les Todoliste (sans checkbox)
        mRecyclerView =  findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(this);

        // on ajoute a l'adapter les todolist propre au pseudo précédemment séléctionné
        mAdapter = new PseudoItem(listUsers.get(positionUser).getMesListToDo());

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnClickListener(new PseudoItem.OnItemClickListener() {
            @Override
            public void onItemClick(int position, List<ListeToDo> mList) {
                Intent toShowListActivity = new Intent(ChoixListActivity.this,ShowListActivity.class);
                //on transmet la position de la TodoList sélectionnée
                data.putInt("positionSelectionnee",position);
                toShowListActivity.putExtras(data);
                startActivity(toShowListActivity);
            }
        });

        btnAjouterTodo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(v.getId()==R.id.btnAjouterTodo){
                    String titreListeTodo = edtAjouterTodo.getText().toString();
                    //ajouter la todolist a la liste des todos associées au pseudo en cours
                    ListeToDo listTodo  = new ListeToDo(titreListeTodo);
                    listUsers.get(positionUser).getMesListToDo().add(listTodo);
                    edtAjouterTodo.setText("");
                    myJsonString = gson.toJson(listUsers);
                    majJson();
                    //On relance l'activité pour pouvoir acceder à la nouvelle todolist qui vient d'êter créer...
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);

                }
            }
        });
     }


    private void majJson() {
        mEditor = mSettings.edit();
        mEditor.clear();
        mEditor.putString("myJsonString", myJsonString);
        mEditor.commit();
        mAdapter = new PseudoItem(listUsers.get(positionUser).getMesListToDo());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnClickListener(new PseudoItem.OnItemClickListener() {
            @Override
            public void onItemClick(int position, List<ListeToDo> mList) {
                Intent toShowListActivity = new Intent(ChoixListActivity.this,ShowListActivity.class);
                startActivity(toShowListActivity);
                ;
            }
        });
    }


}
