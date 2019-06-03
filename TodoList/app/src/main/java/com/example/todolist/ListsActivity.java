package com.example.todolist;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.adapter.ListsAdapter;
import com.example.todolist.data.TodoList;

import java.util.List;

public class ListsActivity extends GenericActivity implements ListsAdapter.ActionListener {

    private EditText mNewListTE;
    private ListsAdapter mListsAdapter;
    private List<TodoList> mListTodoList;
    
    // ------------------------------------------------------------------------------------------ //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lists);

        mNewListTE = findViewById(R.id.newListTE);

        // récupération des listes de l'utilisateur
        mListTodoList = mUserProfile.getLists();

        mListsAdapter = new ListsAdapter(mListTodoList, this);

        // création d'un recycler view pour afficher les listes
        RecyclerView recyclerView = findViewById(R.id.choiceListRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));
        recyclerView.setAdapter(mListsAdapter);
    }

    // ------------------------------------------------------------------------------------------ //
    @Override
    protected void onStart() {
        super.onStart();

        String userName = mSharedPreferences.getString("userName","");

        if(userName != null) {

            if (userName.matches("")) {
                // en cas d'absence de nom utilisateur
                toMain();
            }

            if (!userName.equals(mUserName)) {
                // en cas de changement du nom utilisateur
                mUserName = userName;
                createProfileIfNeeded();

                // récupération des listes de l'utilisateur
                mUserProfile = readJSON(mSharedPreferences.getString(mUserName, ""));
                mListTodoList.clear();
                mListTodoList.addAll(mUserProfile.getLists());
                mListsAdapter.notifyDataSetChanged();
            }
        }
    }

    // ------------------------------------------------------------------------------------------ //
    public void addList(View view) {
        String listTitle = mNewListTE.getText().toString();
        boolean alreadyExists = false;

        // recherche d'une liste ayant le même titre que celui à ajouter
        for (TodoList todoList: mListTodoList) {
            alreadyExists = alreadyExists || todoList.getTitle().matches(listTitle);
        }

        if (listTitle.matches("")) {
            // en cas de titre vide
            Toast.makeText(this, "Veuillez ajouter un nom à votre todo liste", Toast.LENGTH_SHORT).show();
        }
        else if (alreadyExists) {
            // en cas d'une liste ayant le même titre
            Toast.makeText(this, "Une todo liste ayant le même nom existe déjà", Toast.LENGTH_SHORT).show();
        }
        else {
            // ajout de la liste dans le profil
            mUserProfile.addList(new TodoList(listTitle));
            updateProfile(mUserProfile);

            toItems(listTitle);
        }
    }

    // ------------------------------------------------------------------------------------------ //
    @Override
    public void onItemClicked(String listTitle) {
        toItems(listTitle);
    }

    // ------------------------------------------------------------------------------------------ //
    @Override
    public void onDeleteClicked(final int position) {

        // création d'une fenêtre de dialogue pour confirmer la suppression d'une to-do liste
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        
        builder.setTitle("Suppression d'une todo liste");
        builder.setMessage("Êtes-vous sûr de supprimer cette todo liste ?");
        builder.setNegativeButton("Non", null);
        builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                
                mUserProfile.removeList(mListTodoList.get(position));
                updateProfile(mUserProfile);

                mListsAdapter.notifyItemRemoved(position);
            }
        });
        
        builder.show();
    }
}
