package com.example.sujet_sequence_1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Main Activity
 * The user can login in this activity.
 * This is the entry point of the application.
 */
public class MainActivity extends ParentActivity implements View.OnClickListener {
    private Button button = null; // Boutton pour valider l'entré du pseudo
    private EditText textViewEdtPseudo = null; //Champ texte où l'utilisateur entre le pseudo

    /**
     * onCreate est une fonction liée au cycle de vie de l'activité.
     *
     * @param savedInstanceState données utilisées si la réinitialisée après avoir été arrêtée
     *                           Elle est appellée lors de la création de l'activité.
     *                           Met en place le layout de l'activité et lie les variables d'nistances aux éléments du layout
     *                           Ajoute le sous-titre à l'actionBar
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.button);
        textViewEdtPseudo = findViewById(R.id.textViewEdtPseudo);
        button.setOnClickListener(this);
        actionBar.setSubtitle(R.string.BySubtitle);

    }

    /**
     * On charge les préférences lors du démarrage de l'activité.
     * On affiche le contenue de la préférence pseudo (ie. le dernier pseudo rentré) dans le champs texte.
     */
    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        textViewEdtPseudo.setText(settings.getString("pseudo", ""));
        Log.i(CAT, settings.getString("pseudo", "rien"));
    }

    /**
     * On définit le onclick pour l'application qui ici s'applique uniquement au bouton ok.
     * on récupère les préférences et le contenue du champ texte
     * Si ce dernier est non vide on met à jour le pseudo et on passe à l'activité suivante
     * avec un bundle contenant le pseudo.
     * Si on veux plus tard ajouter un autre comportement en cas de click, il faura créer un swith
     * en fonction de la vue sur laquelle le click a eu lieu.
     * @param v
     */
    @Override
    public void onClick(View v) {

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences((this));
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        String pseudo = textViewEdtPseudo.getText().toString();
        if ("".equals(pseudo)) {
            alerter(getResources().getString(R.string.promptforpseudo));
            return;
        }
        editor.putString("pseudo", pseudo);
        editor.apply();
        Intent toChoixListActivity;
        toChoixListActivity = new Intent(MainActivity.this, ChoixListActivity.class);
        Bundle myBdl = new Bundle();
        myBdl.putString("pseudo", pseudo);
        toChoixListActivity.putExtras(myBdl);
        startActivity(toChoixListActivity);
    }



}
