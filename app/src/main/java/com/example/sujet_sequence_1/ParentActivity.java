package com.example.sujet_sequence_1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sujet_sequence_1.settings.SettingsActivity;

/**
 * Template d'activity héritant de AppCompatActivity
 * Celui-ci contient des méthodes utilitaire et l'initialisation de certaines variables (CAT, actionbar)
 */
public class ParentActivity extends AppCompatActivity {
    public final String CAT = "PMR";
    ActionBar actionBar;
    // to check if we are connected to Network
    public boolean isConnected = true;

    // to check if we are monitoring Network
    public boolean monitoringConnectivity = false;


    /**
     * On récupère l'actionBar lors de la création de l'activité
     *
     * @param savedInstanceState : bundle utilisé en cas de redémarrage après un arrêt pour revenir à un état précédent.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionBar = getSupportActionBar();
        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
    }


    /**
     * Méthode utilitaire générant un toast et un log
     *
     * @param s : message d'alerte
     */
    protected void alerter(String s) {
        Log.i(CAT, s);
        Toast myToast = Toast.makeText(this, s, Toast.LENGTH_SHORT);
        myToast.show();
    }

    /**
     * Création du menu dans l'action Bar
     *
     * @param menu : menu de l'action bar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * Impémentation du comportement lors de la sélection d'une option dans le
     * menu de l'action bar
     *
     * @param item l'item du menu sélectionné
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
            case R.id.action_disconnect:
                disconnect();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Récupération d'une préférence.
     *
     * @param pref: clé de la préférence
     * @return : la valeur de la préférence
     * @throws GetPreferenceException : si la préférence est vide.
     */
    public String recupPreference(String pref) throws GetPreferenceException {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String storedString = settings.getString(pref, "");
        if (!"".equals(storedString)) {
            return storedString;
        } else {
            throw new GetPreferenceException();
        }
    }

    /**
     * Retire toutes les préférences liées à un utilisateur (pseudo / mot de passe / hash)
     */
    protected void disconnect() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = settings.edit();
        editor.remove("pseudo");
        editor.remove("hash");
        editor.remove("encryptedPassword");
        editor.apply();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * Vérifie si l'application à accès à internet.
     *
     * @return booléen corespondant à la disponibilité de l'accès à internet.
     */
    public boolean verifReseau() {
        ConnectivityManager cnMngr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cnMngr.getActiveNetworkInfo();
        if (netInfo != null) {
            NetworkInfo.State netState = netInfo.getState();
            return netState.compareTo(NetworkInfo.State.CONNECTED) == 0;
        }
        return false;
    }


}