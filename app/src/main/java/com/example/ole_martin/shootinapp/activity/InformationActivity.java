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
import android.widget.TextView;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;
import com.couchbase.lite.replicator.Replication;
import com.example.ole_martin.shootinapp.R;
import com.example.ole_martin.shootinapp.util.DAO;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class InformationActivity extends AppCompatActivity {

    private Database mDatabase;
    private Manager mManager;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView mNavigationView;
    private Context mContext;
    DAO mDao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        mContext = this;
        setUpCBL();



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
        startTheView();

    }


    //Dette funker ikke de må sikkert ikke være static
    public void startTheView(){
        Map<String, Object> tournament = getCurrentCompetition();
        Map<String, Object> team = findTeam();

        String date = (String) team.get("startTime");
        TextView tv2 = (TextView) findViewById(R.id.timeView);
        tv2.setText(date.toString());
        displayTeam(team);
        //display team
        //display standplasses

    }
    public void displayTeam(Map<String, Object> team){
        ArrayList<Object> members = (ArrayList<Object>)team.get("competitors");
        String memberNames = "";
        for(Object member : members) {
            Map<String, Object> curMember = (Map<String, Object>) member;
            String personId = "Person|" + curMember.get("$ref");
            Map<String, Object> person = mDatabase.getExistingDocument(personId).getProperties();
            memberNames += person.get("firstName") + " " + person.get("lastName") + " , ";
        }
        TextView tv = (TextView) findViewById(R.id.deltager_navn);
        tv.setText(memberNames);
    }

    public void goToRegisterActivity(View view){
        Intent intent = new Intent(this, TournamentActivity.class);
        startActivity(intent);
    }

    public Map<String, Object> getCurrentCompetition(){
        SharedPreferences sharedPref = mContext.getSharedPreferences(
                mContext.getString(R.string.preferences), Context.MODE_PRIVATE);
        String t_id = sharedPref.getString("tournament_id", "none");
        Document d = mDatabase.getExistingDocument(t_id);
        return d.getProperties();
    }
    public Map<String, Object> findTeam(){
        //TODO - Get user from preferences
        Map<String, Object> tournament = getCurrentCompetition();

        ArrayList<Object> teams = (ArrayList<Object>) tournament.get("teams");
        Map<String, Object> user = getCurrentUser();
        for(Object team : teams){
            Map<String, Object> currentTeam = (Map<String, Object>)team;
            String teamId = "Team|" + currentTeam.get("$ref");
            Map<String, Object> theTeam = mDatabase.getExistingDocument(teamId).getProperties();
            ArrayList<Object> members = (ArrayList<Object>)theTeam.get("competitors");
            for(Object member : members){
                Map<String, Object> curMember = (Map<String, Object>) member;
                String personId = "Person|" + curMember.get("$ref");
                Map<String, Object> person = mDatabase.getExistingDocument(personId).getProperties();
                if(person.get("mail").equals(user.get("mail"))){
                    //TODO - Save team to preferences
                    return theTeam;
                }
            }
        }

        return null;
    }

    public Map<String, Object> getCurrentUser(){
        SharedPreferences sharedPref = mContext.getSharedPreferences(
                mContext.getString(R.string.preferences), Context.MODE_PRIVATE);
        String user_id = sharedPref.getString("user", "none");
        Document d = mDatabase.getExistingDocument(user_id);
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
        String host = "http://" + mContext.getResources().getString(R.string.ip_address);
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
