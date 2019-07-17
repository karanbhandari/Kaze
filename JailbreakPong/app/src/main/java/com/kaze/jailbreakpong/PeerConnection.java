package com.kaze.jailbreakpong;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

class PeerConnection {
    private Socket peerSocket;
    private Thread worker;

    public PeerConnection(Socket peer) {
        this.peerSocket = peer;
        this.worker = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    byte[] buffer = new byte[1024];
                    InputStream peerInputs = peerSocket.getInputStream();
                    while (true) {
                        int readlen = peerInputs.read(buffer);
                        if (readlen > 0) {

                        }
                    }
                } catch (IOException e) {
                }
            }
        });
    }

    public void startProcessing() {
        this.worker.start();
    }

    public void stopProcessing() {
        this.worker.stop();
    }

    public String getPeerIp() {
        return peerSocket.getInetAddress().getHostAddress();
    }
}
