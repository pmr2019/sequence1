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

import com.example.sujet_sequence_1.api.APIClient;
import com.example.sujet_sequence_1.api.APIInterface;
import com.example.sujet_sequence_1.models.ListeToDo;
import com.example.sujet_sequence_1.models.ProfilListeToDo;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ChoixListActivity affiche les to do listes associées à un profil.
 */
public class ChoixListActivity extends ParentActivity implements RecyclerViewAdapterList.OnItemClickListener {
    private ProfilListeToDo profilListeToDo; //profil de l'utilisateur
    private EditText editTextNameNewList; // champ texte pour le nom de la nouvelle liste
    private RecyclerView.Adapter adapter; // adapter pour adapter les données

    /**
     * Appelée lors de la création de l'activité
     *
     * @param savedInstanceState : pour reconstruire dans un état précédent en cas d'arrêt.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choix_list);
    }

    /**
     * Appelée lors du démarrage de l'activité
     * onStart met en place le Recylcler view et charge le profil
     */
    @Override

    protected void onStart() {
        super.onStart();
//on essaye de mettre en place l'activité. Si on a une exception dues aux préférences, on renvoit
// l'utilisateur sur la maiActivity pour qu'il se reconnecte.
        try {
            //On récupère le pseudo depuis les préférences
            profilListeToDo = new ProfilListeToDo(recupPreference("pseudo"));
            // RecylclerView pour afficher les listes
            RecyclerView recyclerView = findViewById(R.id.recyclerViewLists);
            //Modification du layoutManager suivant l'orientation
            // layout manager pour le recycler view
            RecyclerView.LayoutManager layoutManager;
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                layoutManager = new GridLayoutManager(this, 2);
            } else {
                layoutManager = new LinearLayoutManager(this);
            }

            //parametrage du recycler view
            recyclerView.setLayoutManager(layoutManager);
            adapter = new RecyclerViewAdapterList(this.profilListeToDo.getMesListeToDo(), this);
            recyclerView.setAdapter(adapter);

            // On récupère les Listes avec à l'API
            appelAPIGetListeToDos(recupPreference("hash"), recupPreference("baseUrl"));


            // Interface ajout de nouvelle liste
            //bouton pour ajouter une nouvelle liste
            Button buttonNewList = findViewById(R.id.buttonNewList);
            editTextNameNewList = findViewById(R.id.editTextNameNewList);
            buttonNewList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String newListName = editTextNameNewList.getText().toString();
                    if (!"".equals(newListName)) {
                        try {
                            appelAPIAddAListeToDo(newListName);
                        } catch (GetPreferenceException e) {
                            e.printStackTrace();
                        }
                        // On ajoute une liste avec l'API
                    } else {
                        alerter(getResources().getString(R.string.promptListName));
                    }
                }
            });

            //add subtitle to action bar
            actionBar = getSupportActionBar();
            actionBar.setSubtitle(getResources().getString(R.string.profilString) + " : " + profilListeToDo.getLogin());

        } catch (GetPreferenceException e) {
            Log.i(CAT, "PreferenceFailure");
            e.printStackTrace();
            startActivity(new Intent(ChoixListActivity.this, MainActivity.class));
        }

    }


    /**
     * Implémentation de l'interface onItemClickListener
     * pour mettre en place ce onItemClick sur tout les éléments de la liste
     * avec le recycler view
     *
     * @param indice correspondant à l'item sur lequel on a cliqué
     */
    @Override
    public void onItemClick(int indice) {

        Log.d(CAT, String.valueOf(indice));
        Log.i(CAT, "id : " + profilListeToDo.getMesListeToDo().get(indice).getId());
        Intent intentToShowListActiviy = new Intent(ChoixListActivity.this, ShowListActivity.class);
        Bundle myBdl = new Bundle();
        myBdl.putInt("id", profilListeToDo.getMesListeToDo().get(indice).getId());
        myBdl.putString("titre", profilListeToDo.getMesListeToDo().get(indice).getTitreListeToDo());
        intentToShowListActiviy.putExtras(myBdl);
        startActivity(intentToShowListActiviy);
    }

    /**
     * Appel l'API pour rajouter une ToDoListe, mise à jour des données et de l'affichage.
     *
     * @param newListName : nom de la nouvelle liste
     * @throws GetPreferenceException : si certaines préférences (hash/url Api) n'ont pas pu êtres récupérées correctement.
     */
    private void appelAPIAddAListeToDo(String newListName) throws GetPreferenceException {

        // On ajoute une liste avec l'API
        final String baseUrl = recupPreference("baseUrl");
        APIInterface api = APIClient.createService(APIInterface.class, baseUrl);
        final String hash = recupPreference("hash");
        Call<ProfilListeToDo> call = api.addListeToDoAPI(hash, newListName);
        call.enqueue(new Callback<ProfilListeToDo>() {

            @Override
            public void onResponse(Call<ProfilListeToDo> call, Response<ProfilListeToDo> response) {
                if (response.isSuccessful()) {
                    //En cas de succès, on récupère les listes avec l'API
                    appelAPIGetListeToDos(hash, baseUrl);
                    Log.i(CAT, "Liste ajoutées avec succès");
                    editTextNameNewList.setText("");
                } else {
                    alerter("Erreur lors de l'ajout d'une liste");
                }
            }

            @Override
            public void onFailure(Call<ProfilListeToDo> call, Throwable t) {
                alerter(getResources().getString(R.string.timeout));
                Log.i(CAT, "Erreur lors de l'ajout d'une liste");
            }
        });
        Log.i(CAT, "fin");
    }

    /**
     * Récupère les ToDoListe à l'aide de l'api, les associes au profil d'utilisateur et signal
     * à l'adaptateur que les données ont changée.
     *
     * @param hash   : pour s'authentifier auprès de l'API
     * @param apiUrl : Url de l'API
     */
    private void appelAPIGetListeToDos(String hash, String apiUrl) {
        APIInterface api = APIClient.createService(APIInterface.class, apiUrl);
        Call<ProfilListeToDo> call = api.importListeToDoAPI(hash);
        call.enqueue(new Callback<ProfilListeToDo>() {
            @Override
            public void onResponse(Call<ProfilListeToDo> call, Response<ProfilListeToDo> response) {
                if (response.isSuccessful()) {
                    ArrayList<ListeToDo> mesListeToDo = response.body().getMesListeToDo();
                    ((RecyclerViewAdapterList) adapter).setMesListesToDo(mesListeToDo);
                    ChoixListActivity.this.profilListeToDo.setMesListeToDo(mesListeToDo);
                    adapter.notifyDataSetChanged();
                } else {
                    alerter("Error fetching ToDoLists");
                }
            }

            @Override
            public void onFailure(Call<ProfilListeToDo> call, Throwable t) {
                alerter(getResources().getString(R.string.timeout));
                Log.i(CAT, "ListeToDo fetching failure");
            }
        });
    }
}
