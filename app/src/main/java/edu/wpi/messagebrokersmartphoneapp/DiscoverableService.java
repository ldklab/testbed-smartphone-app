package edu.wpi.messagebrokersmartphoneapp;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.StrictMode;
import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public class DiscoverableService extends Service {

    private final String deviceName = Build.MANUFACTURER + " " + Build.MODEL;

    public DiscoverableService() {
        Log.d("MyDebug", "Discoverable Service Started!");
        new Thread(new ClientListen()).start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Log.d("MyDebug", "onBind() Called");
        return null;
    }


    public class ClientListen implements Runnable {
        @Override
        public void run() {

            try {
                DatagramSocket socket = new DatagramSocket(3017);
                //socket.setBroadcast(true);

                //sendBroadcast("192.168.0.255", "Test");

                while (true) {
                    Log.i("MyDebug","Ready to receive broadcast packets!");

                    //Receive a packet
                    byte[] recvBuf = new byte[15000];
                    DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
                    socket.receive(packet);

                    //Packet received
                    String data = new String(packet.getData()).trim();
                    Log.i("MyDebug", "Packet received from: " + packet.getAddress().getHostAddress() + ":" + packet.getPort() + " data: " + data);

                    Map<String, String> myMap = new HashMap<>();
                    myMap.put("deviceID", "oifjqwk5wf7qcqwsfcqw");
                    myMap.put("name", deviceName);
                    myMap.put("address", "192.168.0.102");
                    myMap.put("timestamp", "1562941053222");
                    JSONObject jsonObject = new JSONObject(myMap);

                    System.out.println("About to send: " + jsonObject.toString());

                    sendUDPMessage(packet.getAddress().getHostAddress(), packet.getPort(), jsonObject.toString());
                }

            } catch (Exception ex) {
                Log.i("MyDebug", "Oops" + ex.getMessage());
            }

        }


        public void sendUDPMessage(String addr, int port, String messageStr) {
            // Hack Prevent crash (sending should be done using an async task)
            StrictMode.ThreadPolicy policy = new   StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            try {
                //Open a random port to send the package
                DatagramSocket socket = new DatagramSocket();
                //socket.setBroadcast(true);
                byte[] sendData = messageStr.getBytes();

                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(addr), port);
                socket.send(sendPacket);
                System.out.println(getClass().getName() + "Broadcast packet sent to: " + addr);
            } catch (IOException e) {
                Log.e("MyDebug", "IOException: " + e.getMessage());
            }
        }

    }
}
