package com.example.todopmr;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
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
Cette activité gère l'affichage et la modification des listes associé au pseudo entré.
 */
public class CheckListActivity extends GenericActivity implements ListAdapter.ActionListenerListe {

    //Views du layout
    private Button btn_ajout;
    private EditText edt_liste;

    //Attributs de sauvegarde des informations
    public String JSONFile;
    private ListeDesProfils listeProfils;
    private String pseudo;

    //Objets en cours
    private ProfilListeToDo profilActuel;
    private ListAdapter adapterEnCours;
    private int indiceProfil;

    //Langue de l'application
    private String languageToLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Mise à jour de la langue de l'application selon les préférences.
        SharedPreferences settings  = PreferenceManager.getDefaultSharedPreferences(this);
        languageToLoad = settings.getString("langue", "");
        actualiserLangue(languageToLoad);

        setContentView(R.layout.activity_check_list);

        btn_ajout = findViewById(R.id.btn_ajout);
        edt_liste = findViewById(R.id.edt_liste);

        //Récupération de l'information et mise en forme de ListeDesProfils pour traitement.
        pseudo = settings.getString("pseudo","");
        JSONFile = settings.getString("JSONFile", "");
        listeProfils = deserialiserJSON(JSONFile);

        //Si la liste des profils est vide, on crée une nouvelle instance.
        if (listeProfils == null){
            listeProfils = new ListeDesProfils(new ArrayList<ProfilListeToDo>());
            listeProfils.getListeProfils().add(new ProfilListeToDo(pseudo));
            indiceProfil = 0;
        }
        //Sinon on accède au profil en cours, qu'il soit nouveau ou ancien.
        else {
            indiceProfil = rechercherProfil(pseudo, listeProfils);
        }
        JSONFile = serialiserJSON(listeProfils);
        profilActuel = listeProfils.getListeProfils().get(indiceProfil);

        //Affichage des listes du profil actuel.
        if (profilActuel.getMesListeToDo().size() == 0) {
            alerter(getString(R.string.no_list));
        }
        ArrayList<ListeToDo> listesToDo = profilActuel.getMesListeToDo();
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapterEnCours = new ListAdapter(listesToDo, this);
        recyclerView.setAdapter(adapterEnCours);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));

        // Lors du clic sur le bouton d'ajout
        btn_ajout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String titre = edt_liste.getText().toString();
                ListeToDo nouvelleListe = new ListeToDo(titre);
                profilActuel.ajouteListe(nouvelleListe);

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

            //Envoi des informations de préférence : pseudo, historique, stringJSON, langue.
            SharedPreferences newSettings = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = newSettings.edit();
            editor.clear();
            editor.putString("historique", afficherPseudos(JSONFile));
            editor.putString("pseudo", pseudo);
            editor.putString("JSONFile", JSONFile);
            editor.putString("langue", languageToLoad);
            editor.commit();

            //Vers MainActivity
            Intent toMainActivity = new Intent(this, MainActivity.class);
            startActivity(toMainActivity);
            return true;
        }
        return false;
    }

    /*
    En cas de clic sur une liste, on affiche les items de cette liste.
     */
    @Override
    public void onItemClickedListe(ListeToDo listeClicked) {

        //Envoi des informations de préférence : pseudo, historique, stringJSON, langue, idListe, titre.
        int indiceListe = rechercherListe(listeClicked.getIdListe(), profilActuel);
        ListeToDo listeActuelle = profilActuel.getMesListeToDo().get(indiceListe);
        SharedPreferences newSettings = PreferenceManager.getDefaultSharedPreferences(CheckListActivity.this);
        SharedPreferences.Editor editor = newSettings.edit();
        editor.clear();
        editor.putInt("idListe", listeActuelle.getIdListe());
        editor.putString("titre", listeActuelle.getTitreListeToDo());
        editor.putString("historique", afficherPseudos(JSONFile));
        editor.putString("pseudo", pseudo);
        editor.putString("JSONFile", JSONFile);
        editor.putString("langue", languageToLoad);
        editor.commit();

        //Vers ShowListActivity
        Intent toShowListActivity = new Intent(CheckListActivity.this, ShowListActivity.class);
        startActivity(toShowListActivity);
    }

    /*
    En cas de clic sur l'icon de suppresion d'une liste, on supprime et on met à jour les informations.
     */
    @Override
    public void onClickDeleteButtonListe(ListeToDo listeToDelete) {
        ArrayList<ListeToDo> listeActualisee = new ArrayList<>();
        //Recherche de la liste à supprimer
        for (int i = 0; i < profilActuel.getMesListeToDo().size(); i++){
            if (profilActuel.getMesListeToDo().get(i).getIdListe() != listeToDelete.getIdListe()) {
                listeActualisee.add(profilActuel.getMesListeToDo().get(i));
            }
        }
        //Mise à jour ds informations
        profilActuel.setMesListeToDo(listeActualisee);
        JSONFile = actualiser(indiceProfil, profilActuel, listeProfils);
    }

}
