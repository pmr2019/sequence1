package com.ecl.maxime.application_todoliste.activites;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ecl.maxime.application_todoliste.adapter.ItemToDoAdapter;
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

public class ShowListActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ProfileListeToDo mProfileListeToDo;
    private ListeToDo mListeToDo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Récupération de la liste sélectionnée
        final Intent i = getIntent();

        final SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.PREFS, MODE_PRIVATE);
        final Gson gson = new Gson();
        final int num_liste = i.getIntExtra(ChoixListActivity.ID_LISTE, 0);
        String profile = i.getStringExtra(MainActivity.PROFIL_COURANT);

        mProfileListeToDo = null;
        if (profile!=null){
            mProfileListeToDo=gson.fromJson(profile, ProfileListeToDo.class);
        }

        mListeToDo = mProfileListeToDo.getMesListeToDo().get(num_liste);

        // Récupération des items qu'elle contient
        ArrayList<ItemToDo> sesItems = mListeToDo.getLesItems();

        // Gestion du RecyclerView associé à l'activité
        mRecyclerView = findViewById(R.id.list_itemtodo);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        final ItemToDoAdapter itemToDoAdapter = new ItemToDoAdapter(sesItems, new ItemToDoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ItemToDo itemToDo) {
                // On change la valeur du booléen
                itemToDo.setFait(!itemToDo.isFait());
                sharedPreferences.edit().putString(mProfileListeToDo.getLogin(), gson.toJson(mProfileListeToDo)).apply();
            }
        });
        mRecyclerView.setAdapter(itemToDoAdapter);

        final EditText editText = findViewById(R.id.ed_nouvel_item);
        Button button = findViewById(R.id.btn_item);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson1 = new Gson();
                String description_item = editText.getText().toString();
                ItemToDo new_item = new ItemToDo(description_item);
                mListeToDo.ajouteItem(new_item);
                mProfileListeToDo.getMesListeToDo().set(num_liste, mListeToDo);
                sharedPreferences.edit().putString(mProfileListeToDo.getLogin(), gson1.toJson(mProfileListeToDo)).apply();
                itemToDoAdapter.notifyDataSetChanged();
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
            Intent i = new Intent(ShowListActivity.this, SettingsActivity.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

}
