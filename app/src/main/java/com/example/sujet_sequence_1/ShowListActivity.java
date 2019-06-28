package com.example.sujet_sequence_1;

import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.sujet_sequence_1.api.APIClient;
import com.example.sujet_sequence_1.api.APIInterface;
import com.example.sujet_sequence_1.api.UpdateItemWorker;
import com.example.sujet_sequence_1.bdd.AppDatabase;
import com.example.sujet_sequence_1.bdd.BddItemToDo;
import com.example.sujet_sequence_1.models.ItemToDo;
import com.example.sujet_sequence_1.models.ListeToDo;
import com.example.sujet_sequence_1.models.ProfilListeToDo;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activité affichant les éléments d'une todoListe et permetant d'en rajouter
 */
public class ShowListActivity extends ParentActivity implements RecyclerViewAdapterItem.OnItemClickListener {
    private ImageButton imageButtonUrl;//bouton pour ajouter un champs url au nouvel item
    private EditText editTextNewItemToDo;// champ texte pour le nom de la nouvelle liste
    private EditText editTextUrl;
    private ListeToDo listeToDo;// ListeTodo chargée
    private Integer id; //indice de la liste
    private RecyclerViewAdapterItem adapter;// adapter pour adapter les données
    private Button buttonNewToDo;
    private ConnectivityManager connectivityManager;

    /**
     * appelée lors de la création de l'activité
     * Vérfication que le bundle n'est pas vide
     * Mise en place de l'interface
     *
     * @param savedInstanceState : pour reconstruire dans un état précédent en cas d'arrêt.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //vérfication que le bundle n'est pas vide

        Bundle b = this.getIntent().getExtras();
        if (b != null) {
            listeToDo = new ListeToDo(b.getString("titre"));
            id = b.getInt("id");
            listeToDo.setId(id);
        } else {
            Log.i(CAT, "No bundle info. Terminating");
            finish();
        }

        setContentView(R.layout.activity_show_list);
        inializeRecylerView();
        intializeNewListInterface();
        actionBar.setSubtitle(getResources().getString(R.string.listString) + " : " + listeToDo.getTitreListeToDo());
    }

    /**
     * Initialisation du RecyclerView
     */
    private void inializeRecylerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerViewItemsToDo);
        LinearLayoutManager layoutManager;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layoutManager = new GridLayoutManager(this, 2);
        } else {
            layoutManager = new LinearLayoutManager(this);
        }
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecyclerViewAdapterItem(listeToDo.getLesItems(), this);
        recyclerView.setAdapter(adapter);
        checkConnectivity();
        if (!isConnected) {
            new AsyncTaskGetItemFromBdd().execute();

        } else {
            try {
                appelAPIListeItem(recupPreference("hash"), recupPreference("baseUrl"));
            } catch (GetPreferenceException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Initialisation de l'interface de création d'un nouvel item
     */
    private void intializeNewListInterface() {
        // boutton pour ajouter une url à la nouvelle liste
        imageButtonUrl = findViewById(R.id.imageButtonUrl);

        editTextUrl = findViewById(R.id.editTextUrl);
        imageButtonUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageButtonUrl.setVisibility(View.GONE);
                editTextUrl.setVisibility(View.VISIBLE);
            }
        });

        //bouton pour ajouter une nouvelle liste
        buttonNewToDo = findViewById(R.id.buttonNewToDo);
        editTextNewItemToDo = findViewById(R.id.editTextNewItemToDo);
        buttonNewToDo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String newItemName = editTextNewItemToDo.getText().toString();
                final String newItemUrl = editTextUrl.getText().toString();
                if (!"".equals(newItemName)) {
                    try {
                        appelAPIAddItem(newItemName, newItemUrl);
                    } catch (GetPreferenceException e) {
                        e.printStackTrace();
                    }
                } else {
                    alerter(getResources().getString(R.string.promptTaskName));
                }
            }
        });
    }

    /**
     * Implémentation de l'interface onItemClickListener
     * pour mettre en place ce onItemClick sur tout les éléments de la liste
     * avec le recycler view
     *
     * @param indice correspondant à l'item sur lequel on a cliqué
     */
    @Override
    public void onItemClick(final int indice) {
        Log.d(CAT, String.valueOf(indice));
        final ItemToDo itemToDoClicked = listeToDo.getLesItems().get(indice);
        Log.i(CAT, "title clicked item : " + itemToDoClicked.getDescription());
        boolean fait = itemToDoClicked.isFait();
        int itemId = itemToDoClicked.getId();
        if (isConnected) {
            try {
                appelAPIUpdateItem(itemId, (fait ? 0 : 1));
            } catch (GetPreferenceException e) {
                e.printStackTrace();
            }
        } else {
            Constraints constraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();
            Data itemData = new Data.Builder()
                    .putInt("idList", id)
                    .putInt("idItem", itemId)
                    .putInt("check", fait ? 0 : 1)
                    .build();
            OneTimeWorkRequest updateItemRequest = new OneTimeWorkRequest.Builder(UpdateItemWorker.class)
                    .setConstraints(constraints)
                    .setInputData(itemData)
                    .addTag("Todo-app")
                    .build();
            WorkManager.getInstance().enqueue(updateItemRequest);
            itemToDoClicked.setFait(!fait);
            new AsyncTaskUpdateItemBdd().execute(indice);
        }
        adapter.notifyDataSetChanged(); //to ensure that display match data
    }

    /**
     *
     */
    private void appelAPIUpdateItem(Integer itemId, Integer check) throws GetPreferenceException {
        final String baseUrl = recupPreference("baseUrl");
        APIInterface api = APIClient.createService(APIInterface.class, baseUrl);
        final String hash = recupPreference("hash");
        Call<ListeToDo> call = api.checkItemAPI(hash, id, itemId, check);
        call.enqueue(new Callback<ListeToDo>() {
            @Override
            public void onResponse(Call<ListeToDo> call, Response<ListeToDo> response) {
                if (response.isSuccessful()) {
                    appelAPIListeItem(hash, baseUrl);
                } else {
                    alerter("Error checking Item");
                }
            }

            @Override
            public void onFailure(Call<ListeToDo> call, Throwable t) {
                alerter(getResources().getString(R.string.timeout));
                Log.i(CAT, "Erreur lors du (dé)cochage d'un item");

            }
        });
    }

    /**
     * Appel l'API pour ajouter un item à la liste affichée. Récupère aussi les nouvelles données et préviens l'adaptateur.
     *
     * @param newItemName : nom du nouvel item
     * @param url         : url de l'API
     * @throws GetPreferenceException : si elle ne peut pas récupérer le hash ou l'url
     */
    private void appelAPIAddItem(String newItemName, String url) throws GetPreferenceException {
        final String baseUrl = recupPreference("baseUrl");
        APIInterface api = APIClient.createService(APIInterface.class, baseUrl);
        final String hash = recupPreference("hash");
        Call<ProfilListeToDo> call;
        if ("".equals(url)) {
            call = api.addItemToDoAPI(hash, id, newItemName);
        } else {
            if (!(url.startsWith("http://") || url.startsWith("https://"))) {
                url = "http://" + url;
            }
            call = api.addItemToDoAPI(hash, id, newItemName, url);
        }
        call.enqueue(new Callback<ProfilListeToDo>() {
            @Override
            public void onResponse(Call<ProfilListeToDo> call, Response<ProfilListeToDo> response) {
                if (response.isSuccessful()) {
                    //En cas de succès, on récupère les listes avec l'API
                    appelAPIListeItem(hash, baseUrl);
                    Log.i(CAT, "Liste ajoutées avec succès");
                    editTextNewItemToDo.setText("");
                    editTextUrl.setText("");
                    imageButtonUrl.setVisibility(View.VISIBLE);
                    editTextUrl.setVisibility(View.GONE);
                } else {
                    alerter("Erreur lors de l'ajout d'un item");
                }
            }

            @Override
            public void onFailure(Call<ProfilListeToDo> call, Throwable t) {

                alerter(getResources().getString(R.string.timeout));
                Log.i(CAT, "Erreur lors de l'ajout d'un item");
            }
        });

    }

    /**
     * Récupère les items d'une liste et préviens l'adaptateur que les données ont changées.
     *
     * @param hash    : pour s'authentifier auprès de l'API
     * @param baseUrl : url de l'API
     */
    private void appelAPIListeItem(String hash, String baseUrl) {
        APIInterface api = APIClient.createService(APIInterface.class, baseUrl);
        Call<ListeToDo> call = api.importListeItemAPI(hash, id);
        call.enqueue(new Callback<ListeToDo>() {
            @Override
            public void onResponse(Call<ListeToDo> call, Response<ListeToDo> response) {
                if (response.isSuccessful()) {
                    ArrayList<ItemToDo> lesItems = response.body().getLesItems();
                    adapter.setMesListesItem(lesItems);
                    ShowListActivity.this.listeToDo.setLesItems(lesItems);
                    Log.i(CAT, lesItems.toString());
                    adapter.notifyDataSetChanged();
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            AppDatabase database = Room.databaseBuilder(getApplicationContext(),
                                    AppDatabase.class, "ToDoApp-db").build();
                            database.ItemToDoDao().insertAll(convertToBdd(ShowListActivity.this.listeToDo));
                            Log.i(CAT, "Enregistrement dans la base de données de : " + convertToBdd(ShowListActivity.this.listeToDo).toString());
                        }
                    });
                } else {
                    alerter("Error fetching ItemLists");
                }
            }

            @Override
            public void onFailure(Call<ListeToDo> call, Throwable t) {
                alerter(getResources().getString(R.string.timeout));
                Log.i(CAT, "ItemToDo fetching failure");
            }
        });
        Log.i(CAT, "finAppelAPIListeItem");


    }

    /**
     * Convertit une liste de BddItemToDo en une liste de ItemToDo
     *
     * @param all liste de BddItemToDo
     * @return list une liste d'ItemToDo
     */


    private ArrayList<ItemToDo> convertFromBdd(List<BddItemToDo> all) {
        ArrayList<ItemToDo> list = new ArrayList<>();
        for (BddItemToDo bddItemToDo : all) {
            ItemToDo itemToDo = new ItemToDo(bddItemToDo.id);
            itemToDo.setDescription(bddItemToDo.description);
            itemToDo.setFait(bddItemToDo.fait);
            itemToDo.setUrl(bddItemToDo.url);
            list.add(itemToDo);
        }
        return list;
    }

    /**
     * Convertit une listeToDo en une liste de BddItemToDo
     *
     * @param listeToDo
     * @return list une liste d'ItemToDo
     */

    private ArrayList<BddItemToDo> convertToBdd(ListeToDo listeToDo) {
        ArrayList<BddItemToDo> list = new ArrayList<>();
        for (ItemToDo itemToDo : listeToDo.getLesItems()) {
            BddItemToDo bddItemToDo = new BddItemToDo(itemToDo.getId());
            bddItemToDo.description = itemToDo.getDescription();
            bddItemToDo.fait = itemToDo.getFait();
            bddItemToDo.url = itemToDo.getUrl();
            bddItemToDo.idList = listeToDo.getId();
            list.add(bddItemToDo);
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
        // isConnected is a boolean variabl
        // here we check if network is connected or is getting connected
        isConnected = activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
        // SHOW ANY ACTION YOU WANT TO SHOW
        // WHEN WE ARE NOT CONNECTED TO INTERNET/NETWORK
        Log.i(CAT, " NO NETWORK IN LIST ITEM!");
        // if Network is not connected we will register a network callback to  monitor network
        connectivityManager.registerNetworkCallback(
                new NetworkRequest.Builder()
                        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                        .build(), connectivityCallback);
        monitoringConnectivity = true;


    }


    @Override
    protected void onResume() {
        Log.i(CAT, "onResume");
        super.onResume();
        checkConnectivity();
        buttonNewToDo.setEnabled(isConnected);
    }

    /**
     * On désactive le callback du connectivity manager pour éviter les fuites mémoires
     */
    @Override
    protected void onPause() {
        Log.i(CAT, "onPause");
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
            Log.i(CAT, "INTERNET CONNECTED IN LIST ITEM");
            try {
                appelAPIListeItem(recupPreference("hash"), recupPreference("baseUrl"));
            } catch (GetPreferenceException e) {
                e.printStackTrace();
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    buttonNewToDo.setEnabled(true);
                }
            });

        }

        @Override
        public void onLost(Network network) {
            isConnected = false;
            Log.i(CAT, "INTERNET LOST In LIST ITEM");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    buttonNewToDo.setEnabled(false);
                }
            });
        }


    };

    /**
     * Convertit un item en un item au format bdd
     */
    private BddItemToDo convertToBdd(ItemToDo itemToDo) {
        BddItemToDo bddItemToDo = new BddItemToDo(itemToDo.getId());
        bddItemToDo.fait = itemToDo.getFait();
        bddItemToDo.description = itemToDo.getDescription();
        bddItemToDo.url = itemToDo.getUrl();
        bddItemToDo.idList = id;
        return (bddItemToDo);
    }


    public class AsyncTaskGetItemFromBdd extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {

            AppDatabase database = Room.databaseBuilder(getApplicationContext(),
                    AppDatabase.class, "ToDoApp-db").build();
            listeToDo.setLesItems(convertFromBdd(database.ItemToDoDao().loadAllByIdList(listeToDo.getId())));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter.setMesListesItem(listeToDo.getLesItems());
            adapter.notifyDataSetChanged();
        }
    }

    public class AsyncTaskUpdateItemBdd extends AsyncTask<Integer, Void, Void> {
        @Override
        protected Void doInBackground(Integer... integers) {
            AppDatabase database = Room.databaseBuilder(getApplicationContext(),
                    AppDatabase.class, "ToDoApp-db").build();
            for (int indice : integers) {
                database.ItemToDoDao().updateItem(convertToBdd(listeToDo.getLesItems().get(indice)));
                listeToDo.setLesItems(convertFromBdd(database.ItemToDoDao().loadAllByIdList(listeToDo.getId())));
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter.setMesListesItem(ShowListActivity.this.listeToDo.getLesItems());
            adapter.notifyDataSetChanged();
        }
    }


}
