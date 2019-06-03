package com.example.todolist;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends GenericActivity {

    private EditText mUserNameTE;

    // ------------------------------------------------------------------------------------------ //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialisation du champ texte contenant le nom de l'utilisateur
        mUserNameTE = findViewById(R.id.userNameEditText);
        mUserNameTE.setText(mUserName);
    }

    // ------------------------------------------------------------------------------------------ //
    public void toUserLists(View view) {

        String userName = mUserNameTE.getText().toString();
        
        if (userName.matches("")) {
            // en cas d'absence du nom utilisateur
            Toast.makeText(this, "Veuillez entrer votre nom", Toast.LENGTH_SHORT).show();
        }
        else {
            // en cas de présence du nom utilisateur
            mUserName = userName;
            
            // enregistrement du nom de l'utilisateur dans les préférences
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putString("userName", mUserName);
            editor.apply();
            
            createProfileIfNeeded();
            toLists();
        }
    }
}