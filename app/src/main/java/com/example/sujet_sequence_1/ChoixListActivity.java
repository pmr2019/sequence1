package com.example.sujet_sequence_1;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

/**
 * ChoixListActivity affiche les to do listes associées à un profil.
 */
public class ChoixListActivity extends ParentActivity implements RecyclerViewAdapterList.OnItemClickListener {
    private ProfilListeToDo profilListeToDo; //profil de l'utilisateur
    private EditText et;
    private Button buttonNewList; //bouton pour ajouter une nouvelle liste
    private EditText editTextNameNewList; // champ texte pour le nom de la nouvelle liste
    private RecyclerView recyclerView; // RecylclerView pour afficher les listes
    private RecyclerView.Adapter adapter; // adapter pour adapter les données
    private RecyclerView.LayoutManager layoutManager; // layout manager pour le recycler view
    private ArrayList<ListeToDo> listeToDos; //Todo listes du profil chargé
    private Bundle b; // bundle psssée lors de la création de l'activité

    /**
     * appelée lors de la création de l'activité
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choix_list);
        b = this.getIntent().getExtras();
    }

    /**
     * Appelée lors du démarrage de l'activité
     * onStart met en place le Recylcler view et charge le profil
     */
    @Override

    protected void onStart() {
        super.onStart();
        profilListeToDo = importProfil(b.getString("pseudo"));
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewLists);
        listeToDos = profilListeToDo.getMesListeToDo();

        //Modification du layoutManager suivant l'orientation
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layoutManager = new GridLayoutManager(this, 2);
        } else {
            layoutManager = new LinearLayoutManager(this);
        }
        //paramétrage du recycler view
        recyclerView.setLayoutManager(layoutManager);
        Log.i(CAT, "got listes to do");
        adapter = new RecyclerViewAdapterList(listeToDos, this);
        recyclerView.setAdapter(adapter);
        // Interface ajout de nouvelle liste
        buttonNewList = findViewById(R.id.buttonNewList);
        editTextNameNewList = findViewById(R.id.editTextNameNewList);
        buttonNewList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newListName = editTextNameNewList.getText().toString();
                if (!"".equals(newListName)) {
                    ListeToDo newList = new ListeToDo(newListName);
                    listeToDos.add(newList);
                    adapter.notifyDataSetChanged();
                    editTextNameNewList.setText("");
                } else {
                    alerter(getResources().getString(R.string.promptListName));
                }
            }
        });
        actionBar = getSupportActionBar();

        actionBar.setSubtitle(getResources().getString(R.string.profilString) + " : " + profilListeToDo.getLogin());
    }

    /**
     * On sauvegarde le profil lorsque l'activité est mise en pause pour enregistrer les modifications
     */
    @Override
    protected void onPause() {
        super.onPause();
        sauveProfilToJsonFile(profilListeToDo);
    }

    /**
     * Implémetnation de l'interface onItemClickListener
     * pour mettre en place ce onItemClick sur tout les éléments de la liste
     * avec le recycler view
     * @param indice correspondant à l'item sur lequel on a cliqué
     */
    @Override
    public void onItemClick(int indice) {
        Intent intentToShowListActiviy = new Intent(ChoixListActivity.this, ShowListActivity.class);
        Bundle myBdl = new Bundle();
        myBdl.putInt("indice", indice);
        myBdl.putString("pseudo", profilListeToDo.getLogin());
        intentToShowListActiviy.putExtras(myBdl);
        startActivity(intentToShowListActiviy);
    }
}
