package com.kaze.jailbreakpong;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class Server extends Thread {
    Socket socket;
    ServerSocket serverSocket;
    SendReceive sendReceive;
    byte[] message;
    static  final int MESSAGE_READ = 1;
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case MESSAGE_READ:
                    byte[] readBuff = (byte[]) msg.obj;
                    String tempMsg = new String(readBuff,0,msg.arg1);
                    int go = 0;
                    break;
            }
            return true;
        }
    });
    public void setMessage(byte [] data){
        this.message = data;
    }
    @Override
    public void run(){
        try{
            serverSocket = new ServerSocket(8888);
            socket = serverSocket.accept();
            sendReceive = new SendReceive(socket,handler);
            sendReceive.start();
            while (sendReceive != null){
                if(message != null){
                    sendReceive.write(message);
                    message = null;
                }
            }
            //sendReceive.write("This is a test".getBytes());
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
