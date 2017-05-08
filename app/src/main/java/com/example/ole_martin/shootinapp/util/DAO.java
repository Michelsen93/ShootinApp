package com.example.ole_martin.shootinapp.util;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.auth.Authenticator;
import com.couchbase.lite.auth.AuthenticatorFactory;
import com.couchbase.lite.replicator.Replication;
import com.couchbase.lite.util.Log;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ole-martin on 08.05.2017.
 */

public class DAO {

    private Database init(){


        return null;
    }

    private Document createDocument(Database database){
        //Random id

        //Specific id

        Document idDocument = database.getDocument("mySpecificID");

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", "Michelsen");
        map.put("age", "24");
        try {
            idDocument.putProperties(map);
        } catch (CouchbaseLiteException e) {
            Log.e("Couchbase", "Error creating", e);
        }
        return idDocument;
    }
    private Map getExistingDocumentMap(Database database, String documentId){
        Document document = database.getExistingDocument(documentId);
        Map<String, Object> properties = document.getProperties();
        return properties;
    }

    private void updateDocument(Database database, String documentId){

        Document updateDocument = database.getExistingDocument(documentId);
        if(updateDocument != null){
            Map<String, Object> updates = new HashMap<String, Object>();
            updates.putAll(updateDocument.getProperties());

            //Add values to map

            try {
                updateDocument.putProperties(updates);
            } catch (CouchbaseLiteException e) {
                Log.e("Couchbase: ", "unable to save", e);
            }
        }
    }

    private void deleteDocument(Database database, String documentId){
        Document deleteDocument = database.getExistingDocument(documentId);
        if(deleteDocument != null){
            try {
                deleteDocument.delete();
            } catch (CouchbaseLiteException e) {
                Log.e("Couchbase:", "unable to delete document", e);
            }
        }
    }

    private void createPull(Database database){
        String syncGatewayDNS = "http//:127.0.0.1";
        String syncGateWayPort ="4984";


        try {
            URL syncGateWayURI = new URL(syncGatewayDNS + ":" + syncGateWayPort +
                    database.getName());
            Replication pull = database.createPullReplication(syncGateWayURI);

            pull.addChangeListener(new Replication.ChangeListener(){
                @Override
                public void changed(Replication.ChangeEvent changeEvent){
                    int completedChangesCount = changeEvent.getSource().getCompletedChangesCount();
                    int changesCount = changeEvent.getSource().getChangesCount();
                    //Update view
                }
            });

            pull.start();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

    private void createPush(Database database){
        String syncGatewayDNS = "http://127.0.0.1";
        String syncGateWayPort ="4984";

        try {
            URL syncGateWayURI = new URL(syncGatewayDNS + ":" + syncGateWayPort +
                    database.getName());
            Replication push = database.createPushReplication(syncGateWayURI);
            push.start();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
    private void createAuthenticatedPull(Database database){
        String syncGatewayDNS = "http://127.0.0.1";
        String syncGateWayPort ="4984";


        try {
            URL syncGateWayURI = new URL(syncGatewayDNS + ":" + syncGateWayPort +
                    database.getName());
            Replication pull = database.createPullReplication(syncGateWayURI);

            pull.addChangeListener(new Replication.ChangeListener(){
                @Override
                public void changed(Replication.ChangeEvent changeEvent){
                    int completedChangesCount = changeEvent.getSource().getCompletedChangesCount();
                    int changesCount = changeEvent.getSource().getChangesCount();
                    //Update view
                }
            });

            Authenticator authenticator = AuthenticatorFactory.createBasicAuthenticator("username", "password");
            pull.setAuthenticator(authenticator);

            pull.start();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

}
