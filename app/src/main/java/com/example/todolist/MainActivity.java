package com.example.todolist;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    List<ProfilListeToDo> listUsers = new ArrayList<>() ;

    // le String myJsonString correspond au fichier Json associé a la liste des profils "listUsers"
    private String myJsonString;
    private Button btnOK = null;
    private EditText edtPseudo = null;
    private SharedPreferences mSettings;
    private SharedPreferences.Editor mEditor;
    private String pseudo;
    private int positionUser;
    GsonBuilder builder = new GsonBuilder();
    Gson gson = builder.create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnOK = findViewById(R.id.btnOK);
        edtPseudo = findViewById(R.id.edt_pseudo);
        btnOK.setOnClickListener(this);

        mSettings =PreferenceManager.getDefaultSharedPreferences(this);
        myJsonString = mSettings.getString("myJsonString", "");
        if(!myJsonString.isEmpty()){
            afficherDernierPseudo();
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btnOK) {
            pseudo = edtPseudo.getText().toString();

            //si myJsonString n'existe pas alors on le créé,
            // on y ajoute l'utilisateur et on envoie le fichier
            if(myJsonString.isEmpty()) {
                Log.i("le fichier","n'existe pas");
                listUsers = new ArrayList<>();
                ProfilListeToDo user = new ProfilListeToDo(pseudo);
                listUsers.add(user);
                myJsonString = gson.toJson(listUsers);  //on réinitialise myJsonString avec la liste des profils mise à jour
                Log.i("myJsonString", myJsonString );
                majJson(); //on met a jour le fichier json myJsonString
            }
            // sinon si le fichier existe :
            else {
                // on récupère la liste des profils
                listUsers = gson.fromJson(myJsonString, new TypeToken<List<ProfilListeToDo>>(){}.getType());
                Log.i("le fichier","existe");
                //On regarde si le pseudo est déjà connu dans la liste des profils
                 if(utilisateurConnu(listUsers,pseudo)){
                     majJson();
                 }
                 else{ //Si il n'est pas connu
                     Log.i("utilisateur","inconnu");
                     ProfilListeToDo user = new ProfilListeToDo(pseudo);
                     listUsers.add(user);
                     //on met a jour la position de l'utilisateur dans la liste de profil
                     positionUser=listUsers.size()-1;
                     myJsonString = gson.toJson(listUsers);
                     majJson();
                 }
            }
        }
        Intent toChoixListActivity = new Intent(this, ChoixListActivity.class);
        Bundle data = new Bundle();
        data.putInt("positionUser",positionUser);
        data.putString("pseudo",edtPseudo.getText().toString());
        toChoixListActivity.putExtras(data);
        startActivity(toChoixListActivity);
    }


    protected void onStart() {
        super.onStart();
        myJsonString = mSettings.getString("myJsonString", "");
        if(!myJsonString.isEmpty()){
            afficherDernierPseudo();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.action_settings) {
                //  Lors du clic sur le menu préférences,
                //  afficher l'activité 'SettingActivity'
                Intent toSettings = new Intent(this,SettingsActivity.class);
                startActivity(toSettings);
        }
        return super.onOptionsItemSelected(item);
    }




    ////////////// Fonction de gestion des fichiers JSON/////////////


    private boolean utilisateurConnu(List<ProfilListeToDo> listUsers, String pseudo) {
        positionUser = -1;
        for (ProfilListeToDo profil : listUsers) {
            positionUser+=1;
            if (profil.getLogin().matches(pseudo)) {
                return true;
            }
        }
        return false;
    }

    private void majJson() {
        mEditor = mSettings.edit();
        mEditor.clear();
        mEditor.putString("myJsonString", myJsonString);
        mEditor.commit();
    }

    private void afficherDernierPseudo() {
        listUsers = gson.fromJson(myJsonString, new TypeToken<List<ProfilListeToDo>>(){}.getType());
        edtPseudo.setText(listUsers.get(listUsers.size()-1).getLogin());
    }
}
