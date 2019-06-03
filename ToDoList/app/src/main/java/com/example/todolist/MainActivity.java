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
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static String CAT = "PMR";
    private Button btnOk = null;
    private EditText edtPseudo = null;
    private Button btnReset;

    // Fonction alerter()
    private void alerter(String s){
        Toast toastAlert = Toast.makeText(this, s, Toast.LENGTH_SHORT);
        toastAlert.show();
        Log.i(CAT, s);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        alerter("onCreate");

        // On récupère les vues du layout par id
        btnOk = findViewById(R.id.btnOk);
        edtPseudo = findViewById(R.id.edtPseudo);
        btnReset = findViewById(R.id.btnReset);

        // Mise en place d'un gestionnaire de clic
        btnOk.setOnClickListener(this);
        edtPseudo.setOnClickListener(this);
        btnReset.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
//        alerter("onStart");
        String pseudo;

        // Récupération du Pseudo dans les préférences (niveau application)
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        // PSEUDO
        pseudo = settings.getString("pseudo", "");
        edtPseudo.setText(pseudo);
    }

    // Override de la méthode onClick()
    @Override
    public void onClick(View v) {
        // alerter("Clic");
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = settings.edit();

        switch (v.getId()){
            case R.id.btnOk:
                String pseudo = edtPseudo.getText().toString();

                // Enregistrement du pseudo dans les préférences
                editor.putString("pseudo", pseudo);
                editor.commit();
//                alerter("Sauvegarde: " + pseudo);

                // Initialisation d'un Intent explicite
                Intent toChoixListActivity = new Intent(this,ChoixListActivity.class);

                // Initialisation d'un Bundle
                Bundle data = new Bundle();
                data.putString("pseudo", pseudo);
                toChoixListActivity.putExtras(data);

                // Changement d'activité
                startActivity(toChoixListActivity);
                break;
            case R.id.edtPseudo:
                alerter("Saisir votre pseudo");
                break;

            case R.id.btnReset:
                editor.clear();
                editor.commit();
                edtPseudo.setText("");
                alerter("Reset");
                break;
        }
    }

    // Affichage d'un menu
    // Instantiation
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
        editor.clear();
        editor.putString(pseudo, jsonProfilListe);
        editor.commit();
    }
}
