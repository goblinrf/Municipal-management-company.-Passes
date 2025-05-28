package company.bot.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
    private static final Map<Long, String> sessionMap = new ConcurrentHashMap<>();

    public static void saveSession(Long userId, String sessionId) {
        sessionMap.put(userId, sessionId);
    }

    public static String getSession(Long userId) {
        return "JSESSIONID=" + sessionMap.get(userId);
    }

    public static void logout(Long userId) {
        sessionMap.remove(userId);
    }

    public static boolean isLoggedIn(Long userId) {
        return sessionMap.containsKey(userId);
    }
}
