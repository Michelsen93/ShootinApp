package com.example.ole_martin.shootinapp.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ole_martin.shootinapp.R;
import com.example.ole_martin.shootinapp.util.Checker;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //TODO - if logged in, skip checkIfLoggedIn
    }


    public void doLogIn(View view){
        String username = ((EditText)findViewById(R.id.username_input)).getText().toString();
        String password = ((EditText)findViewById(R.id.password_input)).getText().toString();;
        Toast.makeText(this, username, Toast.LENGTH_LONG).show();
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
