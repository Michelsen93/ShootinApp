package com.example.ole_martin.shootinapp.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.provider.ContactsContract;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.Layout;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

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

public class TournamentActivity extends AppCompatActivity {
    private Map<String, Object> mStandplasses;
    private ArrayList<Map<String, Object>> mTeam;
    private Context mContext;
    private Database mDatabase;
    private Manager mManager;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView mNavigationView;
    private Spinner mSpinner;

    //TODO - Get team store in map

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tournament);
        mContext = this;
        mStandplasses = new HashMap<String, Object>();
        setUpCBL();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.tournament_layout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mSpinner = (Spinner) findViewById(R.id.person_spinner);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Sets up navigationDrawer with clicks
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

        getStandplasses();
        fillSpinner();
        //Load standplasses and let user pick one to register result
        //Let user have optaion to register result for team member



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void fillSpinner(){
        mTeam = findTeam();
        ArrayList<String> memberList = new ArrayList<String>();


        for(Map<String, Object> member : mTeam){
            memberList.add((String) member.get("firstName") + " " + member.get("lastName"));
        }
        ArrayAdapter<String> adp = new ArrayAdapter<String> (this, android.R.layout.simple_spinner_dropdown_item, memberList);
    }

    public void getStandplasses(){
        Map<String, Object> currentCompetition = getCurrentCompetition();
        ArrayList<Map<String, Object>> standplasses = (ArrayList<Map<String, Object>>) currentCompetition.get("standplasses");
        for(Map<String, Object> standplass :  standplasses){
            String asd ="Standplass|" + standplass.get("$ref");
            Document d = mDatabase.getExistingDocument(asd);
            Map<String, Object> curStandplass = d.getProperties();
            //Map values to show key = name, value = object
            mStandplasses.put((String) curStandplass.get("name"), curStandplass);
            Button button = new Button(this);
            button.setText((String) curStandplass.get("name"));
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    showRegisterResult(v);
                }
            });

            LinearLayout layout = (LinearLayout) findViewById(R.id.standplasses);
            layout.addView(button);




            // Dette skal være knapper som du kan trykke på. Med dette skal du kunne velge person, deretter registrere resultat
        }

        //load the to view

    }

    /*
    ColorDrawable buttonColor = (ColorDrawable) v.getBackground();
        if(buttonColor.getColor() == Color.GREEN){
            v.setBackgroundColor(Color.YELLOW);
        }
     */

    public void showRegisterResult(View v){
        //color selected green
        Button clicked = (Button)v;
        v.setBackgroundColor(Color.GREEN);
        //Save teamMember key to value
        TextView sName = (TextView) findViewById(R.id.nameOfStandplass);
        sName.setText(clicked.getText());
        //Save standplass key to value
        //Display register thing
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

    public Map<String, Object> getCurrentCompetition(){
        SharedPreferences sharedPref = mContext.getSharedPreferences(
                mContext.getString(R.string.preferences), Context.MODE_PRIVATE);
        String t_id = sharedPref.getString("tournament_id", "none");
        Document d = mDatabase.getExistingDocument(t_id);
        return d.getProperties();
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
    public ArrayList<Map<String,Object>> findTeam(){
        //TODO - This method has to get team id from preferences, then load it for mDatabase

        Map<String, Object> tournament = getCurrentCompetition();

        ArrayList<Object> teams = (ArrayList<Object>) tournament.get("teams");
        Map<String, Object> user = getCurrentUser();
        for(Object team : teams){
            ArrayList<Map<String, Object>> curTeam = (ArrayList<Map<String, Object>>) team;
            for(Map<String, Object> member : curTeam){
                if(member.get("mail").equals(user.get("mail"))){
                    return curTeam;
                }
            }

            return null;
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
}
