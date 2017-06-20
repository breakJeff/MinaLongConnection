package com.mina.config;

public class ConnectionConfig {
    private final int port;
    private final int bufferSize;
    private final int idleTime;
    private final int timeInterval;
    private final String ip;
    private final Object context;

    public int getBufferSize() {
        return bufferSize;
    }

    public int getIdleTime() {
        return idleTime;
    }

    public int getTimeInterval() {
        return timeInterval;
    }

    public int getPort() {
        return port;
    }

    public String getIp() {
        return ip;
    }

    public Object getContext() {
        return context;
    }

    private ConnectionConfig(Builder builder) {
        this.port = builder.port;
        this.bufferSize = builder.bufferSize;
        this.idleTime = builder.idleTime;
        this.timeInterval = builder.timeInterval;
        this.ip = builder.ip;
        this.context = builder.context;
    }

    public static class Builder {
        private int port;
        private int bufferSize;
        private int idleTime;
        private int timeInterval;
        private String ip;
        private Object context;

        public Builder(int port) {
            this.port = port;
        }

        public Builder setBufferSize(int bufferSize) {
            this.bufferSize = bufferSize;
            return this;
        }

        public Builder setIdleTime(int idleTime) {
            this.idleTime = idleTime;
            return this;
        }

        public Builder setIp(String ip) {
            this.ip = ip;
            return this;
        }

        public Builder setContext(Object context) {
            this.context = context;
            return this;
        }

        public Builder setTimeInterval(int timeInterval) {
            this.timeInterval = timeInterval;
            return this;
        }

        public ConnectionConfig build() {
            return new ConnectionConfig(this);
        }
    }
}
