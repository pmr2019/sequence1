package com.example.todo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnOK = null;
    private ArrayAdapter<String> adapter ;
    private AutoCompleteTextView edtLogin;
    private SharedPreferences settings;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnOK = findViewById(R.id.buttonOK);
        btnOK.setOnClickListener(this);


        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line);
        edtLogin = (AutoCompleteTextView)findViewById(R.id.edtLogin);

        //calling settings
        settings = PreferenceManager.getDefaultSharedPreferences(this);

        //initializing Last ids
        ListeToDo.setLastIdList(settings.getInt("lastListId",0));
        ItemToDo.setLastIdItem(settings.getInt("lastItemId",0));

    }

    @Override
    protected void onStart() {
        super.onStart();
        //get login from preferences
        edtLogin.setText(settings.getString("login","").toString());
    }

    @Override // To add the menu
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override //Menu event listener
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_settings:
                Intent myInt = new Intent(MainActivity.this,SettingsActivity.class);
                startActivity(myInt);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonOK :
                adapter.add(edtLogin.getText().toString());
                edtLogin.setAdapter(adapter);

                //save login in preferences
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("login", edtLogin.getText().toString());
                editor.commit();

                //Create an intent to change activity
                Intent toSecondAct = new Intent(MainActivity.this,ChoixListActivity.class);
                Bundle data = new Bundle();
                data.putString("login",edtLogin.getText().toString());
                toSecondAct.putExtras(data);
                startActivity(toSecondAct);

                break;

        }

    }


    @Override
    protected void onStop() {
        super.onStop();
        //Save last ids in preferences
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("lastItemId",ItemToDo.getLastIdItem());
        editor.putInt("lastListId",ListeToDo.getLastIdList());
        editor.commit();
    }

    }

