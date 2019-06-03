package com.example.todo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

public class ChoixListActivity extends AppCompatActivity implements ItemAdapterList.ActionListener{
    private EditText edtNewList;
    private Button btnAddList;
    private String login;
    private ProfilToDoList profil;
    private ItemAdapterList adapterlist;
    private RecyclerView recyclerView;
    private HashMap<String,ProfilToDoList> profils;
    private SharedPreferences settings;
    private String profilsJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_choix_list);

        btnAddList = (Button) findViewById(R.id.btnAddList);
        edtNewList = (EditText) findViewById(R.id.edtNewList);

        //Recovering the login from bundle
        Bundle b = this.getIntent().getExtras();
        login = b.getString("login");

        //Recovering the preferences
        settings = PreferenceManager.getDefaultSharedPreferences(this);

        //Recovering the profiles
        profilsJson = settings.getString("profils","");

        //Deserialization
        Gson gson = new Gson();
        Type type = new TypeToken<HashMap<String,ProfilToDoList>>(){}.getType();
        profils = gson.fromJson(profilsJson,type);


        //Verify if the login exists
        if (null != profils) {
            if (!profils.containsKey(login)) {
                profil = new ProfilToDoList(login);
                profils.put(login, profil);
            }
            else{
                profil=profils.get(login);
            }
        }
        else{
            //create new profile
            profils=new HashMap<String, ProfilToDoList>();
            profil=new ProfilToDoList(login);
            profils.put(login,profil);
        }


        //Serialization and Saving profils in preferences
        gson=new GsonBuilder().enableComplexMapKeySerialization().create();
        profilsJson = gson.toJson(profils);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("profils",profilsJson);
        editor.commit();


        //adapter parameters
        adapterlist = new ItemAdapterList(newItemList(profil),this);
        final RecyclerView recyclerView = findViewById(R.id.lists);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapterlist);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));
        recyclerView.addItemDecoration((new DividerItemDecoration(this, LinearLayout.VERTICAL)));


        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallbackList(adapterlist));
        itemTouchHelper.attachToRecyclerView(recyclerView);


        // Click listener for Add button
        btnAddList.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String listTitle = edtNewList.getText().toString();

                if (listTitle.equals("")) {
                    Toast myToast = Toast.makeText(getApplicationContext()," Please name your list !",Toast.LENGTH_LONG);
                    myToast.show();
                }
                else {
                    //create a new list
                    ListeToDo newListeToDo = new ListeToDo(edtNewList.getText().toString());
                    //add it
                    adapterlist.AddToList(newListeToDo);
                    save();
                }

            }


        });

    }


    //Generate a List of ListeToDo titles of the current profil
    private List<ListeToDo> newItemList(ProfilToDoList profil) {
        return profil.getMesListeToDo();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    //List clicked
    public void onListClicked(Integer idListe) {

        //Create an intent to move to ShowListActivity
        Intent toSecondAct = new Intent(ChoixListActivity.this,ShowListActivity.class);
        Bundle data_list = new Bundle();
        data_list.putString("login",login);
        data_list.putInt("idList",idListe);
        toSecondAct.putExtras(data_list);
        startActivity(toSecondAct);
    }


    //Deleting
    public void onListRemoved(){
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
