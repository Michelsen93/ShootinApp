package com.example.ole_martin.shootinapp.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.ole_martin.shootinapp.R;

import java.util.ArrayList;

public class PickTounamentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_tounament);

        Spinner spinner = (Spinner) findViewById(R.id.pick_tournament);
        ArrayList<String> tournamentList = new ArrayList<String>();
        //TODO - Load content to list
        tournamentList.add("t1");
        tournamentList.add("t2");
        ArrayAdapter<String> adp = new ArrayAdapter<String> (this, android.R.layout.simple_spinner_dropdown_item, tournamentList);
        spinner.setAdapter(adp);
    }


    public void pick(View view){
        /**
         * if item selected:
         * get item from spinner
         * save it somewhere to start working with
         * go to next activity
         */
    }
}
