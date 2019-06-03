package com.example.todolist;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;

public class ChoixListActivity extends AppCompatActivity implements View.OnClickListener, MyAdapter.ActionListener {

    private static String CAT = "ChoixListActivity";
    private String pseudo = null;
    private ProfilListeToDo profilListeToDo = null;
    private TextView edtCreerListe = null;
    private Button btnCreerListe = null;
    private RecyclerView recyclerView = null;


    // Fonction alerter()
    private void alerter(String s){
        Toast toastAlert = Toast.makeText(this, s, Toast.LENGTH_SHORT);
        toastAlert.show();
        Log.i(CAT, s);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choix_list);
//        alerter("onCreate: choixlist");

        // Récupération des views du layout
        edtCreerListe = findViewById(R.id.edtNewListe);
        btnCreerListe = findViewById(R.id.btnNewList);

        // ################# Gestionnaire de clic ########################
        btnCreerListe.setOnClickListener(this);
        edtCreerListe.setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();

        // Récupération du bundle transmis par MainActivity: PSEUDO
        Bundle data = this.getIntent().getExtras();
        pseudo = data.getString("pseudo", "");
        pseudo = pseudo.toLowerCase();
//        alerter(pseudo + " reçu 5/5");

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String jsonProfilListe = settings.getString(pseudo, "null");
//        alerter(jsonProfilListe);
        if (jsonProfilListe == "null") {
            profilListeToDo = new ProfilListeToDo(pseudo, new ArrayList<ListeToDo>());
            sauvegardeProfilListe(profilListeToDo, pseudo);
            alerter("création profilListe");
        }
        else {
            profilListeToDo =  deserialisation(jsonProfilListe);
            alerter("désérialisation");
        }

        // ############### RecyclerView #################################
        recyclerView = findViewById(R.id.myRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager((this)));
        recyclerView.setAdapter(new MyAdapter(profilListeToDo, this));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    // Gestionnaire d'événement menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.action_account:
                alerter("Menu Compte");
                break;
            case R.id.action_settings:
                alerter("Menu Préférences");
                Intent toSettings = new Intent(this,SettingsActivity.class);
                startActivity(toSettings);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.btnNewList:
                // Ajout d'une nouvelle liste
                ListeToDo listeToDo = new ListeToDo();
                listeToDo.setTitreListeToDo(edtCreerListe.getText().toString());
//                alerter("ajout: " + profilListeToDo);

                boolean ok = profilListeToDo.ajouterListeToDo(listeToDo);
                alerter("ajout: " + ok);
                sauvegardeProfilListe(profilListeToDo, pseudo);
                edtCreerListe.setText("");
                recyclerView.setAdapter(new MyAdapter(profilListeToDo, this));

        }
    }


    // ############### Sérialisation/ Désérialisation ###################################
    public ProfilListeToDo deserialisation(String jsonProfilListe){
        Gson gson = new Gson();
        ProfilListeToDo profilListeToDo = gson.fromJson(jsonProfilListe,ProfilListeToDo.class);
        return profilListeToDo;
    }


    public String serialisation(ProfilListeToDo profilListeToDo){
        Gson gson = new Gson();
        return gson.toJson(profilListeToDo);
    }

    // ############## Gestion des préférences partagées ################################
    public ProfilListeToDo recupProfilListe(String pseudo){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String jsonProfilListe = settings.getString(pseudo, "");
        return deserialisation(jsonProfilListe);
    }

    public void sauvegardeProfilListe(ProfilListeToDo newProfilListeToDo, String pseudo){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String jsonProfilListe = serialisation(newProfilListeToDo);
        SharedPreferences.Editor editor = settings.edit();
//        editor.clear();
        editor.putString(pseudo, jsonProfilListe);
        editor.commit();
    }

    @Override
    public void onItemClicked(int posListe) {
//        alerter("Activité : click sur : " + posListe);
        // on a la position de la liste cliquée
        // on lance l'activité ShowListActivity en lui passant la position

/*        ListeToDo liste = profilListeToDo.getMesListeToDo().get(posListe);
        ItemToDo item1 = new ItemToDo("item 1");
        ItemToDo item2 = new ItemToDo();
        liste.ajouterItem(item1);
        liste.ajouterItem(item2);
        sauvegardeProfilListe(profilListeToDo,pseudo);*/


        Intent toChoixListActivity = new Intent(ChoixListActivity.this, ShowListActivity.class);
        Bundle myBundle = new Bundle();
        myBundle.putInt("posListe", posListe);
        myBundle.putString("pseudo", pseudo);
        toChoixListActivity.putExtras(myBundle);
        startActivity(toChoixListActivity);
    }
}
