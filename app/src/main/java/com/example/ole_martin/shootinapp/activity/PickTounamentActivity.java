package com.example.ole_martin.shootinapp.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
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
import com.example.ole_martin.shootinapp.Java_Classes.Competition;
import com.example.ole_martin.shootinapp.R;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;


//TODO - load old tournaments to mSpinner2
public class PickTounamentActivity extends AppCompatActivity {
    Spinner mSpinner;
    Spinner mSpinner2;
    Manager mManager = null;
    Database mDatabase = null;
    Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_tounament);
        mContext = this;
        setUpCBL();
        try {
            startReplications();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }


        mSpinner = (Spinner) findViewById(R.id.pick_tournament);
        ArrayList<String> tournamentList = fillActive();

        ArrayAdapter<String> adp = new ArrayAdapter<String> (this, android.R.layout.simple_spinner_dropdown_item, tournamentList);
        mSpinner.setAdapter(adp);
    }

    public ArrayList<String> fillActive(){
        ArrayList<String> activeTournaments = new ArrayList<String>();

        Query query = mDatabase.createAllDocumentsQuery();
        query.setAllDocsMode(Query.AllDocsMode.ALL_DOCS);

        try {
            QueryEnumerator result = query.run();

            for (Iterator<QueryRow> it = result; it.hasNext(); ) {
                QueryRow row = it.next();
                Document d = row.getDocument();
                Map<String, Object> current = d.getProperties();

                if(current.get("klasse").equals("Competition") && (boolean) current.get("active") == true){
                    activeTournaments.add(Integer.toString((Integer) current.get("competitionNumber")));
                }
            }

        }catch (Exception e){
            Toast.makeText(this, "feil", Toast.LENGTH_LONG).show();
        }



        return activeTournaments;
    }

    public void pick(View view){
        /**
         * if item selected:
         * get item from spinner
         * load tournament with the users team etc.
         * go to next activity
         */

        String selected = mSpinner.getSelectedItem().toString();
        //get doc id from db
        Query query = mDatabase.createAllDocumentsQuery();
        query.setAllDocsMode(Query.AllDocsMode.ALL_DOCS);
        String docId = "";
        try {
            QueryEnumerator result = query.run();
            for (Iterator<QueryRow> it = result; it.hasNext(); ) {
                QueryRow row = it.next();
                Document d = row.getDocument();
                Map<String, Object> current = d.getProperties();
                if(current.get("klasse").equals("Competition") && (int) current.get("competitionNumber") == Integer.parseInt(selected)){
                    docId = (String) current.get("_id");
                    break;
                }
            }
        }catch (Exception e){
            Toast.makeText(this, "feil", Toast.LENGTH_LONG).show();
        }

        SharedPreferences sharedPref = mContext.getSharedPreferences(
                getString(R.string.preferences), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("tournament_id", selected);
        editor.commit();
        Intent intent = new Intent(this, InfoActivity.class);
        startActivity(intent);
    }


    public void pickCompletedTournament(){



        String selected = mSpinner2.getSelectedItem().toString();

        //Load tournament to phone

        Intent intent = new Intent(this, CompletedTournamentActivity.class);
        startActivity(intent);
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

    public URL createSyncURL(boolean isEncrypted){
        URL syncURL = null;
        String host = "http://158.37.228.221";
        String port = "4984";
        String dbName = getResources().getString(R.string.DB_NAME);
        try {
            syncURL = new URL(host + ":" + port + "/" + dbName);
        } catch (MalformedURLException me) {
            me.printStackTrace();
        }
        return syncURL;
    }
    private void startReplications() throws CouchbaseLiteException {
        final Replication pull = mDatabase.createPullReplication(this.createSyncURL(false));
        final ProgressDialog progressDialog = ProgressDialog.show(this, "Please wait ...", "Syncing", false);
        pull.addChangeListener(new Replication.ChangeListener() {
            @Override
            public void changed(Replication.ChangeEvent event) {
                boolean active = (pull.getStatus() == Replication.ReplicationStatus.REPLICATION_ACTIVE);
                if (!active) {
                    progressDialog.dismiss();
                } else {
                    int total = pull.getCompletedChangesCount();
                    progressDialog.setMax(total);
                    progressDialog.setProgress(pull.getChangesCount());
                }
            }
        });

        pull.start();
    }
}
