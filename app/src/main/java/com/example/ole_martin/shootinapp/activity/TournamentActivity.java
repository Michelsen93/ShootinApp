package com.example.ole_martin.shootinapp.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class TournamentActivity extends AppCompatActivity {
    private Map<String, Map<String, Object>> mStandplasses;
    private Map<String, Object> mTeam;
    private Context mContext;
    private Database mDatabase;
    private Manager mManager;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView mNavigationView;
    private Spinner mSpinner;
    private EditText mFigures;
    private EditText mHits;
    private EditText mBullseyes;
    private static final int REQUEST_EXTERNAL_WRITE = 0;

    private String externalString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        externalString = "";
        setContentView(R.layout.activity_tournament);
        mFigures = (EditText) findViewById(R.id.figures);
        mHits = (EditText) findViewById(R.id.hits);
        mBullseyes = (EditText) findViewById(R.id.bullseye);
        mFigures.setTransformationMethod(null);
        mHits.setTransformationMethod(null);
        mBullseyes.setTransformationMethod(null);
        mContext = this;
        mStandplasses = new HashMap<String, Map<String, Object>>();
        setUpCBL();
        mSpinner = (Spinner) findViewById(R.id.person_spinner);


        mDrawerLayout = (DrawerLayout) findViewById(R.id.tournament_layout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Sets up navigationDrawer with clicks
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.bringToFront();
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.nav_account) {
                    Intent a = new Intent(getBaseContext(), MyAccountActivity.class);
                    startActivity(a);

                } else if (id == R.id.nav_settings) {
                    Intent b = new Intent(getBaseContext(), SettingsActivity.class);
                    startActivity(b);

                } else if (id == R.id.nav_live) {
                    Intent c = new Intent(getBaseContext(), ShowStatsActivity.class);
                    startActivity(c);

                } else if (id == R.id.nav_registration) {
                    Intent d = new Intent(getBaseContext(), TournamentActivity.class);
                    startActivity(d);

                }
                return true;
            }
        });

        getStandplasses();
        fillSpinner();
        //Load standplasses and let user pick one to register result
        //Let user have optaion to register result for team member


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void fillSpinner() {
        mTeam = findTeam();
        ArrayList<String> memberList = new ArrayList<String>();
        for (Map<String, Object> member : (ArrayList<Map<String, Object>>) mTeam.get("competitors")) {
            String personId = "Person|" + member.get("$ref");
            Map<String, Object> theMember = mDatabase.getExistingDocument(personId).getProperties();
            memberList.add((String) theMember.get("firstName") + " " + theMember.get("lastName"));
        }
        ArrayAdapter<String> adp = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, memberList);
        mSpinner.setAdapter(adp);
    }

    public void getStandplasses() {
        Map<String, Object> currentCompetition = getCurrentCompetition();
        ArrayList<Map<String, Object>> standplasses = (ArrayList<Map<String, Object>>) currentCompetition.get("standplasses");

        for (Map<String, Object> standplass : standplasses) {
            String asd = (String) standplass.get("$ref");
            Document d = mDatabase.getExistingDocument(asd);
            if (d == null) {
                String fds = "Standplass|" + standplass.get("$ref");
                Query query = mDatabase.createAllDocumentsQuery();
                query.setAllDocsMode(Query.AllDocsMode.ALL_DOCS);
                try {
                    QueryEnumerator result = query.run();
                    for (Iterator<QueryRow> it = result; it.hasNext(); ) {
                        QueryRow row = it.next();
                        Document doc = row.getDocument();
                        Map<String, Object> current = doc.getProperties();
                        if (current.get("_id").equals(fds)) {
                            d = doc;
                        }
                    }
                } catch (Exception e) {
                    String s = e.toString();
                }
            }
            if (d != null) {
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
            }


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

    public void showRegisterResult(View v) {
        //color selected green
        LinearLayout ll = (LinearLayout) findViewById(R.id.result_form);
        ll.setVisibility(View.VISIBLE);
        LinearLayout standplassesLayout = (LinearLayout) findViewById(R.id.standplasses);
        Button clicked = (Button) v;
        for (int i = 0; i < standplassesLayout.getChildCount(); i++) {
            Button aButton = (Button) standplassesLayout.getChildAt(i);
            aButton.getBackground().clearColorFilter();
        }
        v.getBackground().setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);
        //Save teamMember key to value
        TextView standplassName = (TextView) findViewById(R.id.nameOfStandplass);
        standplassName.setText(clicked.getText());


        //Save standplass key to value
        //Display register thing
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

    public void registerResult(View view) {
        //get input of textviews
        String name = mSpinner.getSelectedItem().toString();
        //standplassname, hits, figures, bullseye
        TextView standplassnm = (TextView) findViewById(R.id.nameOfStandplass);
        String standplassName = standplassnm.getText().toString();
        if (!isEmpty(mHits) && !isEmpty(mFigures)) {
            int bullseyes;
            if (isEmpty(mBullseyes)) {
                bullseyes = 0;
            } else {
                bullseyes = Integer.parseInt(mBullseyes.getText().toString());
            }
            int hits = Integer.parseInt(mHits.getText().toString());
            int figures = Integer.parseInt(mFigures.getText().toString());
            Document personDoc = getDocOfPerson(name);
            String standplass = (String) mStandplasses.get(standplassName).get("name");
            HashMap<String, Object> result = new HashMap<String, Object>();
            result.put("hits", hits);
            result.put("figures", figures);
            result.put("bullseyes", bullseyes);
            result.put("standplass", standplass);
            Document scoreCard = getScorecardOfPerson(personDoc);
            HashMap<String, Object> newScorecard = new HashMap<>();
            newScorecard.putAll(scoreCard.getProperties());
            ArrayList<Map<String, Object>> results = (ArrayList<Map<String, Object>>) newScorecard.get("results");
            boolean overwrite = true;
            if (results.size() == 0) {
                results.add(result);
            } else {
                for (int i = 0; i < results.size(); i++) {
                    if (results.get(i).get("standplass").equals(standplass)) {
                        results.remove(i);
                        results.add(result);
                        overwrite = false;
                    }
                }
                    if (overwrite) {
                        results.add(result);
                    }
                    Map<String, Object> personDocProperties = personDoc.getProperties();
                    String navn = (String) personDocProperties.get("firstName") + " " + personDocProperties.get("lastName") + " : ";
                    externalString = navn + standplass + " | " + hits + " | " + figures + " | " + bullseyes;
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_GRANTED){
                    ExternalStorager.saveScoreToExternal(mContext, externalString);
                } else {
                    writeExt();
                }

                }
                newScorecard.put("results", results);
                if (results.size() == mStandplasses.size()) {
                    newScorecard.put("completed", true);
                }
                try {
                    scoreCard.putProperties(newScorecard);
                } catch (CouchbaseLiteException e) {
                    e.printStackTrace();
                }
                Replication push = mDatabase.createPushReplication(createSyncURL(false));
                push.start();


                Toast.makeText(this, "Resultat lagret", Toast.LENGTH_LONG).show();


            } else{
                Toast.makeText(this, "Mangler innput", Toast.LENGTH_LONG).show();
            }

            //if any are empty toast for wrong input
            //else get inputs, get persondoc, create result hashmap, put to scorecard of person
        }

        @TargetApi(23)
        public void writeExt(){


                if(shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){

                }
                requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_EXTERNAL_WRITE);

        }
        @Override
        public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
            if(requestCode == REQUEST_EXTERNAL_WRITE){
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    ExternalStorager.saveScoreToExternal(mContext, externalString);
                }

            }
        }

    public Document getScorecardOfPerson(Document personDoc) {
        Map<String, Object> personProperties = personDoc.getProperties();
        Document scorecardOfPerson = null;
        ArrayList<Map<String, Object>> personScorecards = (ArrayList<Map<String, Object>>) personProperties.get("scoreCards");
        for (Map<String, Object> scorecard : personScorecards) {
            String scorecardId = (String) scorecard.get("$ref");
            scorecardOfPerson = mDatabase.getExistingDocument("Scorecard|" + scorecardId);
            Map<String, Object> scorecardProperties = scorecardOfPerson.getProperties();
            if ((boolean) scorecardProperties.get("completed") == false) {
                return scorecardOfPerson;
            }
        }
        return scorecardOfPerson;
    }


    public Document getDocOfPerson(String name) {
        String[] names = name.split(" ");
        Query query = mDatabase.createAllDocumentsQuery();
        query.setAllDocsMode(Query.AllDocsMode.ALL_DOCS);
        try {
            QueryEnumerator result = query.run();
            for (Iterator<QueryRow> it = result; it.hasNext(); ) {
                QueryRow row = it.next();
                Document d = row.getDocument();
                Map<String, Object> current = d.getProperties();
                if (current.get("klasse").equals("Person") && current.get("firstName").equals(names[0]) && current.get("lastName").equals(names[1])) {
                    return d;
                }
            }
        } catch (Exception e) {

        }
        return null;
    }

    public Map<String, Object> getCurrentCompetition() {
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

    public URL createSyncURL(boolean isEncrypted) {
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

    public Map<String, Object> findTeam() {

        SharedPreferences sharedPref = mContext.getSharedPreferences(
                mContext.getString(R.string.preferences), Context.MODE_PRIVATE);
        String user_id = sharedPref.getString("team_id", "none");
        Document d = mDatabase.getExistingDocument(user_id);
        return d.getProperties();


    }

    public Map<String, Object> getCurrentUser() {
        SharedPreferences sharedPref = mContext.getSharedPreferences(
                mContext.getString(R.string.preferences), Context.MODE_PRIVATE);
        String user_id = sharedPref.getString("user", "none");
        Document d = mDatabase.getExistingDocument(user_id);
        return d.getProperties();
    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }
}
