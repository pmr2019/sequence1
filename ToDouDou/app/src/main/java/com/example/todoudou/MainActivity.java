package com.example.todoudou;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private Button btn_Ok = null;
    private EditText edt_pseudo = null;

    // méthode appelée pour un debug dans l'appli
    private void alerter(String s) {
        Log.i("Debug",s);
        //Toast myToast = Toast.makeText(this,s,Toast.LENGTH_SHORT);
        //myToast.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // This 2 lines sets the toolbar as the app bar for the activity
        // cf https://developer.android.com/training/appbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar_main);
        setSupportActionBar(myToolbar);

        btn_Ok = findViewById(R.id.btn_ok);
        edt_pseudo = findViewById(R.id.edt_pseudo);

        Log.i("donnee", "oncretae ");

        // Lors du clic sur le bouton OK, le pseudo est sauvegardé dans les préférences
        // partagées de l’application et l’activité ChoixListActivity s’affiche
        btn_Ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alerter("Clique Ok");

                // pseudo
                String pseudo = String.valueOf(edt_pseudo.getText());
                if(pseudo != "pseudo" & pseudo.length() != 0){
                    sauvergardeLastPseudo(pseudo);

                    GestionDonnees ges = new GestionDonnees(getBaseContext());
                    ges.creerNouveauProfil(pseudo);

                    // Changement d'activité
                    Intent toChoixList = new Intent(getBaseContext(), ChoixListActivity.class);
                    startActivity(toChoixList);
                }
                else{
                    alerter("Choisir un login autre que pseudo");
                }


            }
        });
        edt_pseudo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alerter("Entrez votre pseudo");
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        alerter("onStart");

        // récupération des préférences au niveau Application
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);

        edt_pseudo.setText(settings.getString("pseudo",""));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_action_bar, menu);
        return true;
    }

    // https://developer.android.com/training/appbar/actions.html
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                alerter("Menu Préférences");
                //Lors du clic sur le bouton “Préférences” de ce menu, une activité
                //SettingsActivity s’affiche
                Intent toSettings = new Intent(this,SettingsActivity.class);
                startActivity(toSettings);
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


    private void sauvergardeLastPseudo(String pseudo){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        // pseudo
        editor.putString("pseudo", pseudo);
        editor.commit();


    }
}
