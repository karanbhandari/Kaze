package com.kaze.jailbreakpong;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;


public class Client extends Thread {
    Socket socket;
    String hostAdd;
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
    public Client(InetAddress hostAddress){
        hostAdd = hostAddress.getHostAddress();
        socket = new Socket();
    }

    public void setMessage(byte[] message) {
        this.message = message;
    }

    @Override
    public void run(){
        try{
            socket.connect(new InetSocketAddress(hostAdd,8888),500);
            sendReceive = new SendReceive(socket,handler);
            sendReceive.start();
            while (sendReceive != null){
                if(message != null){
                    sendReceive.write(message);
                    message = null;
                }
            }

        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

}
