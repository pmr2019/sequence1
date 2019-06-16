package com.example.sujet_sequence_1;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.sujet_sequence_1.api.APIClient;
import com.example.sujet_sequence_1.api.APIInterface;
import com.example.sujet_sequence_1.models.ItemToDo;
import com.example.sujet_sequence_1.models.ListeToDo;
import com.example.sujet_sequence_1.models.ProfilListeToDo;

import java.util.ArrayList;

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
        try {
            appelAPIListeItem(recupPreference("hash"), recupPreference("baseUrl"));
        } catch (GetPreferenceException e) {
            e.printStackTrace();
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
        Button buttonNewToDo = findViewById(R.id.buttonNewToDo);
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
    public void onItemClick(int indice) throws GetPreferenceException {
        Log.d(CAT, String.valueOf(indice));
        final ItemToDo itemToDoClicked = listeToDo.getLesItems().get(indice);
        Log.i(CAT, "title clicked item : " + itemToDoClicked.getDescription());
        final String baseUrl = recupPreference("baseUrl");
        APIInterface api = APIClient.createService(APIInterface.class, baseUrl);
        final String hash = recupPreference("hash");
        Call<ListeToDo> call = api.checkItemAPI(hash, id, itemToDoClicked.getId(), (itemToDoClicked.isFait()) ? 0 : 1);
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
        adapter.notifyDataSetChanged(); //to ensure that display match data
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
        Log.i(CAT, "fin");


    }
}
