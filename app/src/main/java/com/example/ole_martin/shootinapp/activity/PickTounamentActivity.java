package com.example.ole_martin.shootinapp.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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
import java.util.Iterator;

public class PickTounamentActivity extends AppCompatActivity {
    Spinner mSpinner;
    Spinner mSpinner2;
    Manager mManager = null;
    Database mDatabase = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_tounament);
        //TODO - load db
        setUpCBL();
        try {
            startReplications();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }


        mSpinner = (Spinner) findViewById(R.id.pick_tournament);
        ArrayList<String> tournamentList = new ArrayList<String>();
        //TODO - Load content to list
        tournamentList.add("t1");
        tournamentList.add("t2");
        ArrayAdapter<String> adp = new ArrayAdapter<String> (this, android.R.layout.simple_spinner_dropdown_item, tournamentList);
        mSpinner.setAdapter(adp);
    }


    public void pick(View view){
        /**
         * if item selected:
         * get item from spinner
         * load tournament with the users team etc.
         * go to next activity
         */

        String selected = mSpinner.getSelectedItem().toString();

        //Load tournament to phone

        Intent intent = new Intent(this, TournamentActivity.class);
        startActivity(intent);
    }
    public void testCBL(View view){
        Query query = mDatabase.createAllDocumentsQuery();
        query.setAllDocsMode(Query.AllDocsMode.ALL_DOCS);
        try {
            QueryEnumerator result = query.run();

            for (Iterator<QueryRow> it = result; it.hasNext(); ) {
                QueryRow row = it.next();
                if (row.getConflictingRevisions().size() > 0) {

                    Document d = row.getDocument();
                }
            }
        }catch (Exception e){
            Toast.makeText(this, "feil", Toast.LENGTH_LONG).show();
        }
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

        //Start pull replication
    }

    public URL createSyncURL(boolean isEncrypted){
        URL syncURL = null;
        String host = "http://127.0.0.1";
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
