package xproject.longconnection.mina.client;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.mina.config.ConnectionConfig;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;
import org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;

public class ConnectionManager {
    public static final String OBJECT_FILTER = "objectFilter";
    public static final String HEART_BEAT = "heartbeat";
    public static final String LOGGER = "logger";
    public static final String ACTION = "xproject.longconnection.mina.message";
    public static final String KEY = "mina";
    public static final String HEARTBEAT = "heartbeat";

    private static class HeartbeatFactory implements KeepAliveMessageFactory {
        private final WeakReference<Context> weakReference;

        HeartbeatFactory(Context context) {
            this.weakReference = new WeakReference<>(context);
        }

        @Override
        public boolean isRequest(IoSession ioSession, Object o) {
            return true;
        }

        @Override
        public boolean isResponse(IoSession ioSession, Object o) {
            return false;
        }

        @Override
        public Object getRequest(IoSession ioSession) {
            return HEARTBEAT;
        }

        @Override
        public Object getResponse(IoSession ioSession, Object message) {
            System.out.println("received message is = " + message);
            Intent intent = new Intent(ACTION);
            intent.putExtra(KEY, (Serializable) message);
            LocalBroadcastManager.getInstance(weakReference.get()).sendBroadcast(intent);
            return null;
        }
    }

    private NioSocketConnector connector;
    private IoSession ioSession;
    private ConnectionConfig connectionConfig;

    public ConnectionManager(ConnectionConfig connectionConfig) {
        this.connectionConfig = connectionConfig;
        init();
    }

    public IoSession getIoSession() {
        return ioSession;
    }

    private void init() {
        connector = new NioSocketConnector();
        connector.setHandler(new IoHandlerAdapter());
        DefaultIoFilterChainBuilder chain = connector.getFilterChain();
        ProtocolCodecFilter filter = new ProtocolCodecFilter(new ObjectSerializationCodecFactory());
        chain.addLast(LOGGER, new LoggingFilter());
        chain.addLast(OBJECT_FILTER, filter);
        KeepAliveFilter heartBeat = new KeepAliveFilter(new HeartbeatFactory((Context) connectionConfig.getContext()),
                IdleStatus.READER_IDLE, KeepAliveRequestTimeoutHandler.CLOSE);
        heartBeat.setForwardEvent(true);
        heartBeat.setRequestTimeoutHandler(KeepAliveRequestTimeoutHandler.LOG);
        heartBeat.setRequestInterval(connectionConfig.getTimeInterval());
        chain.addLast(HEART_BEAT, heartBeat);
    }

    public boolean connect() {
        try {
            ConnectFuture connectFuture = connector.connect(new InetSocketAddress(connectionConfig.getIp(), connectionConfig.getPort()));
            connectFuture.awaitUninterruptibly();
            ioSession = connectFuture.getSession();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return ioSession != null;
    }

    public void disConnect() {
        if (ioSession != null) {
            ioSession.closeNow();
            ioSession.getCloseFuture().setClosed();
            ioSession.getCloseFuture().awaitUninterruptibly();
        }
        connector.dispose();
    }
}
