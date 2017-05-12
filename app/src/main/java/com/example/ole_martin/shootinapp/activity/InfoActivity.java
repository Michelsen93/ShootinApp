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
import android.widget.Toast;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Manager;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.android.AndroidContext;
import com.example.ole_martin.shootinapp.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class InfoActivity extends AppCompatActivity {
    Manager mManager = null;
    Database mDatabase = null;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView mNavigationView;
    Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        setUpCBL();
        loadView();

        mContext = this;
        mDrawerLayout = (DrawerLayout) findViewById(R.id.info_layout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.nav_account) {
                    Intent a = new Intent(getBaseContext(), MyAccountActivity.class);
                    startActivity(a);

                } else if (id == R.id.nav_settings) {
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

        Map<String, Object> currentTournament = findCurrentTournament();
    }

    public Map<String, Object> findCurrentTournament(){
        SharedPreferences sharedPref = mContext.getSharedPreferences(
                getString(R.string.preferences), Context.MODE_PRIVATE);
        String t_id = sharedPref.getString("tournament_id", "none");
        Document d = mDatabase.getExistingDocument(t_id);
        return d.getProperties();
    }

    public void loadView(){
        Map<String, Object> tournament = findCurrentTournament();
        //TODO - Find group of logged in guy
        Map<String, Object> team = findGroup();
    }
    public Map<String, Object> findTeam(Map<String, Object> tournament){
        //TODO - Get user from preferences
        String user = "Find the user from preferences";
        Map<String, Object> teams = (Map<String, Object>) tournament.get("teams");
        for(Object team : teams.values()){

        }

    }

    public void setUpCBL(){
        try{
            mManager = new Manager(new AndroidContext(this), Manager.DEFAULT_OPTIONS);
            mDatabase = mManager.getDatabase(getResources().getString(R.string.DB_NAME));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }
}
