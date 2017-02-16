package com.example.ole_martin.shootinapp.wifi;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by ole-martin on 16.02.2017.
 */

public class ServerThread implements Runnable{


    DatagramSocket socket;
    int mPort;

    String client = "nnn";
    String server;

    int sendCount = 0;
    int receiveCount = 0;

    byte[] sendData;
    byte[] receiveData;

    InetAddress mClientAddress;

    boolean gotPacket = false;

    public ServerThread(int initPort){
        mPort = initPort;
    }

    @Override
    public void run() {
        while(true){
            try{
                if(socket == null){
                    socket = new DatagramSocket(mPort);
                    socket.setSoTimeout(1);
                }
            } catch (Exception e){

            }

            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            try{
                socket.receive(receivePacket);
                receivePacket.getData();

                server = new String(receivePacket.getData(), 0, receivePacket.getLength());

                receiveCount++;

                if(mClientAddress == null){
                    mClientAddress = receivePacket.getAddress();
                }

            } catch (Exception e){


            }
            try{

                if (mClientAddress != null){
                    sendData = (client + sendCount).getBytes();
                    sendCount++;

                    DatagramPacket packet = new DatagramPacket(sendData, sendData.length, mClientAddress,mPort);

                    socket.send(packet);

                }


            }catch (Exception e){

            }


        }




    }
}
