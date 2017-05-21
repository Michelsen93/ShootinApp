package com.example.ole_martin.shootinapp.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.couchbase.lite.Document;
import com.example.ole_martin.shootinapp.R;
import com.example.ole_martin.shootinapp.util.Checker;
import com.example.ole_martin.shootinapp.util.DAO;

import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        DAO dao = new DAO(this);
        if(getCurrentUser() != "none"){
            Intent intent = new Intent(this, PickTounamentActivity.class);
            startActivity(intent);
        }
    }


    public String getCurrentUser(){
        SharedPreferences sharedPref = this.getSharedPreferences(
                this.getString(R.string.preferences), Context.MODE_PRIVATE);
        return sharedPref.getString("user", "none");

    }

    public void doLogIn(View view){
        String username = ((EditText)findViewById(R.id.username_input)).getText().toString();
        String password = ((EditText)findViewById(R.id.password_input)).getText().toString();;

        logIn(username, password);
    }

    public void logIn(String userName, String password){
        //TODO - lag log inn sjekk og sånt
        if(Checker.checkLogin(userName, password, this)){
            // start activity
            Intent intent = new Intent(this, PickTounamentActivity.class);
            startActivity(intent);
        }else{
            Toast.makeText(this, "Fant ikke brukeren prøv igjen...", Toast.LENGTH_LONG).show();
        }

    }

}
