package com.connection.java.client;

import com.mina.config.ConnectionConfig;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.net.InetSocketAddress;

public class ConnectionClient {

    private ConnectionConfig connectionConfig;

    public static void main(String[] args) {
        ConnectionClient connectionClient = new ConnectionClient(new ConnectionConfig.Builder(9023).setIp("172.16" +
                ".203.190").build());
        connectionClient.connect();
    }

    public ConnectionClient(ConnectionConfig connectionConfig) {
        this.connectionConfig = connectionConfig;
    }

    public void connect() {
        NioSocketConnector connector = new NioSocketConnector();
        connector.setHandler(new IoHandlerAdapter() {
            @Override
            public void messageReceived(IoSession session, Object message) throws Exception {
                super.messageReceived(session, message);
                System.out.println("message received in java side: " + message);
            }
        });
        connector.getFilterChain().addLast("logger", new LoggingFilter());
        connector.getFilterChain().addLast("objectFilter", new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));
        ConnectFuture future = null;
        try {
            future = connector.connect(new InetSocketAddress(connectionConfig.getIp(), connectionConfig.getPort()));
            future.awaitUninterruptibly();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (future != null) {
            IoSession ioSession = future.getSession();
            ioSession.write("hello world from java client");
        }
    }
}
