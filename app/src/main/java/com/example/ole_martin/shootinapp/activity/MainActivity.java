package com.example.ole_martin.shootinapp.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.ole_martin.shootinapp.R;
import com.example.ole_martin.shootinapp.maybees.MyWifiActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Hei sindre!
        //Hei på dei din store sei!
        //Hei heiheihei
        // Hva skjer'a bagera?
        // Jeg vil å være med!!
        setContentView(R.layout.activity_main);
    }

    public void goToMyWifiActivity(View view){
        Intent intent = new Intent(this, MyWifiActivity.class);
        startActivity(intent);
    }
    public void goToLoginActivity(View view){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
