package com.example.ole_martin.shootinapp.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.test.suitebuilder.TestMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ole_martin.shootinapp.R;
import com.example.ole_martin.shootinapp.wifi.WiFiDirectBroadcastReciever;

import java.net.InetAddress;
import java.util.ArrayList;

public class MyWifiActivity extends AppCompatActivity {
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    WiFiDirectBroadcastReciever mReceiver;
    IntentFilter mIntentFilter;
    ListView mListView;
    ArrayAdapter <String> mAdapter;
    TextView tv;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_wifi);
        tv = (TextView) findViewById(R.id.status);
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new WiFiDirectBroadcastReciever(mManager, mChannel, this);
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        mListView = (ListView) findViewById(R.id.user_list);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                mReceiver.connect(pos);

            }
        });



    }


    public void searchForUsers(){

        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                //show you are searching?
                tv.setText("Wifi Direct: searching...");
            }

            @Override
            public void onFailure(int reasonCode) {
                //Show error
                tv.setText("Error : " + reasonCode);
            }
        });
    }
    public void displayUsers(WifiP2pDeviceList peerList){
        ArrayList<String> users = new ArrayList<String>();
        for(WifiP2pDevice peer : peerList.getDeviceList()){
            users.add(peer.deviceName + " " + peer.deviceAddress);
        }
        mAdapter = new ArrayAdapter<String >(this, android.R.layout.simple_list_item_1,users);
        mListView = (ListView) findViewById(R.id.user_list);
        mListView.setAdapter(mAdapter);

    }

    public void doService(InetAddress address, boolean host){
        Intent intent = new Intent(this, DataDisplayActivity.class);
        intent.putExtra("HostAddress", address);
        intent.putExtra("IsHost", host);
        intent.putExtra("Connected", true);

        startActivity(intent);

    }

    public void searchUsers(View view){
        searchForUsers();
    }


    /* register the broadcast receiver with the intent values to be matched */
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }
    /* unregister the broadcast receiver */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }
    public void makeToast(String toast){
        Toast.makeText(this, toast, Toast.LENGTH_LONG).show();
    }
}
