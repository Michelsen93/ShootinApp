package com.example.ole_martin.shootinapp.util;

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
import com.example.ole_martin.shootinapp.R;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by ole-martin on 28.04.2017.
 */

public class Checker {
    public static boolean checkLogin(String username, String password, Context context){
        //TODO - check if password matches username
        boolean ss = false;
        try{
            Manager mManager = new Manager(new AndroidContext(context), Manager.DEFAULT_OPTIONS);
            Database mDatabase = mManager.getDatabase(context.getResources().getString(R.string.DB_NAME));
            ss = validateUser(mDatabase, username, password, context);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }


        return ss;
    }

    /**
     * If credentials are correct, returns true and saves doc_id to preferences
     * @param database
     * @param username
     * @param password
     * @param context
     * @return
     */
    public static boolean validateUser(Database database, String username, String password, Context context){
    //TODO - find user
    Query query = database.createAllDocumentsQuery();
    query.setAllDocsMode(Query.AllDocsMode.ALL_DOCS);
    try {
        QueryEnumerator result = query.run();
        for (Iterator<QueryRow> it = result; it.hasNext(); ) {
            QueryRow row = it.next();
            Document d = row.getDocument();
            Map<String, Object> current = d.getProperties();
            if(current.get("klasse").equals("Person") && current.get("mail").equals(username)){
                //TODO - check password match
                //TODO - save user to preferences
                SharedPreferences sharedPref = context.getSharedPreferences(
                        context.getString(R.string.preferences), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("user", (String) current.get("_id"));
                editor.commit();
                return true;

            }
        }
    }catch (Exception e){

    }
    //TODO - Check password match
    //TODO - Save user to preferences
    return false;
    }
}