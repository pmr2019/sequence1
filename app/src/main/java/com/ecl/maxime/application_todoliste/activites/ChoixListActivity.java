package com.ecl.maxime.application_todoliste.activites;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ecl.maxime.application_todoliste.adapter.ListeToDoAdapter;
import com.ecl.maxime.application_todoliste.R;
import com.ecl.maxime.application_todoliste.classes.ItemToDo;
import com.ecl.maxime.application_todoliste.classes.ListeToDo;
import com.ecl.maxime.application_todoliste.classes.ProfileListeToDo;
import com.google.gson.Gson;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChoixListActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ProfileListeToDo mProfileListeToDo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choix_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Récupération du pseudo
        Intent intent = getIntent();
        final String pseudoCourant = intent.getStringExtra("Pseudo");

        // Récupération du profil correspondant
        final SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.PREFS, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(pseudoCourant, null);

        if (json != null)
            mProfileListeToDo=gson.fromJson(json, ProfileListeToDo.class);

        // Récupération des Listes associé au profil courant
        ArrayList<ListeToDo> sesListes = mProfileListeToDo.getMesListeToDo();

        // Gestion du RecyclerView associé à l'activité
        mRecyclerView = findViewById(R.id.list_listetodo);
        final ListeToDoAdapter listeToDoAdapter = new ListeToDoAdapter(sesListes, new ListeToDoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ListeToDo listeToDo, int position) {
                // On sérialise la liste sélectionnée pour la transmettre à l'activité suivante
                Gson gson1 = new Gson();
                Intent i = new Intent(ChoixListActivity.this, ShowListActivity.class);
                i.putExtra("Num", position);
                i.putExtra("ProfileCourant", gson1.toJson(mProfileListeToDo));
                startActivity(i);
            }
        });

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(listeToDoAdapter);

        final EditText editText = findViewById(R.id.ed_nouvelle_liste);
        Button button = findViewById(R.id.btn_liste);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nom_liste = editText.getText().toString();
                ListeToDo new_liste = new ListeToDo(nom_liste);
                new_liste.setLesItems(new ArrayList<ItemToDo>());
                mProfileListeToDo.ajouteListe(new_liste);
                Gson gson1 = new Gson();
                sharedPreferences.edit().putString(mProfileListeToDo.getLogin(), gson1.toJson(mProfileListeToDo)).apply();
                listeToDoAdapter.notifyDataSetChanged();
                editText.setText("");
            }
        });
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
            Intent i = new Intent(ChoixListActivity.this, SettingsActivity.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }
}
