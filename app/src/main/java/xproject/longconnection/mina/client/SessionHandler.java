package xproject.longconnection.mina.client;

import org.apache.mina.core.session.IoSession;

public class SessionHandler {
    private final static SessionHandler SESSION_HANDLER = new SessionHandler();
    public static SessionHandler getSessionHandler() {
        return SESSION_HANDLER;
    }

    private IoSession ioSession;

    private SessionHandler() {}

    public void setIoSession(IoSession ioSession) {
        this.ioSession = ioSession;
    }

    public void writeToServer(Object message) {
        if (ioSession == null) return;

        ioSession.write(message);
    }

    public void closeSession() {
        if (ioSession == null) return;

        ioSession.closeOnFlush();
    }
}
