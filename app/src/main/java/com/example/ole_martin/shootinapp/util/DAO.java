package com.example.ole_martin.shootinapp.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
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
    public Database mDatabase;
    public Manager mManager;
    public Context mContext;



    public DAO(Context context){
        setUpCBL(context);

    }
    public void setUpCBL(Context context){
        mContext = context;
        try{
            mManager = new Manager(new AndroidContext(mContext), Manager.DEFAULT_OPTIONS);
            mDatabase = mManager.getDatabase(context.getResources().getString(R.string.DB_NAME));
            startPullReplication();
        } catch (IOException e) {
            Toast.makeText(mContext, "IO feil", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (CouchbaseLiteException e) {
            Toast.makeText(mContext, "CLB feil", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

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
    public void startPullReplication() throws CouchbaseLiteException {
        Replication pull = mDatabase.createPullReplication(this.createSyncURL(false));
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
        SharedPreferences sharedPref = mContext.getSharedPreferences(
                mContext.getString(R.string.preferences), Context.MODE_PRIVATE);
        String t_id = sharedPref.getString("tournament_id", "none");
        Document d = mDatabase.getExistingDocument(t_id);
        return d.getProperties();
    }

    public Map<String, Object> findTeam(){
        //TODO - Get user from preferences
        Map<String, Object> tournament = getCurrentCompetition();

        Map<String, Object> teams = (Map<String, Object>) tournament.get("teams");
        Map<String, Object> user = getCurrentUser(mContext);
        for(Map.Entry<String, Object> team : teams.entrySet()){
            Map<String, Object> curTeam = (Map<String, Object>)team.getValue();
            for(Map.Entry<String, Object> member :  curTeam.entrySet()){
                Map<String, Object> curMember = (Map<String, Object>) member.getValue();
                if(curMember.get("mail").equals(user.get("mail"))){
                    return curTeam;
                }
            }

            return null;
        }

        return null;
    }

    public Map<String, Object> getCurrentUser(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.preferences), Context.MODE_PRIVATE);
        String user_id = sharedPref.getString("user", "none");
        Document d = mDatabase.getExistingDocument(user_id);
        return d.getProperties();
    }



}
