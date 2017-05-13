package com.example.ole_martin.shootinapp.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Manager;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.android.AndroidContext;
import com.couchbase.lite.auth.Authenticator;
import com.couchbase.lite.auth.AuthenticatorFactory;
import com.couchbase.lite.replicator.Replication;
import com.couchbase.lite.util.Log;
import com.example.ole_martin.shootinapp.R;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by ole-martin on 08.05.2017.
 */


public class DAO {
    public static Database mDatabase;
    public static Manager mManager;



    public DAO(Context context){
        setUpCBL(context);

    }
    public void setUpCBL(Context context){
        try{
            mManager = new Manager(new AndroidContext(context), Manager.DEFAULT_OPTIONS);
            mDatabase = mManager.getDatabase(context.getResources().getString(R.string.DB_NAME));
            startPullReplication(context);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

    }

    public URL createSyncURL(boolean isEncrypted, Context context){
        URL syncURL = null;
        String host = "http://localhost";
        String port = "4984";
        String dbName = context.getResources().getString(R.string.DB_NAME);
        try {
            syncURL = new URL(host + ":" + port + "/" + dbName);
        } catch (MalformedURLException me) {
            me.printStackTrace();
        }
        return syncURL;
    }
    public void startPullReplication(Context context) throws CouchbaseLiteException {
        Replication pull = mDatabase.createPullReplication(this.createSyncURL(false, context));
        pull.start();
    }

    public Map<String, Object> findObject(String klasse, String attribute, String value){
        Query query = mDatabase.createAllDocumentsQuery();
        query.setAllDocsMode(Query.AllDocsMode.ALL_DOCS);
        try {
            QueryEnumerator result = query.run();
            for (Iterator<QueryRow> it = result; it.hasNext(); ) {
                QueryRow row = it.next();
                Document d = row.getDocument();
                Map<String, Object> current = d.getProperties();
                if(current.get("klasse").equals(klasse) &&
                        (current.get(attribute).equals(value) || current.get(attribute) == value)){
                    return current;
                }
            }
        }catch (Exception e){

        }
        return null;
    }

    public Map<String, Object> getCurrentCompetition(){
        Map<String, Object> currentCompetition;
        //TODO - load competition from preferences and return it...
        return currentCompetition;
    }



}
