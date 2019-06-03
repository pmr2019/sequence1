package com.example.todopmr;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/*
Cette activité gère l'affichage et la modification des items associés à la liste cliquée.
 */
public class ShowListActivity extends GenericActivity implements ItemAdapter.ActionListenerItem {

    //Views du layout
    private Button btn_ajout;
    private EditText edt_item;

    //Attributs de sauvegarde des informations
    public String JSONFile;
    private ListeDesProfils listeProfils;
    private String pseudo;
    private String titre;
    private int idListe;

    //Objets en cours
    private ProfilListeToDo profilActuel;
    private ListeToDo listeActuelle;
    private ItemAdapter adapterEnCours;
    private int indiceProfil;
    private int indiceListe;

    //Langue de l'application
    private String languageToLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Mise à jour de la langue de l'application selon les préférences.
        SharedPreferences settings  = PreferenceManager.getDefaultSharedPreferences(this);
        languageToLoad = settings.getString("langue", "");
        actualiserLangue(languageToLoad);

        setContentView(R.layout.activity_show_list);

        btn_ajout = findViewById(R.id.btn_ajout);
        edt_item = findViewById(R.id.edt_item);

        //Récupération de l'information et mise en forme de ListeDesProfils pour traitement.
        pseudo = settings.getString("pseudo","");
        titre = settings.getString("titre", "");
        idListe = settings.getInt("idListe", -1);
        JSONFile = settings.getString("JSONFile", "");
        listeProfils = deserialiserJSON(JSONFile);

        //Récupération des objets en cours d'utilisation : Profil
        indiceProfil = rechercherProfil(pseudo, listeProfils);
        profilActuel = listeProfils.getListeProfils().get(indiceProfil);

        //Récupération des objets en cours d'utilisation : Liste
        indiceListe = rechercherListe(idListe, profilActuel);
        ArrayList<ListeToDo> meslistes= profilActuel.getMesListeToDo();
        if (indiceListe >= 0) {
            listeActuelle = meslistes.get(indiceListe);
            if (listeActuelle.getLesItems().size() == 0) {
                alerter(getString(R.string.no_item));
            }
            //Affichage des items
            RecyclerView recyclerView = findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            this.adapterEnCours = new ItemAdapter(listeActuelle.getLesItems(), this);
            recyclerView.setAdapter(adapterEnCours);

            recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));
        }

        // Lors du clic sur le bouton d'ajout
        btn_ajout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description = edt_item.getText().toString();
                ItemToDo nouvelItem = new ItemToDo(description);
                profilActuel.getMesListeToDo().get(indiceListe).ajouterItem(nouvelItem);

                //On actualise le JSON
                JSONFile = actualiser(indiceProfil, profilActuel, listeProfils);
                listeProfils = deserialiserJSON(JSONFile);

                //On actualise l'affichage
                adapterEnCours.actualiserAffichage();
            }
        });

    }

    /*
    En cas de retour vers la page précédente, on envoie les informations actualisées.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            //Envoi des informations de préférence : pseudo, historique, stringJSON, langue, titre.
            SharedPreferences newSettings = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = newSettings.edit();
            editor.clear();
            editor.putString("historique", afficherPseudos(JSONFile));
            editor.putString("titre", titre);
            editor.putString("pseudo", pseudo);
            editor.putString("JSONFile", JSONFile);
            editor.putString("langue", languageToLoad);
            editor.commit();

            //Vers CheckListActivity
            Intent toCheckListActivity = new Intent(this, CheckListActivity.class);
            startActivity(toCheckListActivity);
            return true;
        }
        return false;
    }

    /*
    Affichage de la description de l'item.
     */
    @Override
    public void onItemClickedItem(ItemToDo itemClicked) {
        alerter("Item : " + itemClicked.getDescription());
    }

    /*
    En cas de clic sur l'icon de suppresion d'un item, on supprime et on met à jour les informations.
     */
    @Override
    public void onClickDeleteButtonItem(ItemToDo itemToDelete) {
        ArrayList<ItemToDo> listeItemActualisee = new ArrayList<>();
        //Recherche de l'item à supprimer
        for (int i = 0; i < listeActuelle.getLesItems().size(); i++){
            if (listeActuelle.getLesItems().get(i).getIdItem() != itemToDelete.getIdItem()) {
                listeItemActualisee.add(listeActuelle.getLesItems().get(i));
            }
        }
        //Mise à jour ds informations
        profilActuel.getMesListeToDo().get(indiceListe).setLesItems(listeItemActualisee);
        JSONFile = actualiser(indiceProfil, profilActuel, listeProfils);
    }

    /*
    En cas de clic sur la checkbox d'un item, on change le statut et on met à jour les informations.
     */
    @Override
    public void onClickCheckButtonItem(ItemToDo itemToCheck) {
        //Modification de l'attribut fait de l'item.
        if (itemToCheck.getFait()) {
            itemToCheck.setFait(false);
        }
        else {
            itemToCheck.setFait(true);
        }
        //Actualisation de l'information
        int indiceItem = -1;
        for (int i=0 ; i < listeActuelle.getLesItems().size() ; i++) {
            if (listeActuelle.getLesItems().get(i).getIdItem() == itemToCheck.getIdItem()) {
                indiceItem = i;
            }
        }
        profilActuel.getMesListeToDo().get(indiceListe).getLesItems().get(indiceItem).setFait(itemToCheck.getFait());
        JSONFile = actualiser(indiceProfil, profilActuel, listeProfils);
    }
}