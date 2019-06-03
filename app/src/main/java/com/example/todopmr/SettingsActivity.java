package com.example.todopmr;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;

import java.util.Locale;

/*
Cette activité gère les préférences : modification de la langue, affichage et suppression de l'historique, etc.
 */
public class SettingsActivity extends PreferenceActivity {

    //Paramètres affichés
    private String historique;
    private String pseudo;
    private String JSONFile;
    private String langue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Récupération des informations en paramètres.
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        historique = settings.getString("historique", "Historique vide");
        JSONFile = settings.getString("JSONFile", "");
        pseudo = settings.getString("pseudo", "");
        langue = settings.getString("langue", "");

        //Mise à jour de la langue de l'application
        actualiserLangue(langue);

        addPreferencesFromResource(R.xml.preferences);

        //Modification de la langue, lors du clic sur l'objet ListPrefrence
        final ListPreference listeLangues = (ListPreference) findPreference("langues");
        listeLangues.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                langue = newValue.toString();

                //Mise à jour des préférences avec la nouvelle langue choisie
                SharedPreferences newSettings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor newEditor = newSettings.edit();
                newEditor.clear();
                newEditor.putString("pseudo", pseudo);
                newEditor.putString("JSONFile", JSONFile);
                newEditor.putString("historique", historique);
                newEditor.putString("langue", langue);
                newEditor.commit();

                //Vers SettingsActivity
                Intent toSettingsActivity = new Intent(SettingsActivity.this, SettingsActivity.class);
                startActivity(toSettingsActivity);
                return true;
            }
        });

        //Gestion de l'historique
        final Preference afficherHistoric = findPreference("historic");
        afficherHistoric.setSummary(historique);

        //Suppression de l'historique
        Preference suppressHistoric = findPreference("suppress_historic");
        suppressHistoric.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                historique = "Historique vide";
                JSONFile = "";
                pseudo = "";

                //Mise à jour des préférences
                SharedPreferences newSettings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor newEditor = newSettings.edit();
                newEditor.clear();
                newEditor.putString("pseudo", pseudo);
                newEditor.putString("JSONFile", JSONFile);
                newEditor.putString("historique", historique);
                newEditor.putString("langue", langue);
                newEditor.commit();
                afficherHistoric.setSummary("");
                return true;
            }
        });
    }

    /*
    En cas de retour vers la page précédente, on envoie les informations actualisées.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            //Mise à jour des préférences avec la nouvelle langue choisie.
            SharedPreferences newSettings = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = newSettings.edit();
            editor.clear();
            editor.putString("historique", historique);
            editor.putString("pseudo", pseudo);
            editor.putString("JSONFile", JSONFile);
            editor.putString("langue", langue);
            editor.commit();

            //Vers MainActivity
            Intent toMainActivity = new Intent(this, MainActivity.class);
            startActivity(toMainActivity);
            return true;
        }
        return false;
    }

    /*
    Actualisation de la langue.
     */
    public void actualiserLangue(String langue) {
        String languageToLoad = langue; //
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
    }
}
