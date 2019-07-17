package com.kaze.jailbreakpong;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;

import java.net.InetSocketAddress;
import java.net.Socket;

public class WifiDirectBroadcastReceiver extends BroadcastReceiver{
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private WifiP2pManager.PeerListListener peerListListener;

    public WifiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, WifiP2pManager.PeerListListener peerListListener) {
        super();
        this.manager = manager;
        this.channel = channel;
        this.peerListListener = peerListListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Determine if Wifi P2P mode is enabled or not, alert
            // the Activity.
            if (manager != null) {
                manager.requestPeers(channel, peerListListener);
            }
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                // Wifi Direct mode is enabled
                //activity.setIsWifiP2pEnabled(true);
            }
            else {
                //activity.setIsWifiP2pEnabled(false);
            }
        }

        else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {

        }
        else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
        }
        else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing

//            DeviceListFragment fragment = (DeviceListFragment) activity.getFragmentManager()
//                    .findFragmentById(R.id.fragment_connect);
//            fragment.updateThisDevice((WifiP2pDevice) intent.getParcelableExtra(
//                    WifiP2pManager.EXTRA_WIFI_P2P_DEVICE));

        }
    }
}

