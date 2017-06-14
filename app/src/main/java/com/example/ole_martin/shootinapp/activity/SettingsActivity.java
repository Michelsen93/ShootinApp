package com.example.ole_martin.shootinapp.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.ole_martin.shootinapp.R;

public class SettingsActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView mNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.settings_layout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.nav_settings) {
                    Intent b = new Intent(getBaseContext(), SettingsActivity.class);
                    startActivity(b);

                } else if(id == R.id.nav_live) {
                    Intent c = new Intent(getBaseContext(), ShowStatsActivity.class);
                    startActivity(c);

                } else if (id == R.id.nav_registration){
                    //allready on the activity no action needed?
                    Intent c = new Intent(getBaseContext(), TournamentActivity.class);
                    startActivity(c);

                }
                return true;
            }
        } );


        //Load standplasses and let user pick one to register result
        //Let user have optaion to register result for team member


    }

    public void doLogOut(View v){
        SharedPreferences sharedPref = this.getSharedPreferences(
                this.getString(R.string.preferences), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove("user");
        editor.apply();
        Toast.makeText(this, "logget ut", Toast.LENGTH_LONG).show();
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
