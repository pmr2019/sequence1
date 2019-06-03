package com.example.pmr_exo1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;

public class ChoixListActivity extends AppCompatActivity implements View.OnClickListener {
    private Context mContext;

    public final String CAT = "PMR_logs";
    private Button btnOK = null;
    private EditText edtListe = null;

    private ProfilListeToDo profil;
    private ArrayList<ListeToDo> listes;
    private String pseudo;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<String> mDataset = new ArrayList<>();

    private void alerter(String s) {
        Log.i(CAT, s);
        Toast myToast = Toast.makeText(this, s, Toast.LENGTH_SHORT);
        myToast.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choix_liste);

        Bundle data = this.getIntent().getExtras();
        pseudo = data.getString("pseudo");

        btnOK = findViewById(R.id.btnOK);
        edtListe = findViewById(R.id.edtListe);

        recyclerView = findViewById(R.id.list_recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new MyAdapter(mDataset);
        recyclerView.setAdapter(mAdapter);

        btnOK.setOnClickListener(this);
        edtListe.setOnClickListener(this);

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
        JsonArray prefs = new Gson().fromJson(settings.getString("prefs", null), JsonArray.class);
        if (prefs != null) {
            for (int i = 0; i < prefs.size(); i++) {
                if (prefs.get(i).getAsJsonObject().get("pseudo").getAsString() == pseudo) {
                    profil = new Gson().fromJson(prefs.get(i), ProfilListeToDo.class);
                }
            }
        }

        if (profil != null) {
            listes = profil.getListes();
        }

        if (listes != null) {
            for (int i = 0; i < listes.size(); i++) {
                mDataset.add(listes.get(i).getTitre());
            }
        }
        mAdapter.notifyDataSetChanged();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnOK:
                String nomListe = edtListe.getText().toString();
                mDataset.add(nomListe);
                mAdapter.notifyDataSetChanged();
                edtListe.getText().clear();

                // sauvegarder dans les sharedpreferences les nouvelles listes
                ListeToDo nouvelleListe = new ListeToDo(nomListe);
                profil.ajouteListe(nouvelleListe);
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = settings.edit();
                editor.clear();
                JsonArray prefs = new Gson().fromJson(settings.getString("prefs", null), JsonArray.class);
                prefs.add(new Gson().toJson(profil));
                editor.putString("prefs", new Gson().toJson(prefs));
                editor.commit();
                break;
            case R.id.edtListe:
                break;
        }
    }

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
                break;

            case R.id.action_settings:
                Intent toSettings = new Intent(this, SettingsActivity.class);
                startActivity(toSettings);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    public ProfilListeToDo getProfilListeToDo(String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Gson g = new Gson();
        String json = prefs.getString(key, "");
        return g.fromJson(json, ProfilListeToDo.class);
    }
}
