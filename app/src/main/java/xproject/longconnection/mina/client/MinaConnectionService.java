package xproject.longconnection.mina.client;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.HandlerThread;
import android.os.IBinder;

import com.mina.config.ConnectionConfig;

import xproject.longconnection.mina.client.network.NetworkUtils;

public class MinaConnectionService extends Service {
    public static final String FORCE_TO_RECONNECT = "forceToReconnect";

    private ConnectThread connectThread;

    @Override
    public void onCreate() {
        super.onCreate();

        ConnectionConfig connectionConfig = new ConnectionConfig.Builder(9023).setIp("172.16.203.190")
                .setTimeInterval(20).setContext(getApplicationContext()).build();
        connectThread = new ConnectThread("mina", connectionConfig);
        connectThread.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean forceToReconnect = intent.getBooleanExtra(FORCE_TO_RECONNECT, false);
        System.out.println("MinaService forceToReconnect = " + forceToReconnect);
        if (forceToReconnect) {
            connectThread.setIsConnect(false);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        connectThread.disConnect();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private static class ConnectThread extends HandlerThread {
        public static final int SLEEP_IN_MILLIS = 3000;
        private ConnectionManager connectionManager;
        private boolean isConnect;
        private Context context;

        public ConnectThread(String name, ConnectionConfig connectionConfig) {
            super(name);
            connectionManager = new ConnectionManager(connectionConfig);
            context = (Context) connectionConfig.getContext();
        }

        public void setIsConnect(boolean isConnect) {
            this.isConnect = isConnect;
        }

        @Override
        protected void onLooperPrepared() {
            while (!isConnect) {
                if (NetworkUtils.isNetworkAvailable(context)) {
                    isConnect = connectionManager.connect();
                    if (isConnect) {
                        SessionHandler.getSessionHandler().setIoSession(connectionManager.getIoSession());
                        break;
                    }
                    try {
                        Thread.sleep(SLEEP_IN_MILLIS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("network is unavailable");
                    break;
                }

            }
        }

        public void disConnect() {
            connectionManager.disConnect();
        }
    }
}
