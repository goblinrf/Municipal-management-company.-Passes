package company.bot.service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class AuthService {

    public static String login(String login, String password) {
        try {
            URL url = new URL("http://localhost:8081/login");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            String body = "username=" + URLEncoder.encode(login, "UTF-8") +
                    "&password=" + URLEncoder.encode(password, "UTF-8");

            conn.getOutputStream().write(body.getBytes());

            if (conn.getResponseCode() == 200) {
                String cookie = conn.getHeaderField("Set-Cookie");
                if (cookie != null && cookie.contains("JSESSIONID")) {
                    return cookie.split("JSESSIONID=")[1].split(";")[0];
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
