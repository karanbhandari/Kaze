package com.kaze.jailbreakpong;

import android.os.Handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;




public class SendReceive extends Thread {
    public Socket socket;
    public InputStream inputStream;
    public OutputStream outputStream;
    public Handler handler;


    public SendReceive(Socket skt, Handler hand){
        socket = skt;
        try{
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
            handler = hand;
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void run(){
        byte[] buffer = new byte[1024];
        int bytes;
        while (socket!=null){
            try{
                bytes = inputStream.read(buffer);
                if(bytes > 0){
                    handler.obtainMessage(1,bytes,-1,buffer).sendToTarget();
                }
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }
    public void write(byte[] bytes){
        try {
            outputStream.write(bytes);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
