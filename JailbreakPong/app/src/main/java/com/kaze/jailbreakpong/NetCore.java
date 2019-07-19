package com.kaze.jailbreakpong;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class NetCore {
    Context context;
    WifiP2pManager wifiP2pManager;
    IntentFilter intentFilter;
    WifiP2pManager.Channel channel;
    BroadcastReceiver broadcastReceiver;
    HashMap<String, WifiP2pDevice> groupOwnerTable;
    WifiP2pManager.ConnectionInfoListener connectionInfoListener;
    Handler asyncIsOkHandler;
    boolean isInGroup;
    WifiP2pManager.PeerListListener peersListListener;
    private ArrayList<WifiP2pDevice> devicesList;
    private ServerSocket serverSocket;
    Server server;
    Client client;

    public ArrayList<WifiP2pDevice> getDevices(){
        return devicesList;
    }
    public void setDevicesList(ArrayList devicesListValue){devicesList = devicesListValue;}

    public NetCore(Context context, Handler asyncIsOkHandler) {
        this.context = context;
        this.wifiP2pManager = (WifiP2pManager) this.context.getApplicationContext().getSystemService(Context.WIFI_P2P_SERVICE);
        this.intentFilter = new IntentFilter();
        this.devicesList = new ArrayList<>();
        this.intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        this.intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        this.intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        this.intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        this.channel = this.wifiP2pManager.initialize(this.context.getApplicationContext(), this.context.getMainLooper(), null);
        this.peersListListener = new WifiP2pManager.PeerListListener() {
            @Override
            public void onPeersAvailable(WifiP2pDeviceList peerList) {
                //groupList.removeAllViews();
                ArrayList<WifiP2pDevice> devices = getDevices();
                if(!new ArrayList<>(peerList.getDeviceList()).equals(devices)){
                    groupOwnerTable.clear();
                    ArrayList<WifiP2pDevice> newDevices = new ArrayList<>(peerList.getDeviceList());
                    setDevicesList(newDevices);
                    for(int i = 0; i < newDevices.size(); i++) {
                        WifiP2pDevice device = newDevices.get(i);
                        connectPeer(device);
                        if (device.isGroupOwner()) {
                            groupOwnerTable.put(device.deviceAddress, device);
                        }
                    }
                }
            }
        };
        this.connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
            @Override
            public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
                final InetAddress groupOwnerAddress = wifiP2pInfo.groupOwnerAddress;
                if(wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner){
                        server = new Server();
                        server.start();

                }
                else if(wifiP2pInfo.groupFormed){
                        client = new Client(groupOwnerAddress);
                        client.start();
                }
            }
        };

        this.broadcastReceiver = new WifiDirectBroadcastReceiver(this.wifiP2pManager, this.channel,this.peersListListener,this.connectionInfoListener);
        this.context.registerReceiver(this.broadcastReceiver, this.intentFilter);
        this.groupOwnerTable = new HashMap<String, WifiP2pDevice>();
        this.asyncIsOkHandler = asyncIsOkHandler;
        this.isInGroup = false;
    }

    public void discoverPeers(final Activity updateActivity) {
        this.wifiP2pManager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

                int test = 0;
            }

            @Override
            public void onFailure(int arg0) {
                int test = 0;
            }
        });
    }

    public boolean writeServer(byte[] data){
        if(server.sendReceive.isAlive()){
            server.setMessage(data);
            return  true;
        }
        return false;
    }

    public boolean writeClient(byte[] data){
        try{
            if(client.sendReceive.isAlive()){
                client.setMessage(data);
                return true;
            }
        }
        catch(Exception e){
            int go = 0;
        }
        return false;
    }

    public void connectPeer(WifiP2pDevice device) {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        config.wps.setup = WpsInfo.PBC;

        wifiP2pManager.connect(channel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                isInGroup = true;
                Message message = new Message();
                message.what = 31;
                asyncIsOkHandler.sendMessage(message);
            }

            @Override
            public void onFailure(int arg0) {
                Message message = new Message();
                message.what = 32;
                asyncIsOkHandler.sendMessage(message);
            }
        });
    }

    public void createGroup() {
        this.wifiP2pManager.createGroup(this.channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                isInGroup = true;
                Message message = new Message();
                message.what = 11;
                asyncIsOkHandler.sendMessage(message);
            }

            @Override
            public void onFailure(int reason) {
                Message message = new Message();
                message.what = 12;
                asyncIsOkHandler.sendMessage(message);
            }
        });
    }

    public void dissolveGroup(final Handler notifySuccessOrFailed) {
        this.wifiP2pManager.removeGroup(this.channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                isInGroup = false;
                try {
                    serverSocket.close();
                } catch (IOException e) {
                }
                Message message = new Message();
                message.what = 21;
                notifySuccessOrFailed.sendMessage(message);
            }

            @Override
            public void onFailure(int reason) {
                Message message = new Message();
                message.what = 22;
                notifySuccessOrFailed.sendMessage(message);
            }
        });
    }
    public void debug(String content) {
    }

    public String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
//            Log.e("WifiPreference IpAddress", ex.toString());
        }
        return null;
    }
}
