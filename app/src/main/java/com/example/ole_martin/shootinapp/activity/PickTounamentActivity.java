package com.example.ole_martin.shootinapp.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.ole_martin.shootinapp.R;

import java.util.ArrayList;

public class PickTounamentActivity extends AppCompatActivity {
    Spinner mSpinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_tounament);

        Toast.makeText(this, "Hello", Toast.LENGTH_LONG).show();
        mSpinner = (Spinner) findViewById(R.id.pick_tournament);
        ArrayList<String> tournamentList = new ArrayList<String>();
        //TODO - Load content to list
        tournamentList.add("t1");
        tournamentList.add("t2");
        ArrayAdapter<String> adp = new ArrayAdapter<String> (this, android.R.layout.simple_spinner_dropdown_item, tournamentList);
        mSpinner.setAdapter(adp);
    }


    public void pick(View view){
        /**
         * if item selected:
         * get item from spinner
         * load tournament with the users team etc.
         * go to next activity
         */

        String selected = mSpinner.getSelectedItem().toString();

        //Load tournament to phone

        Intent intent = new Intent(this, TournamentActivity.class);
        startActivity(intent);
    }
}
