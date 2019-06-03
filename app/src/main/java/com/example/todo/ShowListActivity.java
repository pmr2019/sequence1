package com.example.todo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

public class ShowListActivity extends AppCompatActivity implements ItemAdapterItem.ActionListener {

    private EditText edtNewItem;
    private Button btnAddItem;
    private String login;
    private Integer idList;
    private String profilsJson;
    private ProfilToDoList profil;
    private ItemAdapterItem adapterlist;
    private RecyclerView recyclerView;
    private HashMap<String, ProfilToDoList> profils;
    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_list);

        btnAddItem = (Button) findViewById(R.id.btnAddItem);
        edtNewItem = (EditText) findViewById(R.id.edtNewItem);

        //Recovering the bundle
        Bundle b = this.getIntent().getExtras();
        login = b.getString("login");
        idList = b.getInt("idList");

        //Recover the profiles from preferences
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        profilsJson = settings.getString("profils", "");

        //Deserialization
        Gson gson = new Gson();
        Type type = new TypeToken<HashMap<String, ProfilToDoList>>(){}.getType();
        profils = gson.fromJson(profilsJson, type);
        profil=profils.get(login);


        //adapter parameters
        adapterlist = new ItemAdapterItem(newItemList(profil,idList),this);
        final RecyclerView recyclerView = findViewById(R.id.items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapterlist);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));
        recyclerView.addItemDecoration((new DividerItemDecoration(this, LinearLayout.VERTICAL)));

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallbackItem(adapterlist));
        itemTouchHelper.attachToRecyclerView(recyclerView);

        // Click listener for OK button
        btnAddItem.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                String itemTitle=edtNewItem.getText().toString() ;
                if (itemTitle.equals("")){
                    Toast myToast = Toast.makeText(getApplicationContext()," Please name your item !", Toast.LENGTH_LONG);
                    myToast.show();
                }
                else {
                    //create a new item
                    ItemToDo newItem = new ItemToDo(edtNewItem.getText().toString());
                    //Add it
                    adapterlist.AddToList(newItem,false);
                    //save
                    save();

                }
            }
        });

    }


    //Generate a list of items of the current profil and list
    private List<ItemToDo> newItemList(ProfilToDoList profil, Integer idList) {
        List<ItemToDo> result= null;
        for (ListeToDo list :profil.getMesListeToDo()){
            if (list.getIdListe()==idList)
                result=list.getLesItems();

        }
        return result;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item_list clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


//Item clicked
    public void onCheckBoxClicked(Integer idItem) {

        List<ListeToDo> listOfLists = profil.getMesListeToDo();
        int indexList =0;

        for (int i = 0; i < listOfLists.size(); i++) {
            if (listOfLists.get(i).getIdListe() == idList) {
                indexList=i;
            }
        }

        ListeToDo modifiedList = listOfLists.get(indexList);

        List<ItemToDo> listeOfItems = modifiedList.getLesItems();

        int indexItem =0;
        for (int i = 0; i < listeOfItems.size(); i++) {
            if (listeOfItems.get(i).getIdItem() == idItem) {
                indexItem=i;
            }
        }

        ItemToDo modifiedItem = listeOfItems.get(indexItem);
        modifiedItem.setFait(!modifiedItem.getFait());

        save();

    }


    //Deleting
    public void onItemRemoved(){
        save();
    }


    //Undo delete
    public void onUndoDelete(){
        save();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Saving last ids
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("lastItemId",ItemToDo.getLastIdItem());
        editor.putInt("lastListId",ListeToDo.getLastIdList());
        editor.commit();
    }

    public void save(){
        Gson gson=new GsonBuilder().enableComplexMapKeySerialization().create();
        profils.remove(login);
        profils.put(login,profil);
        profilsJson = gson.toJson(profils);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("profils",profilsJson);
        editor.commit();

    }

}
