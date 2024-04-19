package webserver;

import java.util.HashMap;
import java.util.Map;

public class SessionManager {
    private static final Map<String, Session> SESSIONS = new HashMap<>();
    private static final SessionManager instance = new SessionManager();

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        return instance;
    }

    public static void add(final Session session) {
        SESSIONS.put(session.getId(), session);
    }

    public static boolean findSession(final String sessionId) {
        return SESSIONS.get(sessionId) != null;
    }

    public void remove(final String sessionId) {
        SESSIONS.remove(sessionId);
    }

}
