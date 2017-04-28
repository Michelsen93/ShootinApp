package com.example.ole_martin.shootinapp.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.ole_martin.shootinapp.R;
import com.example.ole_martin.shootinapp.util.Checker;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }
    public void logIn(String userName, String password){
        //TODO - lag log inn sjekk og sånt
        if(Checker.checkLogin(userName, password)){
            // start activity
            Intent intent = new Intent(this, PickTounamentActivity.class);
            startActivity(intent);
        }else{
            //display error message, try again
        }

    }

}
