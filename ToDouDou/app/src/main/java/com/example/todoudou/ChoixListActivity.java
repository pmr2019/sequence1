package com.example.todoudou;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

public class ChoixListActivity extends AppCompatActivity implements ListAdapter.ActionListener {

    private ArrayList<ListeToDo> listToDo; // <---------

    private ArrayList<ListeToDo> newList(int n) {
        ArrayList<ListeToDo> data = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            data.add(new ListeToDo("Liste n°"+i));
        }
        return data;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choix_list);
        final RecyclerView recyclerView = findViewById(R.id.list_recycler_view);
        RecyclerView.LayoutManager layoutManager;
        Toolbar myToolbar = findViewById(R.id.toolbarList);
        setSupportActionBar(myToolbar);

        GestionDonnees ges = new GestionDonnees(this); // <---------
        listToDo = (ArrayList<ListeToDo>) ges.getListToDoProfilCourant(); // <---------



        //final ArrayList<ListeToDo> myDataset = newList(0);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        final ListAdapter myAdapter = new ListAdapter(listToDo,this);
        recyclerView.setAdapter(myAdapter);

        Button btn = findViewById(R.id.btnAjouterListe);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText;
                editText = findViewById(R.id.edtAjouterListe);
                String str = editText.getText().toString();
                editText.setText("");
                if (str.length() != 0) {
                    listToDo.add(new ListeToDo(str));
                    GestionDonnees ges = new GestionDonnees(getBaseContext()); // <---------
                    ges.ajoutListe(str); // <---------
                    myAdapter.notifyDataSetChanged();
                }
            }
        });

        findViewById(R.id.deleteList).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GestionDonnees ges = new GestionDonnees(getBaseContext()); // <---------
                ges.supprimeListes();
                finish();
            }
        });
    }



    public void onItemClicked(ListeToDo liste) {
        Log.i("fusion", "liste cliqué " + liste.toString()); // <---------

        GestionDonnees ges = new GestionDonnees(this); // <---------
        ProfilListeToDo profil = ges.getProfilEnCours(); // <---------
        int indice = profil.rechercherList(liste.getTitreListeToDo()); // <---------
//        int indice = listToDo.indexOf(liste); // <---------

        Log.i("fusion", "liste indice " + indice);

        Bundle myBdl = new Bundle();  // <---------
        myBdl.putInt("indiceList", indice); // <---------

        Intent intent = new Intent(ChoixListActivity.this, ShowListActivity.class);
        intent.putExtras(myBdl); // <---------
        startActivity(intent);
    }

}
