package com.example.todolist.recycler_activities;

/** Définition de la classe ShowListActivity.
 * Cette classe représente l'activité ShowList Activity de l'application
 */

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
import com.example.todolist.modele.ItemToDo;
import com.example.todolist.modele.ProfilListeToDo;
import com.example.todolist.recycler_activities.adapter.ItemAdapterItem;

import java.util.ArrayList;

/** Définition de la classe ShowListActivity.
 * Cette classe représente l'activité ShowList Activity de l'application
 */
public class ShowListActivity extends Library implements View.OnClickListener,
        ItemAdapterItem.onClickItemListener{

    /* Le pseudo rentré par l'utilisateur dans l'activité principale */
    private String pseudo;
    /* La description du nouvel item à ajouter, saisi par l'utilisateur dans l'activité courante */
    private EditText ajouterItem;
    /* Le profil associé au pseudo */
    private ProfilListeToDo profil;
    /* L'adapteur associé à la Recycler View de l'activité courante */
    private ItemAdapterItem itemAdapterItem;
    /* La Recycle View de l'activité courante */
    private RecyclerView recyclerView;
    /* La liste des items associé à la ToDoList courante */
    private ArrayList<ItemToDo> listeItem;
    /* La position (identifiant) de la ToDoList courante */
    private int position;

    /**
     * Fonction onCreate appelée lors de le création de l'activité
     *
     * @param savedInstanceState données à récupérer si l'activité est réinitialisée après avoir planté
     *         Lie l'activité à son layout et récupère les éléments avec lesquels on peut intéragir
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showliste);

        /* Récupération du pseudo depuis les préférences de l'application */
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        pseudo = preferences.getString("pseudo", "");
        Bundle b = this.getIntent().getExtras();
        position = b.getInt("idListe");

        /* Traitement de l'ajout d'un item à la ToDoList */
        Button btnOk = findViewById(R.id.btnOk);
        btnOk.setOnClickListener(this);
        ajouterItem = findViewById(R.id.ajouterItem);
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
     * à la liste des items
     */
    @Override
    protected void onResume() {
        super.onResume();
        /* Création du profil associé */
        profil = importProfil(pseudo);

        /* Mise en place de la Recycler View sur la liste des items associée à la ToDoList */
        recyclerView = findViewById(R.id.recyclerView);
        listeItem = profil.getMesListesToDo().get(position).getLesItems();
        itemAdapterItem = new ItemAdapterItem(listeItem, this);
        recyclerView.setAdapter(itemAdapterItem);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    /** Fonction par défaut de l'interface View.OnClickListener, appelée lors du clic sur la vue
     * @param v la vue cliquée
     * Ici, lors du clic sur le bouton OK, on crée l'item avec la description fournie par l'utilisateur,
     *          et on l'ajoute à la liste des items de la ToDoList
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnOk:
                ItemToDo item = new ItemToDo();
                item.setDescription(ajouterItem.getText().toString());
                Log.i("ChoixListe", "onClick: " + item.toString());
                listeItem = profil.getMesListesToDo().get(position).getLesItems();
                listeItem.add(item);
                profil.getMesListesToDo().get(position).setLesItems(listeItem);
                itemAdapterItem.notifyItemInserted(listeItem.size()-1);
                Log.i("ChoixListe", "onClick: " + itemAdapterItem.getItemCount());
                break;
            default:
        }
    }

    /** Permet de changer la valeur du paramètre fait de l'item sélectionné
     * @param position l'indice où se trouve l'item dans la liste des items
     */
    @Override
    public void clickItem(int position) {
        listeItem.get(position).setFait(!listeItem.get(position).getFait());

        Log.i("ClickItem", "clickItem: "+profil.toString());
        itemAdapterItem.notifyItemChanged(position);
    }
}
