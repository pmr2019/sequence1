package com.example.pmr_exo1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public final String CAT = "PMR_Logs";
    private Button btnOK = null;
    private EditText edtPseudo = null;
    private ProfilListeToDo profil;

    private void alerter(String s) {
        Log.i(CAT, s);
        Toast myToast = Toast.makeText(this, s, Toast.LENGTH_SHORT);
        myToast.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnOK = findViewById(R.id.btnOK);
        edtPseudo = findViewById(R.id.edtPseudo);

        btnOK.setOnClickListener(this);
        edtPseudo.setOnClickListener(this);


        //for testing to avoid "JsonSyntaxException : got unexpected JsonNull"
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        editor.putString("prefs", "");
        editor.commit();
    }


    @Override
    protected void onStart() {
        super.onStart();

        // récupération des préférences au niveau Application
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String prefs = settings.getString("prefs", null);
        JsonArray prefs_array = new Gson().fromJson(prefs, JsonArray.class);
        if (prefs_array != null) {
            edtPseudo.setText(prefs_array.get(-1).getAsJsonObject().get("pseudo").getAsString());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnOK:
                String pseudo = edtPseudo.getText().toString();
                alerter("Pseudo: " + pseudo);

                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = settings.edit();
                editor.clear();

                // pseudo + listesToDo
                JsonArray prefs = new Gson().fromJson(settings.getString("prefs", ""), JsonArray.class);
                if ((prefs != null) && (!prefs.toString().contains("\"pseudo\":\"" + pseudo + "\""))) { // si prefs est null ou si le profil n'existe pas encore
                    profil = new ProfilListeToDo();
                    profil.setLogin(pseudo);
                    profil.setListes(new ArrayList<ListeToDo>());
                    prefs.add(new Gson().toJson(profil));
                }
                editor.putString("prefs", new Gson().toJson(prefs));

                // heure de connexion
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                String dateLogin = sdf.format(new Date());
                editor.putString("dateLogin", dateLogin);
                editor.commit();

                // nouvelle activité
                Intent toChoixListAct =
                        new Intent(this, ChoixListActivity.class);

                // Ajout d'un bundle de données
                Bundle data = new Bundle();
                data.putString("pseudo", pseudo);
                toChoixListAct.putExtras(data);

                // Changement d'activité
                startActivity(toChoixListAct);
                break;

            case R.id.edtPseudo:
                alerter("Saisir votre pseudo");
                break;

        }
    }

    // Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_account:
                alerter("Menu Compte");
                break;

            case R.id.action_settings:
                alerter("Menu Préférences");
                Intent toSettings = new Intent(this, SettingsActivity.class);
                startActivity(toSettings);
                break;

        }
        return super.onOptionsItemSelected(item);
    }


}
