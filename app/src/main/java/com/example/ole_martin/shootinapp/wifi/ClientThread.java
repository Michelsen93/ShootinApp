package com.example.ole_martin.shootinapp.wifi;

import com.example.ole_martin.shootinapp.maybees.DataDisplayActivity;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by ole-martin on 16.02.2017.
 */

public class ClientThread implements Runnable{

    DataDisplayActivity dActivity;

    InetAddress mHostAddress;
    int mPort;
    int sendCount = 1;
    DatagramSocket socket;

    byte[] sendData;
    byte[] receiveData;

    String client = "client package number : ";
    String server;

    public ClientThread(InetAddress hostAddress, int port){
        mHostAddress = hostAddress;
        mPort = port;
    }


    @Override
    public void run() {

        if(mHostAddress != null && mPort != 0){

            while(true){

                try{
                    if(socket == null){
                        socket = new DatagramSocket(mPort);
                        socket.setSoTimeout(1);
                    }


                } catch (Exception e){

                }

                try {

                    sendData = (client + sendCount).getBytes();
                    sendCount++;

                    DatagramPacket packet = new DatagramPacket(sendData, sendData.length, mHostAddress,mPort);
                    socket.send(packet);


                } catch (Exception e){

                }

                try{
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    socket.receive(receivePacket);
                    receivePacket.getData();
                    server = new String(receivePacket.getData(), 0, receivePacket.getLength());



                } catch (Exception e){

                }


            }


        }


    }
}
