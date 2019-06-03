package com.example.todolist.recycler_activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.R;
import com.example.todolist.modele.ListeToDo;
import com.example.todolist.modele.ProfilListeToDo;
import com.example.todolist.recycler_activities.adapter.ItemAdapterList;

/** Définition de la classe ChoixListActivity.
 * Cette classe représente l'activité ChoixListe Activity de l'application
 */
public class ChoixListActivity extends Library implements View.OnClickListener,
        ItemAdapterList.onClickListListener {

    /* Le pseudo rentré par l'utilisateur dans l'activité principale */
    private String pseudo;
    /* Le titre de la nouvelle ToDoList à ajouter, saisi par l'utilisateur dans l'activité courante */
    private EditText ajouterListe;
    /* Le profil associé au pseudo */
    private ProfilListeToDo profil;
    /* L'adapteur associé à la Recycler View de l'activité courante */
    private ItemAdapterList itemAdapterList;
    /* La Recycle View de l'activité courante */
    private RecyclerView recyclerView;

    /** Fonction onCreate appelée lors de le création de l'activité
     * @param savedInstanceState données à récupérer si l'activité est réinitialisée après avoir planté
     * Lie l'activité à son layout et récupère les éléments avec lesquels on peut intéragir
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choixliste);

        /* Récupération du pseudo depuis les préférences de l'application */
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        pseudo = preferences.getString("pseudo","");

        /* Traitement de l'ajout d'une ToDoList au profil */
        Button btnOk = findViewById(R.id.btnOk);
        btnOk.setOnClickListener(this);
        ajouterListe = findViewById(R.id.ajouterListe);
    }

    /** Fonction onPause appelée lors d'un changement d'activité au détriment de celle-ci
     * Permet de sauvegarder le profil courant dans les préférences
     */
    @Override
    protected void onPause() {
        super.onPause();
        sauveProfilToJsonFile(profil);
    }

    /** Fonction onResume appelée après la création de l'activité et à chaque retour sur l'activité courante
     * Permet de recharger le profil courant à partir du pseudo et de générer la RecyclerView associée
     *      * à la liste des ToDoLists
     */
    @Override
    protected void onResume() {
        super.onResume();
        /* Création du profil associé */
        profil = importProfil(pseudo);

        /* Mise en place de la Recycler View sur la liste des ToDoLists associée au profil*/
        recyclerView = findViewById(R.id.recyclerView);
        itemAdapterList = new ItemAdapterList(profil.getMesListesToDo(),this);
        recyclerView.setAdapter(itemAdapterList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    /** Fonction par défaut de l'interface View.OnClickListener, appelée lors du clic sur la vue
     * @param v la vue cliquée
     * Ici, lors du clic sur le bouton OK, on crée la ToDoList avec le titre fourni par l'utilisateur,
     *          et on l'ajoute à la liste des ToDoLists du profil
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnOk:
                ListeToDo listeToDo = new ListeToDo();
                listeToDo.setTitreListeToDo(ajouterListe.getText().toString());
                Log.i("ChoixListe", "onClick: " + listeToDo.toString());
                profil.ajouteListe(listeToDo);
                itemAdapterList.notifyItemInserted(profil.getMesListesToDo().size()-1);
                Log.i("ChoixListe", "onClick: " + itemAdapterList.getItemCount());
                break;
            default:
        }
    }

    /** Permet d'ouvrir l'activité ShowList Activity lors du clic sur une des ToDoLists
     * @param position l'indice où se trouve la ToDoList dans la liste des ToDoLists
     */
    @Override
    public void clickList(int position) {
        Intent showListIntent = new Intent(this, ShowListActivity.class);
        showListIntent.putExtra("idListe", position);
        startActivity(showListIntent);
    }
}
