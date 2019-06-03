package com.example.todolist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.todolist.data.UserProfile;
import com.google.gson.Gson;

public class GenericActivity extends AppCompatActivity {

    protected SharedPreferences mSharedPreferences;
    protected String mUserName;
    protected UserProfile mUserProfile;

    // ------------------------------------------------------------------------------------------ //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initialisation du fichier des préférences, du nom de l'utilisateur et de son profil
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mUserName = mSharedPreferences.getString("userName","");
        mUserProfile = readJSON(mSharedPreferences.getString(mUserName, ""));
    }

    // ------------------------------------------------------------------------------------------ //
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // création du menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    // ------------------------------------------------------------------------------------------ //
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.settings) {
            // retourne l'activité des préférences
            Intent toSettings = new Intent(this, SettingsActivity.class);
            startActivity(toSettings);
            return true;
        }
        else {
            return super.onOptionsItemSelected(item);
        }
    }

    // ------------------------------------------------------------------------------------------ //
    protected String writeJSON(UserProfile profile) {
        // sérialise la classe UserProfile en un texte JSON
        Gson gson = new Gson();
        return gson.toJson(profile);
    }

    // ------------------------------------------------------------------------------------------ //
    protected UserProfile readJSON(String profileJSON) {
        // instancie la classe UserProfile à partir de texte JSON
        Gson gson = new Gson();
        return gson.fromJson(profileJSON, UserProfile.class);
    }

    // ------------------------------------------------------------------------------------------ //
    protected void updateProfile(UserProfile userProfile) {
        // mise à jour du profil dans les préférences
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(mUserName, writeJSON(userProfile));
        editor.apply();
    }

    // ------------------------------------------------------------------------------------------ //
    protected void createProfileIfNeeded() {
        // création du profil utilisateur s'il n'existe pas
        String userProfileJSON = mSharedPreferences.getString(mUserName, "");
        if(userProfileJSON != null) {
            if (userProfileJSON.matches("")) {
                updateProfile(new UserProfile(mUserName));
            }
        } else {
            // pointeur nul : hautement improbable
            updateProfile(new UserProfile(mUserName));
        }
    }

    // ------------------------------------------------------------------------------------------ //
    protected void toMain() {
        // accés à l'activité principale
        Intent toMain = new Intent(this, MainActivity.class);
        startActivity(toMain);
    }

    // ------------------------------------------------------------------------------------------ //
    protected void toLists() {
        // accés aux liste de l'utilisateur
        Intent toLists = new Intent(this, ListsActivity.class);
        startActivity(toLists);
    }

    // ------------------------------------------------------------------------------------------ //
    protected void toItems(String listTitle) {
        // accés aux items de la to-do liste selectionnéee
        Intent toItems = new Intent(this, ItemsActivity.class);
        toItems.putExtra("list", listTitle);
        startActivity(toItems);
    }
}
