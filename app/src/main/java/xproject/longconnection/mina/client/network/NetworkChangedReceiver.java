package xproject.longconnection.mina.client.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Parcelable;

import xproject.longconnection.mina.client.MinaConnectionService;

public class NetworkChangedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
            Parcelable networkExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (networkExtra != null) {
                NetworkInfo networkInfo = (NetworkInfo) networkExtra;
                NetworkInfo.State state = networkInfo.getState();
                startService(state == NetworkInfo.State.CONNECTED, context);
            }
        } else if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            NetworkInfo info = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            if (info != null) {
                startService(NetworkInfo.State.CONNECTED == info.getState() && info.isAvailable(), context);
            }
        }
    }

    private static void startService(boolean isConnect, Context context) {
        if (isConnect) {
            Intent minaIntent = new Intent(context, MinaConnectionService.class);
            minaIntent.putExtra(MinaConnectionService.FORCE_TO_RECONNECT, true);
            context.startService(minaIntent);
            System.out.println("NetworkConnectChangedReceiver connected");
        } else {
            System.out.println("oops, no network");
            Intent minaIntent = new Intent(context, MinaConnectionService.class);
            context.stopService(minaIntent);
        }
    }
}