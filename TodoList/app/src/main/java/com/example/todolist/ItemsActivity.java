package com.example.todolist;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.example.todolist.adapter.ItemsAdapter;
import com.example.todolist.data.Item;
import com.example.todolist.data.TodoList;
import com.example.todolist.data.UserProfile;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.List;

public class ItemsActivity extends GenericActivity implements ItemsAdapter.ActionListener {

    private EditText mNewItemTE;
    private ItemsAdapter mItemsAdapter;
    private List<Item> mListItem;
    private TodoList mTodoList;

    // ------------------------------------------------------------------------------------------ //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);

        mNewItemTE = findViewById(R.id.newItemTE);

        // récupération des items de la to-do liste
        Intent intent = getIntent();
        mTodoList = mUserProfile.getList(intent.getStringExtra("list"));
        mListItem = mTodoList.getItems();

        mItemsAdapter = new ItemsAdapter(mListItem, this);

        // création d'un recycler view pour afficher les items
        RecyclerView recyclerView = findViewById(R.id.showListRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));
        recyclerView.setAdapter(mItemsAdapter);
    }

    // ------------------------------------------------------------------------------------------ //
    @Override
    protected void onStart() {
        super.onStart();

        String userName = mSharedPreferences.getString("userName", "");

        if (userName != null) {

            if (userName.matches("")) {
                // en cas d'absence du nom utilisateur
                toMain();
            }

            if (!userName.equals(mUserName)) {
                // en cas de changement du nom utilisateur
                mUserName = userName;
                createProfileIfNeeded();
                toLists();
            }
        }
    }

    // ------------------------------------------------------------------------------------------ //
    public void addItem(View view) {
        String itemDescription = mNewItemTE.getText().toString();
        boolean alreadyExists = false;

        // recherche d'un item ayant la même description que celle à ajouter
        for (Item item: mListItem) {
            alreadyExists = alreadyExists || item.getDescription().matches(itemDescription);
        }

        if (itemDescription.matches("")) {
            // en cas de description vide
            Toast.makeText(this, "Veuillez ajouter une description à votre item", Toast.LENGTH_SHORT).show();
        }
        else if (alreadyExists) {
            // en cas d'un item ayant la même description
            Toast.makeText(this, "Un item ayant le même nom existe déjà", Toast.LENGTH_SHORT).show();
        }
        else {
            mNewItemTE.setText("");

            // ajout de l'item dans la liste
            mTodoList.addItem(new Item(itemDescription));
            updateProfile(mUserProfile);

            mListItem = mTodoList.getItems();
            mItemsAdapter.notifyItemInserted(mListItem.size() - 1);
        }
    }

    // ------------------------------------------------------------------------------------------ //
    @Override
    public void onCheckedClicked(final int position, final boolean isChecked) {
        mListItem.get(position).setDone(isChecked);
        updateProfile(mUserProfile);
    }

    // ------------------------------------------------------------------------------------------ //
    @Override
    public void onDeleteClicked(final int position) {

        // création d'une fenêtre de dialogue pour confirmer la suppression d'un item
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Suppression d'un item");
        builder.setMessage("Êtes-vous sûr de supprimer cet item ?");
        builder.setNegativeButton("Non", null);
        builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                mTodoList.removeItem(mListItem.get(position));
                updateProfile(mUserProfile);

                mItemsAdapter.notifyItemRemoved(position);
            }
        });

        builder.show();
    }
}
