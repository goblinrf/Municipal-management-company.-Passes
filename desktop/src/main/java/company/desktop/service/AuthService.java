package company.desktop.service;

import company.desktop.model.LoginResponse;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class AuthService {
    private static String sessionCookie;

    public static LoginResponse login(String username, String password) {
        try {
            URL url = new URL("http://localhost:8081/login");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false); // важно, чтобы не потерять cookie

            // Формируем тело запроса
            String body = String.format("username=%s&password=%s",
                    URLEncoder.encode(username, "UTF-8"),
                    URLEncoder.encode(password, "UTF-8"));
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            try (OutputStream os = conn.getOutputStream()) {
                os.write(body.getBytes());
            }

            int code = conn.getResponseCode();
            if (code == 200) {
                // Сохраняем JSESSIONID
                String setCookie = conn.getHeaderField("Set-Cookie");
                if (setCookie != null && setCookie.contains("JSESSIONID")) {
                    sessionCookie = setCookie.split(";", 2)[0];
                    return new LoginResponse(true, "Успех", setCookie);
                } else {
                    return new LoginResponse(false, "Сессия не установлена", null);
                }
            } else {
                return new LoginResponse(false, "Неверный логин/пароль", null);
            }
        } catch (Exception e) {
            return new LoginResponse(false, "Ошибка: " + e.getMessage(), null);
        }
    }

    public static String getSessionCookie() {
        return sessionCookie;
    }

    public static boolean isAuthenticated() {
        return sessionCookie != null;
    }

    public static void logout() {
        if (sessionCookie == null) return;
        try {
            URL url = new URL("http://localhost:8081/logout");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Cookie", sessionCookie);
            conn.setDoOutput(true);
            conn.getResponseCode(); // отправить запрос
        } catch (Exception ignored) {
            // Можно залогировать, но не критично
        } finally {
            sessionCookie = null;
        }
    }
}
