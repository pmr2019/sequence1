package com.example.todo;

import android.content.ClipData;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.preference.PreferenceManager;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

public class ShowListActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ShowListActivity";

    //Var
    private ArrayList<ItemToDo> mItem = new ArrayList<ItemToDo>();
    private ProfilListeToDo profil;
    private int iList;

    //Widgets
    private EditText edtItemDesc;
    private Button btnAddItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_list);

        //Getting the data of the intent
        Bundle data = this.getIntent().getExtras();
        assert data != null;
        iList = Integer.parseInt(data.getString("iList", "0"));
        Log.d(TAG, "iList : " + iList);

        //Init variables
        profil = getProfil();
        mItem.addAll(profil.getMesListeToDo().get(iList).getLesItems());

        //Init widgets
        edtItemDesc = findViewById(R.id.edtItemDesc);
        btnAddItem = findViewById(R.id.btnAddItem);
        btnAddItem.setOnClickListener(this);

        //Init reclyclerView
        initRecyclerView();
        refreshRecyclerView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAddItem:
                Log.d(TAG, "onClick: clicked on : add item");
                String desc = edtItemDesc.getText().toString();
                if (!desc.isEmpty()) {
                    addItemToDo(desc);
                }
        }
    }

    private void initRecyclerView(){
        Log.d(TAG, "initRecyclerView: init RecyclerView");
        RecyclerView recyclerView = findViewById(R.id.reclycler_view_item);
        RecyclerViewAdapterItem adapter = new RecyclerViewAdapterItem(this, mItem);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void refreshRecyclerView(){
        mItem.clear();
        mItem.addAll(profil.getMesListeToDo().get(iList).getLesItems());
        Log.d(TAG, "refreshRecyclerView: "+mItem.toString());
        RecyclerView recyclerView = findViewById(R.id.reclycler_view_item);
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    private void saveData() {
        Log.d(TAG, "saveData: saving.");

        Gson gson = new GsonBuilder()
                .serializeNulls()
                .disableHtmlEscaping()
                .setPrettyPrinting()
                .create();

        String filename = profil.getLogin() + ".json";
        Log.d(TAG, "saveData: " + filename);
        String fileContents = gson.toJson(profil);
        FileWriter fileWriter;
        try {
            fileWriter = new FileWriter(getFilesDir() + "/" + filename);
            fileWriter.write(fileContents);
            fileWriter.close();
        } catch (Exception e) {
            Log.e(TAG, "saveData: error while writing save file : ", e);
        }
    }

    /**
     * Get the ListeToDo number id from the pseudo active in the preferences.
     * Warning : if the user can access to the preferences in other activities
     * than MainActivity, then the user can modify the pseudo used and the application
     * will no longer work.
     * @param
     * @return ProfilListeToDo
     */
    private ProfilListeToDo getProfil(){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String pseudo = settings.getString("pseudo","");

        Gson gson = new GsonBuilder()
                .serializeNulls()
                .disableHtmlEscaping()
                .setPrettyPrinting()
                .create();

        String filename = pseudo + ".json";
        String fileContents = "";
        FileReader fileReader;
        try {
            fileReader = new FileReader(getFilesDir() + "/" + filename);
            BufferedReader reader = new BufferedReader(fileReader);
            String line;
            while ((line = reader.readLine()) != null) {
                fileContents += line;
            }
            reader.close();
            fileReader.close();
        } catch (FileNotFoundException e) {
            Log.i(TAG, "getListeToDo: user unknown.");
        } catch (Exception e) {
            Log.e(TAG, "getListeToDo: error while reading save file : ", e);
        }
        profil = gson.fromJson(fileContents, ProfilListeToDo.class);
        return profil;
    }

    /**
     * Return the ListeToDo(i) from the profil of this class.
     * @param i
     * @return ListeToDo
     */
    private ListeToDo getListe(int i){
        if (profil!=null)
            return profil.getMesListeToDo().get(i);
        else
            return null;
    }

    private void addItemToDo(String desc){
        ItemToDo item = new ItemToDo(desc);
        profil.getMesListeToDo().get(iList).ajouteItem(item);
        saveData();
        Log.d(TAG, "addItemToDo: item ajouté : "+item.toString());
        refreshRecyclerView();
    }

    public void deleteItem(int i){
        profil.getMesListeToDo().get(iList).getLesItems().remove(i);
        saveData();
        refreshRecyclerView();
    }

    /**
     * Check the item if it is not checked, or uncheck the item otherwise.
     * Then, it refresh the recyclerview.
     * @param i
     */
    public void checkItem(int i, boolean check){
        ItemToDo item = profil.getMesListeToDo().get(iList).getLesItems().get(i);
        item.setFait(check);
        saveData();
        refreshRecyclerView();
    }

}
