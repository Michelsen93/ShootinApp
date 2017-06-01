package com.example.ole_martin.shootinapp.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;
import com.couchbase.lite.replicator.Replication;
import com.example.ole_martin.shootinapp.R;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ShowStatsActivity extends AppCompatActivity {
    private Context mContext;
    private Database mDatabase;
    private Manager mManager;
    RecyclerView mRecyclerView;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView mNavigationView;
    public Map<String, Object> currentCompetition;
    public ArrayList<Map<String, Object>> scorecards;
    HashMap<Integer, Map<String, Object>> index;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_stats);
        index = new HashMap<Integer, Map<String, Object>>();
        mContext = this;
        setUpCBL();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.show_stats_layout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        showStats();
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



    }

    public void showStats(){
        currentCompetition = getCurrentCompetition();
        scorecards = (ArrayList<Map<String, Object>>)currentCompetition.get("scorecards");
        //iterate scorecards
        ArrayList<String> items = new ArrayList<String>();
        //get competitor, add to string, get resluts add sum to string, add standplasses registered
        for(Map<String, Object> scorecard: scorecards){
            int i = 0;
            String id = "Scorecard|" + scorecard.get("$ref");
            Document scoreardDocument = mDatabase.getExistingDocument(id);
            Map<String, Object> scoreCardProperties = scoreardDocument.getProperties();
            String personId = (String)((Map<String, Object>) scoreCardProperties.get("competitor")).get("$ref");
            int hits = 0;
            int figures = 0;
            int bullseyes = 0;
            ArrayList<Map<String, Object>> results = (ArrayList<Map<String, Object>>) scoreCardProperties.get("results");
            for(Map<String, Object> result : results){
                hits += (int) result.get("hits");
                figures += (int) result.get("figures");
                bullseyes += (int) result.get("bullseyes");
            }
            Map<String, Object> person = mDatabase.getExistingDocument(personId).getProperties();
            String overview = (String) person.get("firstName") + " " + person.get("lastName") +
                    " : " + hits + " | " + figures + " | " + bullseyes;
            items.add(overview);
            index.put(index.size(), scoreCardProperties);

        }
        String[] compResults = new String[items.size()];
        for(int i = 0; i<items.size(); i++){
            compResults[i] = items.get(i);
        }
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new Adapter(this, compResults, index));
        //Display person with total score in a card. When clicked, do stuff
        //Allow on click to view detailed information
    }

    public void showDetailedStats(View view){
        //Display scorecard of selected person
        ListView listView = new ListView(this);
        String[] items;

    }

















    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public Map<String, Object> getCurrentCompetition(){
        SharedPreferences sharedPref = mContext.getSharedPreferences(
                mContext.getString(R.string.preferences), Context.MODE_PRIVATE);
        String t_id = sharedPref.getString("tournament_id", "none");
        Document d = mDatabase.getExistingDocument(t_id);
        return d.getProperties();
    }
    public void setUpCBL(){

        try{
            mManager = new Manager(new AndroidContext(mContext), Manager.DEFAULT_OPTIONS);
            mDatabase = mManager.getDatabase(mContext.getResources().getString(R.string.DB_NAME));
            startPullReplication();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

    }
    public void startPullReplication() throws CouchbaseLiteException {
        Replication pull = mDatabase.createPullReplication(this.createSyncURL(false));
        pull.start();
    }
    public URL createSyncURL(boolean isEncrypted){
        URL syncURL = null;
        String host = "http://" + getResources().getString(R.string.ip_address);
        String port = "4984";
        String dbName = mContext.getResources().getString(R.string.DB_NAME);
        try {
            syncURL = new URL(host + ":" + port + "/" + dbName);
        } catch (MalformedURLException me) {
            me.printStackTrace();
        }
        return syncURL;
    }
}
