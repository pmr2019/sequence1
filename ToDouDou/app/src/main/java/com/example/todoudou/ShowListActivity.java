package com.example.todoudou;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

public class ShowListActivity extends AppCompatActivity implements ItemAdapter.ActionListener {

    private int indListCourante; // <---------
    private ArrayList<ItemToDo> itemToDo; // <---------


    private ArrayList<ItemToDo> newItemList(int n) {
        ArrayList<ItemToDo> data = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            data.add(new ItemToDo("Item n°"+i,false));
        }

        return data;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_list);
        final RecyclerView recyclerView = findViewById(R.id.item_recycler_view);
        RecyclerView.LayoutManager layoutManager;

        Bundle b = this.getIntent().getExtras(); // <---------
        indListCourante = b.getInt("indiceList"); // <---------

        Log.i("fusion", "ind list courante " + indListCourante);

        GestionDonnees ges = new GestionDonnees(this); // <---------
        itemToDo = (ArrayList<ItemToDo>) ges.getListItemCourant(indListCourante);// <---------

       // final ArrayList<ItemToDo> myDataset = newItemList(n);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        final ItemAdapter myAdapter = new ItemAdapter(itemToDo,this);
        recyclerView.setAdapter(myAdapter);

        Button btn = findViewById(R.id.btnAjouterTache);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText;
                editText = findViewById(R.id.edtAjouterTache);
                String str = editText.getText().toString();
                editText.setText("");
                if (str.length() != 0) {
                    itemToDo.add(new ItemToDo(str, false));
                    GestionDonnees ges = new GestionDonnees(getBaseContext()); // <---------
                    ges.ajoutItem(indListCourante, str); // <---------
                    myAdapter.notifyDataSetChanged();
                }
            }
        });

        findViewById(R.id.deleteItem).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GestionDonnees ges = new GestionDonnees(getBaseContext()); // <---------
                ges.supprimeItems(indListCourante);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_action_bar, menu);
        return true;
    }

    // https://developer.android.com/training/appbar/actions.html
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                //Lors du clic sur le bouton “Préférences” de ce menu, une activité
                //SettingsActivity s’affiche
                Intent toSettings = new Intent(this,SettingsActivity.class);
                startActivity(toSettings);
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    public void onItemClicked(ItemToDo item) {
        Log.i("fusion", "item cliqué " + item.toString()); // <---------

        GestionDonnees ges = new GestionDonnees(getBaseContext()); // <---------
        ges.faireItem(indListCourante, item.getDescription(), !item.getFait()); // <---------
        item.setFait(!item.getFait());
    }


}
