package com.example.sujet_sequence_1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.sujet_sequence_1.api.APIClient;
import com.example.sujet_sequence_1.api.APIInterface;
import com.example.sujet_sequence_1.bdd.AppDatabase;
import com.example.sujet_sequence_1.bdd.BddListeToDo;
import com.example.sujet_sequence_1.models.ListeToDo;
import com.example.sujet_sequence_1.models.ProfilListeToDo;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ChoixListActivity affiche les to do listes associées à un profil.
 */
public class ChoixListActivity extends ParentActivity implements RecyclerViewAdapterList.OnItemClickListener {
    private String pseudo; //profil de l'utilisateur
    private EditText editTextNameNewList; // champ texte pour le nom de la nouvelle liste
    private RecyclerViewAdapterList adapter; // adapter pour adapter les données
    private AppDatabase database;
    private String hash;
    private String baseUrl;
    private ArrayList<ListeToDo> mesListeToDo = new ArrayList<>();
    private Button buttonNewList;
    private ConnectivityManager connectivityManager;


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
            pseudo = recupPreference("pseudo");
            AppDatabase database = Room.databaseBuilder(getApplicationContext(),
                    AppDatabase.class, "ToDoApp-db").build();
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
            adapter = new RecyclerViewAdapterList(mesListeToDo, this);
            recyclerView.setAdapter(adapter);

            // On récupère les Listes avec à l'API
            // Interface ajout de nouvelle liste
            //bouton pour ajouter une nouvelle liste
            buttonNewList = findViewById(R.id.buttonNewList);
            editTextNameNewList = findViewById(R.id.editTextNameNewList);
            buttonNewList.setEnabled(isConnected);
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
            actionBar.setSubtitle(getResources().getString(R.string.profilString) + " : " + pseudo);
            hash = recupPreference("hash");
            baseUrl = recupPreference("baseUrl");
        } catch (GetPreferenceException e) {
            Log.i(CAT, "PreferenceFailure");
            e.printStackTrace();
            startActivity(new Intent(ChoixListActivity.this, MainActivity.class));
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        checkConnectivity();
        if (!isConnected){
            new AsyncTaskGetListFromBdd().execute();
        }
        else {
            appelAPIGetListeToDos(hash, baseUrl);
        }
        buttonNewList.setEnabled(isConnected);
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
        Log.i(CAT, "id : " + mesListeToDo.get(indice).getId());
        Intent intentToShowListActiviy = new Intent(ChoixListActivity.this, ShowListActivity.class);
        Bundle myBdl = new Bundle();
        myBdl.putInt("id", mesListeToDo.get(indice).getId());
        myBdl.putString("titre", mesListeToDo.get(indice).getTitreListeToDo());
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
        APIInterface api = APIClient.createService(APIInterface.class, baseUrl);
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
                    mesListeToDo = response.body().getMesListeToDo();
                    adapter.setMesListesToDo(mesListeToDo);
                    adapter.notifyDataSetChanged();
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            AppDatabase database = Room.databaseBuilder(getApplicationContext(),
                                    AppDatabase.class, "ToDoApp-db").build();
                            database.ListeToDoDao().insertAll(convertToBdd(mesListeToDo,pseudo));
                            Log.i(CAT, "Enregistrement dans la base de données");
                        }
                    });


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

    /**
     * Convertit une liste de BddListeToDo en une liste de ListeToDo
     *
     * @param all
     * @return list une liste de ListeToDo
     */

    private ArrayList<ListeToDo> convertFromBdd(List<BddListeToDo> all) {
        ArrayList<ListeToDo> list = new ArrayList<>();
        for(BddListeToDo bddListeToDo: all){
            ListeToDo listeToDo = new ListeToDo(bddListeToDo.titreListeToDo);
            listeToDo.setId(bddListeToDo.id);
            list.add(listeToDo);
        }
        return list;
    }

    /**
     * Convertit un profil en une liste de ListeToDo
     *
     */
    private ArrayList<BddListeToDo> convertToBdd(ArrayList<ListeToDo> listeToDos,String pseudo) {
        ArrayList<BddListeToDo> list = new ArrayList<>();
        for(ListeToDo listeToDo: listeToDos){
            BddListeToDo bddListeToDo = new BddListeToDo(listeToDo.getId(), listeToDo.getTitreListeToDo(),pseudo);
            listeToDo.setId(bddListeToDo.id);
            list.add(bddListeToDo);
        }
        return list;
    }


    private void checkConnectivity() {
        // here we are getting the connectivity service from connectivity manager
        connectivityManager = (ConnectivityManager) getSystemService(
                Context.CONNECTIVITY_SERVICE);

        // Getting network Info
        // give Network Access Permission in Manifest
        final NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        // isConnected is a boolean variable
        // here we check if network is connected or is getting connected
        isConnected = activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();

        // SHOW ANY ACTION YOU WANT TO SHOW
        // WHEN WE ARE NOT CONNECTED TO INTERNET/NETWORK
        // if Network is not connected we will register a network callback to  monitor network
        connectivityManager.registerNetworkCallback(
                new NetworkRequest.Builder()
                        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                        .build(), connectivityCallback);
        monitoringConnectivity = true;


    }

    /**
     * On désactive le callback du connectivity manager pour éviter les fuites mémoires
     */
    @Override
    protected void onPause() {
        // if network is being monitered then we will unregister the network callback
        if (monitoringConnectivity) {
            final ConnectivityManager connectivityManager
                    = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            connectivityManager.unregisterNetworkCallback(connectivityCallback);
            monitoringConnectivity = false;
        }
        super.onPause();
    }

    private ConnectivityManager.NetworkCallback connectivityCallback
            = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(Network network) {
            isConnected = true;
            Log.i(CAT, "INTERNET CONNECTED");
            appelAPIGetListeToDos(hash, baseUrl);
            dislayButton();
        }

        @Override
        public void onLost(Network network) {
            isConnected = false;
            Log.i(CAT, "INTERNET LOST");
            dislayButton();
        }
    };

    void dislayButton(){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                buttonNewList.setEnabled(isConnected);
            }
        });
    }

    public class AsyncTaskGetListFromBdd extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            AppDatabase database = Room.databaseBuilder(getApplicationContext(),
                    AppDatabase.class, "ToDoApp-db").build();
            mesListeToDo = convertFromBdd(database.ListeToDoDao().loadAllByUserLogin(pseudo));
            Log.i(CAT, "Depuis bdd : " +convertFromBdd(database.ListeToDoDao().loadAllByUserLogin(pseudo)));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter.setMesListesToDo(mesListeToDo);
            adapter.notifyDataSetChanged();
        }
    }

}



