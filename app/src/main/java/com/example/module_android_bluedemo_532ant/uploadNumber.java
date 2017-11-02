package com.example.module_android_bluedemo_532ant;

/**
 * Created by dell on 2017/9/9.
 */

import android.support.v7.app.AppCompatActivity;

import java.io.DataOutputStream;
import java.net.Socket;

/**
 * Created by dell on 2017/6/29.
 */

public class uploadNumber extends AppCompatActivity {
    String msg;
    long end;
    long start;
    long time;

    public void upload (final int number){
        Thread myThread = new Thread(new Runnable(){
            public void run(){
                try{
                    Socket socket = new Socket("192.168.1.105",8888);
                    DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                    outputStream.writeInt(number);
                    outputStream.flush();
//                    socket.shutdownOutput();
//                    socket.close();
                }

                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        myThread.start();
        try {
            myThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
