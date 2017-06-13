package com.example.ole_martin.shootinapp.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.NavigationView;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;


import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Manager;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.android.AndroidContext;
import com.couchbase.lite.replicator.Replication;
import com.example.ole_martin.shootinapp.R;
import com.example.ole_martin.shootinapp.util.Checker;
import com.example.ole_martin.shootinapp.util.DAO;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

public class InformationActivity extends AppCompatActivity {

    private Database mDatabase;
    private Manager mManager;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView mNavigationView;
    private Context mContext;
    private boolean isCurrentlyActive;
    ArrayList<String> mWeaponClasses;
    ArrayList<String> mWeaponGroups;
    DAO mDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        mContext = this;
        setUpCBL();
        isCurrentlyActive = false;

        mDrawerLayout = (DrawerLayout) findViewById(R.id.info_layout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.nav_settings) {
                    Intent b = new Intent(getBaseContext(), SettingsActivity.class);
                    startActivity(b);

                } else if (id == R.id.nav_live) {
                    Intent c = new Intent(getBaseContext(), ShowStatsActivity.class);
                    startActivity(c);

                } else if (id == R.id.nav_registration) {
                    Intent c = new Intent(getBaseContext(), TournamentActivity.class);
                    startActivity(c);

                }
                return true;
            }
        });

        //TODO - If active scorecards, only display info
        startTheView();

    }


    //Dette funker ikke de må sikkert ikke være static
    public void startTheView() {
        Map<String, Object> tournament = getCurrentCompetition();
        Map<String, Object> team = findTeam();

        String date = (String) team.get("startTime");
        TextView tv2 = (TextView) findViewById(R.id.timeView);
        tv2.setText(date.toString());


        displayTeam(team);
        //display team
        //display standplasses

    }


    public void displayTeam(Map<String, Object> team) {
        mWeaponClasses = getAllWeaponClasses();
        mWeaponGroups = getAllWeaponGroups();
        ArrayAdapter<String> wca = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, mWeaponClasses);
        ArrayAdapter<String> wga = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, mWeaponGroups);



        TableLayout memberList = (TableLayout) findViewById(R.id.member_form);
        ArrayList<Object> members = (ArrayList<Object>) team.get("competitors");

        for (Object member : members) {
            Map<String, Object> curMember = (Map<String, Object>) member;
            String personId = "Person|" + curMember.get("$ref");
            Map<String, Object> person = mDatabase.getExistingDocument(personId).getProperties();
            ArrayList<Map<String, Object>> scorecardsOfPerson = (ArrayList<Map<String, Object>>) person.get("scoreCards");
            for (Map<String, Object> scorecardRef : scorecardsOfPerson) {
                String scorecardId = "Scorecard|" + scorecardRef.get("$ref");
                Document scoreDoc = mDatabase.getExistingDocument(scorecardId);
                if (scoreDoc != null) {
                    Map<String, Object> scoreProperties = scoreDoc.getProperties();
                    if ((boolean) scoreProperties.get("completed") == false) {
                        isCurrentlyActive = true;
                        break;
                    }
                }
            }
        }
        if (!isCurrentlyActive) {

            for (Object member : members) {
                Map<String, Object> curMember = (Map<String, Object>) member;
                String personId = "Person|" + curMember.get("$ref");
                Map<String, Object> person = mDatabase.getExistingDocument(personId).getProperties();
                String name = person.get("firstName") + " " + person.get("lastName");

                TextView memberName = new TextView(this);
                Spinner klasseSpinner = new Spinner(this);
                Spinner gruppeSpinner = new Spinner(this);

                memberName.setText(name);

                memberName.setHint(personId);

                TableRow memberLine = new TableRow(this);

                memberName.setWidth(400);
                memberLine.addView(memberName);
                memberLine.addView(klasseSpinner);
                memberLine.addView(gruppeSpinner);
                memberList.addView(memberLine);
                klasseSpinner.setAdapter(wca);
                gruppeSpinner.setAdapter(wga);

            }
        } else{
            TableLayout tl = (TableLayout) findViewById(R.id.member_form);
            tl.setVisibility(View.GONE);
            Button button = (Button) findViewById(R.id.registerButton);
            button.setText("Fortsett registrering");
            TextView tv = (TextView) findViewById(R.id.deltagere_tag);
            tv.setVisibility(View.GONE);
            Toast.makeText(this, "Noen på laget er ikke ferdig.. fullfør registrering", Toast.LENGTH_LONG).show();
        }


    }

    public void goToRegisterActivity(View view) {
        //Fetch filled data, Create new scorecard, add to db
        if(!isCurrentlyActive){
            createScorecards();
        }

        Intent intent = new Intent(this, TournamentActivity.class);
        startActivity(intent);
    }

    public void createScorecards() {
        TableLayout tl = (TableLayout) findViewById(R.id.member_form);
        for (int i = 1; i < tl.getChildCount(); i++) {
            View child = tl.getChildAt(i);
            if (child instanceof TableRow) {
                TableRow row = (TableRow) child;
                TextView member = (TextView) ((TableRow) child).getChildAt(0);
                Spinner klasseSpinner = (Spinner) ((TableRow) child).getChildAt(1);
                Spinner groupSinner = (Spinner) ((TableRow) child).getChildAt(2);
                String memberId = (String) member.getHint();
                Map<String, Object> thePerson = mDatabase.getExistingDocument(memberId).getProperties();
                String scorecardPrefix = "Scorecard|";
                String scorecard_id = Checker.generateUUid();
                String competitionNumber = (String) getCurrentCompetition().get("competitionNumber");
                String weaponGroup_id = getAWaponGroupRef(groupSinner.getSelectedItem().toString());
                String weaponClass_id = getAWaponClassRef(klasseSpinner.getSelectedItem().toString());
                String person_id = (String) thePerson.get("_id");
                String ref = "$ref";
                String klasse = "klasse";
                Document scorecard = mDatabase.getDocument(scorecardPrefix + scorecard_id);
                Map<String, Object> scorecardProperties = new HashMap<String, Object>();
                scorecardProperties.put("competitionNumber", competitionNumber);
                Map<String, Object> weaponGroupRef = new HashMap<String, Object>();
                weaponGroupRef.put(klasse, "WeaponGroup");
                weaponGroupRef.put(ref, weaponGroup_id);
                Map<String, Object> weaponClassRef = new HashMap<String, Object>();
                weaponClassRef.put(klasse, "WeaponClass");
                weaponClassRef.put(ref, weaponClass_id);
                scorecardProperties.put("weaponClass", weaponClassRef);
                scorecardProperties.put("shootingGroup", weaponGroupRef);
                ArrayList<Object> results = new ArrayList<>();
                scorecardProperties.put("results", results);
                Map<String, Object> competitor = new HashMap<String, Object>();
                competitor.put(klasse, "Person");
                String[] split = person_id.split("\\|");
                competitor.put(ref, split[1]);
                scorecardProperties.put("competitor", competitor);
                scorecardProperties.put("completed", false);
                scorecardProperties.put(klasse, "Scorecard");
                Document personDoc = mDatabase.getExistingDocument(memberId);
                Map<String, Object> scorecardRef = new HashMap<String, Object>();
                scorecardRef.put(klasse, "Scorecard");
                scorecardRef.put(ref, scorecard_id);
                ArrayList<Object> scorecards = (ArrayList<Object>) thePerson.get("scoreCards");
                scorecards.add(scorecardRef);
                HashMap<String, Object> alteredPerson = new HashMap<String, Object>();
                alteredPerson.putAll(thePerson);
                alteredPerson.put("scoreCards", scorecards);
                SharedPreferences sharedPref = mContext.getSharedPreferences(
                        mContext.getString(R.string.preferences), Context.MODE_PRIVATE);
                String t_id = sharedPref.getString("tournament_id", "none");
                Document tournamentDoc = mDatabase.getExistingDocument(t_id);
                Map<String, Object> tournamentProperties = tournamentDoc.getProperties();
                ArrayList<Object> tScorecards = (ArrayList<Object>) tournamentProperties.get("scorecards");
                tScorecards.add(scorecardRef);
                HashMap<String, Object> newTournamentProperties = new HashMap<String, Object>();
                newTournamentProperties.putAll(tournamentProperties);
                newTournamentProperties.put("scorecards", tScorecards);
                try {
                    scorecard.putProperties(scorecardProperties);
                    personDoc.putProperties(alteredPerson);
                    tournamentDoc.putProperties(newTournamentProperties);
                } catch (CouchbaseLiteException e) {
                    e.printStackTrace();
                }

            }
            i = i++;
        }
        Replication push = mDatabase.createPushReplication(createSyncURL(false));
        push.start();

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public Map<String, Object> getCurrentCompetition() {
        SharedPreferences sharedPref = mContext.getSharedPreferences(
                mContext.getString(R.string.preferences), Context.MODE_PRIVATE);
        String t_id = sharedPref.getString("tournament_id", "none");
        Document d = mDatabase.getExistingDocument(t_id);
        return d.getProperties();
    }

    public Map<String, Object> findTeam() {
        //TODO - Get user from preferences
        Map<String, Object> tournament = getCurrentCompetition();
        ArrayList<Object> teams = (ArrayList<Object>) tournament.get("teams");
        Map<String, Object> user = getCurrentUser();
        for (Object team : teams) {
            Map<String, Object> currentTeam = (Map<String, Object>) team;
            String teamId = "Team|" + currentTeam.get("$ref");
            Map<String, Object> theTeam = mDatabase.getExistingDocument(teamId).getProperties();
            ArrayList<Object> members = (ArrayList<Object>) theTeam.get("competitors");
            for (Object member : members) {
                Map<String, Object> curMember = (Map<String, Object>) member;
                String personId = "Person|" + curMember.get("$ref");
                Map<String, Object> person = mDatabase.getExistingDocument(personId).getProperties();
                if (person.get("mail").equals(user.get("mail"))) {
                    //TODO - Save team to preferences
                    SharedPreferences sharedPref = mContext.getSharedPreferences(
                            mContext.getString(R.string.preferences), Context.MODE_PRIVATE);
                    String team_id = (String) theTeam.get("_id");
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("team_id", team_id);
                    editor.commit();
                    return theTeam;
                }
            }
        }

        return null;
    }

    public Map<String, Object> getCurrentUser() {
        SharedPreferences sharedPref = mContext.getSharedPreferences(
                mContext.getString(R.string.preferences), Context.MODE_PRIVATE);
        String user_id = sharedPref.getString("user", "none");
        Document d = mDatabase.getExistingDocument(user_id);
        return d.getProperties();
    }

    public void setUpCBL() {

        try {
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

    public URL createSyncURL(boolean isEncrypted) {
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

    public ArrayList<String> getAllWeaponGroups() {
        ArrayList<String> strings = new ArrayList<>();
        ArrayList<Map<String, Object>> weaponGroups = new ArrayList<Map<String, Object>>();
        Query query = mDatabase.createAllDocumentsQuery();
        query.setAllDocsMode(Query.AllDocsMode.ALL_DOCS);
        try {
            QueryEnumerator result = query.run();
            for (Iterator<QueryRow> it = result; it.hasNext(); ) {
                QueryRow row = it.next();
                Document d = row.getDocument();
                Map<String, Object> current = d.getProperties();
                if (current.get("klasse").equals("WeaponGroup")) {
                    weaponGroups.add(current);
                }
            }
        } catch (Exception e) {

        }

        for (Map<String, Object> groups : weaponGroups) {
            strings.add((String) groups.get("name"));
        }
        return strings;
    }

    public ArrayList<String> getAllWeaponClasses() {
        ArrayList<String> strings = new ArrayList<>();
        ArrayList<Map<String, Object>> weaponClasses = new ArrayList<Map<String, Object>>();
        Query query = mDatabase.createAllDocumentsQuery();
        query.setAllDocsMode(Query.AllDocsMode.ALL_DOCS);
        try {
            QueryEnumerator result = query.run();
            for (Iterator<QueryRow> it = result; it.hasNext(); ) {
                QueryRow row = it.next();
                Document d = row.getDocument();
                Map<String, Object> current = d.getProperties();
                if (current.get("klasse").equals("WeaponClass")) {
                    weaponClasses.add(current);
                }
            }
        } catch (Exception e) {

        }
        for (Map<String, Object> classes : weaponClasses) {
            strings.add((String) classes.get("weaponName"));
        }
        return strings;

    }

    public String getAWaponClassRef(String weaponName) {

        Query query = mDatabase.createAllDocumentsQuery();
        query.setAllDocsMode(Query.AllDocsMode.ALL_DOCS);
        try {
            QueryEnumerator result = query.run();
            for (Iterator<QueryRow> it = result; it.hasNext(); ) {
                QueryRow row = it.next();
                Document d = row.getDocument();
                Map<String, Object> current = d.getProperties();
                if (current.get("klasse").equals("WeaponClass") && current.get("weaponName").equals(weaponName)) {
                    return (String) current.get("_id");
                }
            }
        } catch (Exception e) {

        }

        return "ikke spesifisert";
    }

    public String getAWaponGroupRef(String name) {

        Query query = mDatabase.createAllDocumentsQuery();
        query.setAllDocsMode(Query.AllDocsMode.ALL_DOCS);
        try {
            QueryEnumerator result = query.run();
            for (Iterator<QueryRow> it = result; it.hasNext(); ) {
                QueryRow row = it.next();
                Document d = row.getDocument();
                Map<String, Object> current = d.getProperties();
                if (current.get("klasse").equals("WeaponGroup") && current.get("name").equals(name)) {
                    String id = (String) current.get("_id"); //abc|asdhgegei
                    String[] parts = id.split("\\|");
                    return parts[1];
                }
            }
        } catch (Exception e) {

        }

        return "ikke spesifisert";
    }


}
