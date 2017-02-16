package com.example.ole_martin.shootinapp.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.ole_martin.shootinapp.R;
import com.example.ole_martin.shootinapp.wifi.ClientThread;
import com.example.ole_martin.shootinapp.wifi.ServerThread;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class DataDisplayActivity extends AppCompatActivity {


    InetAddress hostAddress;
    Boolean host;
    String stringHostAddress;
    int port = 8888;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_display);
        Intent intent = getIntent();

        if(intent.getBooleanExtra("Connected",false)){
            stringHostAddress = intent.getStringExtra("HostAdress");

            try{
                hostAddress = InetAddress.getByName(stringHostAddress);
            } catch (UnknownHostException e){

            }

            host = intent.getBooleanExtra("IsHost", false);
            if(host){
                //serverThread
                ServerThread st = new ServerThread(port);
                Thread serverThread = new Thread(st);
                serverThread.start();

            } else {
                //clientThread
                ClientThread ct = new ClientThread(hostAddress, port);
                Thread clientTread = new Thread(ct);
                clientTread.start();
            }
        }


    }
}
