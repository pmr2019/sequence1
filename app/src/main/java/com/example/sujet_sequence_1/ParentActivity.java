package com.example.sujet_sequence_1;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Template d'activity héritant de AppCompatActivity
 * Celui-ci contient des méthodes utilitaire et l'initialisation de certaines variables (CAT, actionbar)
 *
 */
public class ParentActivity extends AppCompatActivity {
    public final String CAT = "PMR";
    ActionBar actionBar;

    /**
     * On récupère l'actionBar lors de la création de l'activité
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionBar = getSupportActionBar();
    }

    /**
     * Méthode utilitaire générant un toast et un log
     * @param s
     */
    protected void alerter(String s) {
        Log.i(CAT, s);
        Toast myToast = Toast.makeText(this, s, Toast.LENGTH_SHORT);
        myToast.show();
    }

    /**
     * Création du menu dans l'action Bar
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * Impémentation du comportement lors de la sélection d'une option dans le
     * menu de l'action bar
     * @param item l'item du menu sélectionné
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                Intent toSettingsActivity;
                toSettingsActivity = new Intent(this, SettingsActivity.class);
                startActivity(toSettingsActivity);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Importation du profil depuis les fichiers texte json correspondant
     * ou création si pas de fichier existant
     * @param pseudo chaîne de caractères du pseudo qu'on souhaite importé
     * @return
     */
    public ProfilListeToDo importProfil(String pseudo) {
        final GsonBuilder builder = new GsonBuilder();
        final Gson gson = builder.create();
        String filename = pseudo + "_json";
        FileInputStream inputStream;
        StringBuilder sJsonLu = new StringBuilder();
        ProfilListeToDo profil;

        /* Import du fichier JSON de sauvegarde dans l'objet */
        try {
            inputStream = openFileInput(filename);
            int content;
            while ((content = inputStream.read()) != -1) {
                // convert to char and display it
                sJsonLu.append((char) content);
            }
            inputStream.close();

            profil = gson.fromJson(sJsonLu.toString(), ProfilListeToDo.class);
        } catch (Exception e) {

            /* Creation d'un profil */
            profil = newProfil(pseudo);
            Log.i(CAT, "Création d'un nouveau profil " + profil.getLogin());

            String fileContents = gson.toJson(profil);
            FileOutputStream outputStream;

            try {
                outputStream = openFileOutput(filename, MODE_PRIVATE);
                outputStream.write(fileContents.getBytes());
                outputStream.close();
                Log.i(CAT, "Création du fichier  " + pseudo + "_json");
            } catch (Exception ex) {
                ex.printStackTrace();
                Log.i(CAT, "Impossible de créer le fichier de sauvegarde du profil par défaut");
            }
        }
        return profil;
    }

    /**
     * Création d'un nouveau profil
     * @param pseudo
     * @return
     */
    public static ProfilListeToDo newProfil(String pseudo) {
        return new ProfilListeToDo(pseudo);
    }

    /**
     * Sauvegarde d'un profil dans un fichier texte au format json corespondant.
     * @param profilListeToDo
     */
    public void sauveProfilToJsonFile(ProfilListeToDo profilListeToDo) {
        Log.i(CAT, "Sauvegarde en cours");
        final GsonBuilder builder = new GsonBuilder();
        final Gson gson = builder.create();
        String filename = profilListeToDo.getLogin() + "_json";
        String fileContents = gson.toJson(profilListeToDo);
        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(fileContents.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
