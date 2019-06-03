package com.example.todolist;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

public class ShowListActivity extends AppCompatActivity implements View.OnClickListener, MyAdapterShowList.ActionListener {

    private ProfilListeToDo profilListeToDo;
    private int posListe;
    private ListeToDo listeTodo;
    private String pseudo;
    private Button btnCreerItem;
    private TextView edtCreerItem;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_list);

        // Récupération des views du layout
        edtCreerItem = findViewById(R.id.edtNewItem);
        btnCreerItem = findViewById(R.id.btnNewItem);

        // ################# Gestionnaire de clic ########################
        btnCreerItem.setOnClickListener(this);
        edtCreerItem.setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();

        // Récupération des infos de l'activité ChoixListeActivity
        Bundle myBundle = this.getIntent().getExtras();
        posListe = myBundle.getInt("posListe");
        pseudo = myBundle.getString("pseudo");

        // Récupération du profilListe dans les sharedpreferences
        profilListeToDo = recupProfilListe(pseudo);
        listeTodo = profilListeToDo.getMesListeToDo().get(posListe);


        // ############### RecyclerView #################################
        recyclerView = findViewById(R.id.myRecyclerViewItem);
        recyclerView.setLayoutManager(new LinearLayoutManager((this)));
        recyclerView.setAdapter(new MyAdapterShowList(listeTodo, this));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnNewItem:
                // Ajout d'un nouvel Item
                String description = edtCreerItem.getText().toString();
                ItemToDo itemToDo = new ItemToDo(description);
                listeTodo.ajouterItem(itemToDo);
                sauvegardeProfilListe(profilListeToDo, pseudo);
                recyclerView.setAdapter(new MyAdapterShowList(listeTodo, this));
                edtCreerItem.setText("");
                break;
        }
    }

    @Override
    public void onItemClicked(int posItem, boolean fait, View v) {
        switch (v.getId()){
            case R.id.cbFait:
                listeTodo.getLesItems().get(posItem).setFait(fait);
                sauvegardeProfilListe(profilListeToDo, pseudo);
            case R.id.item:
                listeTodo.getLesItems().get(posItem).setFait(fait);
                sauvegardeProfilListe(profilListeToDo, pseudo);
            case R.id.containerItem:
                listeTodo.getLesItems().get(posItem).setFait(fait);
                sauvegardeProfilListe(profilListeToDo, pseudo);
        }

    }

    // ############### Sérialisation/ Désérialisation ###################################
    // Json --> ProfilListeToDo
    public ProfilListeToDo deserialisation(String jsonProfilListe){
        Gson gson = new Gson();
        ProfilListeToDo profilListeToDo = gson.fromJson(jsonProfilListe, ProfilListeToDo.class);
        return profilListeToDo;
    }

    // ProfilListeToDo --> Json
    public String serialisation(ProfilListeToDo profilListeToDo){
        Gson gson = new Gson();
        return gson.toJson(profilListeToDo);
    }

    // ############## Gestion des préférences partagées ################################
    // Chargement des données
    public ProfilListeToDo recupProfilListe(String pseudo){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String jsonProfilListe = settings.getString(pseudo, "");
        return deserialisation(jsonProfilListe);
    }

    // Sauvegarde des données
    public void sauvegardeProfilListe(ProfilListeToDo newProfilListeToDo, String pseudo){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String jsonProfilListe = serialisation(newProfilListeToDo);
        SharedPreferences.Editor editor = settings.edit();
//        editor.clear();
        editor.putString(pseudo, jsonProfilListe);
        editor.commit();
    }
}
