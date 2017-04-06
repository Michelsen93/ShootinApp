package com.example.ole_martin.shootinapp.wifi;
import com.example.ole_martin.shootinapp.maybees.MyWifiActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ole-martin on 16.02.2017.
 */



public class WiFiDirectBroadcastReciever extends BroadcastReceiver{
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private MyWifiActivity mActivity;
    WifiP2pDevice mDevice;
    private List<WifiP2pDevice> mPeers;
    private List<WifiP2pConfig> mConfigs;

    public WiFiDirectBroadcastReciever(WifiP2pManager manager, WifiP2pManager.Channel channel,
                                       MyWifiActivity activity) {
        super();
        this.mManager = manager;
        this.mChannel = channel;
        this.mActivity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            //  Indicates a change in the Wi-Fi P2P status.
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if(state == WifiP2pManager.WIFI_P2P_STATE_ENABLED){


                mActivity.makeToast("wifi on");

            }else{
                mActivity.makeToast("please enable wifi");
            }

        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // Indicates a change in the list of available peers.
            mPeers = new ArrayList<WifiP2pDevice>();
            mConfigs = new ArrayList<WifiP2pConfig>();

            if(mManager != null){
                WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener(){
                    @Override
                    public void onPeersAvailable(WifiP2pDeviceList peerList){
                        mActivity.makeToast("Found peer");
                        mPeers.clear();
                        mPeers.addAll(peerList.getDeviceList());
                        mActivity.displayUsers(peerList);
                        mPeers.addAll(peerList.getDeviceList());
                        for(int i = 0; i<peerList.getDeviceList().size(); i++){
                            WifiP2pConfig config = new WifiP2pConfig();
                            config.deviceAddress = mPeers.get(i).deviceAddress;
                            mConfigs.add(config);
                        }
                    }
                };
                mManager.requestPeers(mChannel,peerListListener);
            }
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // Respond to new connection or disconnections
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing
        }
    }

    public void connect(int position){
        WifiP2pConfig config = mConfigs.get(position);
        mDevice = mPeers.get(position);
        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener(){
            @Override
            public void onSuccess(){
                //connected
                mActivity.makeToast("Connected");
            }
            @Override
            public void onFailure(int reason){
                //Connection failed
                mActivity.makeToast("Connection failed: " + reason);
            }
        });
    }

    WifiP2pManager.ConnectionInfoListener infoListener = new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo info) {
            InetAddress groupOwnerAddress = info.groupOwnerAddress;
            mActivity.makeToast("got info!");
            if(info.groupFormed){
                if(info.isGroupOwner){
                    //this is a host
                    //do host stuff
                    mActivity.doService(groupOwnerAddress, true);

                } else{
                    //this is a client
                    //do client stuff
                    mActivity.doService(groupOwnerAddress, false);

                }
            }
        }
    };
}
