package company.desktop.service;

import company.desktop.model.LoginResponse;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class AuthService {
    public static LoginResponse login(String username, String password) {
        try {
            URL url = new URL("http://localhost:8081/api/auth/login");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String json = String.format("{\"username\": \"%s\", \"rawPassword\": \"%s\"}", username, password);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes());
            }

            int code = conn.getResponseCode();
            if (code == 200) {
                try (Scanner scanner = new Scanner(conn.getInputStream())) {
                    String token = scanner.useDelimiter("\\A").next();
                    return new LoginResponse(true, "Успех", token);
                }
            } else {
                return new LoginResponse(false, "Неверный логин или пароль", null);
            }
        } catch (Exception e) {
            return new LoginResponse(false, "Ошибка сервера: " + e.getMessage(), null);
        }
    }
}
