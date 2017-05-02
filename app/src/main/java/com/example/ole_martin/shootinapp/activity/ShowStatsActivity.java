package com.example.ole_martin.shootinapp.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.ole_martin.shootinapp.R;

public class ShowStatsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_stats);
    }

    public void showStats(String competitionNumber){
        //Get tournament with competitionNumber
        //Get scorecards from competition
        //Display person with total score
        //Allow on click to view detailed information
    }

    public void showDetailedStats(View view){
        //Display scorecard of selected person
    }
}
