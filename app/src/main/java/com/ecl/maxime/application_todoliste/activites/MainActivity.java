package com.ecl.maxime.application_todoliste.activites;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import com.ecl.maxime.application_todoliste.R;
import com.ecl.maxime.application_todoliste.classes.ListeToDo;
import com.ecl.maxime.application_todoliste.classes.ProfileListeToDo;
import com.google.gson.Gson;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import android.util.Pair;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final String PREFS = "MyProfile";
    public static final String DERNIER_PSEUDO = "Dernier Pseudo";
    public static final String PROFIL_COURANT = "Profil courant";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // Référencer les vues à des objets
        Button btn_ok = findViewById(R.id.btn_ok);
        final EditText pseudo_edittext = findViewById(R.id.pseudo_edittext);
        final EditText password_edt = findViewById(R.id.password_edt);


        // Préférences
        final SharedPreferences sharedPreferences = getSharedPreferences(PREFS, MODE_PRIVATE);

        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String strUserName = SP.getString("username", sharedPreferences.getString(DERNIER_PSEUDO, ""));

        pseudo_edittext.setText(strUserName);

        btn_ok.setEnabled(false);

        if (verifReseau())
            btn_ok.setEnabled(true);

        // Mise en place de l'écouteur
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verifReseau()) {
                    String password = password_edt.getText().toString();
                    String pseudo = pseudo_edittext.getText().toString();
                    Pair<String, String> pair = new Pair<>(pseudo, password);

                    sharedPreferences.edit().putString(DERNIER_PSEUDO, pseudo).apply();

                    // On enregistre dans les préférences un nouveau profil si ce nom n'existe pas déjà
                    if (!sharedPreferences.contains(pseudo)) {
                        ProfileListeToDo profileListeToDo = new ProfileListeToDo(pseudo, password, new ArrayList<ListeToDo>());
                        Gson gson = new Gson();
                        sharedPreferences.edit().putString(pseudo, gson.toJson(profileListeToDo)).apply();
                    }

                    // On passe le pseudo à l'activité suivante
                    Intent i = new Intent(MainActivity.this, ChoixListActivity.class);
                    i.putExtra("Pseudo", pseudo);
                    startActivity(i);
                }
            }
        });

    }

    public boolean verifReseau()
    {
        // On vérifie si le réseau est disponible,
        // si oui on change le statut du bouton de connexion
        ConnectivityManager cnMngr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cnMngr.getActiveNetworkInfo();

        String sType = "Aucun réseau détecté";
        Boolean bStatut = false;
        if (netInfo != null)
        {

            NetworkInfo.State netState = netInfo.getState();

            if (netState.compareTo(NetworkInfo.State.CONNECTED) == 0)
            {
                bStatut = true;
                int netType= netInfo.getType();
                switch (netType)
                {
                    case ConnectivityManager.TYPE_MOBILE :
                        sType = "Réseau mobile détecté"; break;
                    case ConnectivityManager.TYPE_WIFI :
                        sType = "Réseau wifi détecté"; break;
                }

            }
        }

        this.alerter(sType);
        return bStatut;
    }

    public void alerter(String s){
        Toast toast = Toast.makeText(this, s, Toast.LENGTH_LONG);
        toast.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }
}
