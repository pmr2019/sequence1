package com.example.sujet_sequence_1;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Activité affichant les éléments d'une todo liste et permetant d'en rajouter
 */
public class ShowListActivity extends ParentActivity implements RecyclerViewAdapterItem.OnItemClickListener {
    private ProfilListeToDo profilListeToDo;//profil de l'utilisateur
    private Button buttonNewToDo;//bouton pour ajouter une nouvelle liste
    private EditText editTextNewItemToDo;// champ texte pour le nom de la nouvelle liste
    private ListeToDo listeToDo;// Liste Todo chargée
    private Integer indice; //indice de la liste
    private LinearLayoutManager layoutManager; // layout manager du recycler view
    private RecyclerView recyclerView; // RecylclerView pour afficher les listes
    private RecyclerViewAdapterItem adapter;// adapter pour adapter les données
    /**
     * appelée lors de la création de l'activité
     * met en place le Recylcler view et charge la liste souhaitée
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_list);
        Bundle b = this.getIntent().getExtras();
        profilListeToDo = importProfil(b.getString("pseudo"));
        indice = b.getInt("indice");
        listeToDo = profilListeToDo.getMesListeToDo().get(indice);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewItemsToDo);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layoutManager = new GridLayoutManager(this, 2);
        } else {
            layoutManager = new LinearLayoutManager(this);
        }
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecyclerViewAdapterItem(listeToDo.getLesItems(), this);
        recyclerView.setAdapter(adapter);


        buttonNewToDo = findViewById(R.id.buttonNewToDo);
        editTextNewItemToDo = findViewById(R.id.editTextNewItemToDo);
        buttonNewToDo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newItemName = editTextNewItemToDo.getText().toString();
                if (!"".equals(newItemName)) {
                    ItemToDo newItemToDo = new ItemToDo(newItemName);
                    listeToDo.ajouterItem(newItemToDo);
                    adapter.notifyDataSetChanged();
                    editTextNewItemToDo.setText("");
                } else {
                    alerter(getResources().getString(R.string.promptTaskName));
                }
            }
        });
        actionBar.setSubtitle(getResources().getString(R.string.listString) + " : " + listeToDo.getTitreListeToDo());
    }

    /**
     * Sauvegarde des modification lorsque l'activité est mise en pase
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
        listeToDo.getLesItems().get(indice).toggleFait(); //toggleFait change le booléen faitd'un todoItem
        adapter.notifyDataSetChanged();
    }
}
