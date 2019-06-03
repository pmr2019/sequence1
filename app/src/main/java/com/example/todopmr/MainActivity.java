package com.example.todopmr;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/*
Cette activité gère la page d'accueil, l'accès aux paramètres, et l'entrée d'un pseudo.
 */
public class MainActivity extends GenericActivity {

    //Views du layout
    private EditText edtPseudo;
    private Button btnOK;

    //Attributs de sauvegarde des informations
    private ListeDesProfils listeProfils;
    private String JSONFile;
    private String pseudo;

    //Langue de l'application
    private String languageToLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Mise à jour de la langue de l'application selon les préférences.
        SharedPreferences settings  = PreferenceManager.getDefaultSharedPreferences(this);
        languageToLoad = settings.getString("langue", "");
        actualiserLangue(languageToLoad);

        setContentView(R.layout.activity_main);

        btnOK = findViewById(R.id.btnOK);
        edtPseudo = findViewById(R.id.edtPseudo);

        //Récupération de l'information et mise en forme de ListeDesProfils pour traitement.
        JSONFile = settings.getString("JSONFile", "");
        listeProfils = deserialiserJSON(JSONFile);

        // Lors du clic sur le champ de saisi du pseudo
        edtPseudo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alerter(getString(R.string.set_pseudo));
            }
        });

        // Lors du clic sur le bouton OK
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pseudo = edtPseudo.getText().toString();
                if (pseudo.isEmpty()) {
                    alerter(getString(R.string.pseudo_vide));
                }
                else {
                    //Envoi des informations de préférence : pseudo, historique, stringJSON, langue.
                    SharedPreferences newSettings = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                    final SharedPreferences.Editor editor = newSettings.edit();
                    editor.clear();
                    editor.putString("historique", afficherPseudos(JSONFile));
                    editor.putString("pseudo", pseudo);
                    editor.putString("JSONFile", JSONFile);
                    editor.putString("langue", languageToLoad);
                    editor.commit();

                    //Vers CheckListActivity
                    Intent accesCheckList = new Intent(MainActivity.this, CheckListActivity.class);
                    startActivity(accesCheckList);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Affichage du pseudo précédent après récupération dans les préférences.
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        edtPseudo.setText(settings.getString("pseudo",""));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Une seule option dans le menu : préférences

        //Envoi des informations de préférence : pseudo, historique, stringJSON, langue.
        SharedPreferences newSettings = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        final SharedPreferences.Editor editor = newSettings.edit();
        editor.clear();
        editor.putString("historique", afficherPseudos(JSONFile));
        editor.putString("pseudo", pseudo);
        editor.putString("JSONFile", JSONFile);
        editor.putString("langue", languageToLoad);
        editor.commit();

        //Vers SettingsActivity
        Intent toSettingsActivity = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(toSettingsActivity);
        return super.onOptionsItemSelected(item);
    }

}
