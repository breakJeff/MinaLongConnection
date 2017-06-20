package xproject.longconnection.mina.client.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtils {

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) return false;

        NetworkInfo[] allNetworkInfo = connectivity.getAllNetworkInfo();
        if (allNetworkInfo != null) {
            for (NetworkInfo networkInfo: allNetworkInfo) {
                if (networkInfo.isAvailable()) {
                    return true;
                }
            }
        }

        return false;
    }

}
