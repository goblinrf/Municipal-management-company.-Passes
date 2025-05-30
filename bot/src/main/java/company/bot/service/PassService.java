package company.bot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import company.bot.models.Pass;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PassService {
    private static final String BASE_URL = "http://localhost:8081/api/passes";
    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        // Поддержка LocalDateTime
        mapper.registerModule(new JavaTimeModule());
    }

    public static List<Pass> getAllPasses(String sessionCookie) {
        try {
            URL url = new URL(BASE_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Cookie", sessionCookie);
            int code = conn.getResponseCode();
            if (code == 200) {
                try (InputStream is = conn.getInputStream()) {
                    Pass[] passes = mapper.readValue(is, Pass[].class);
                    return Arrays.asList(passes);
                }
            } else {
                throw new RuntimeException("Ошибка загрузки пропусков: HTTP " + code);
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка загрузки пропусков: " + e.getMessage(), e);
        }
    }

    public static Pass getPassById(Long id, String sessionCookie) {
        try {
            URL url = new URL(BASE_URL + "/" + id);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Cookie", sessionCookie);
            int code = conn.getResponseCode();
            if (code == 200) {
                try (InputStream is = conn.getInputStream()) {
                    Pass pass = mapper.readValue(is, Pass.class);
                    return pass;
                }
            } else {
                throw new RuntimeException("Ошибка загрузки пропуска: HTTP " + code);
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка загрузки пропуска: " + e.getMessage(), e);
        }
    }

    public static void extendPass(String sessionCookie, Long id, LocalDate limitation, String code) {
        try {
            URL url = new URL(BASE_URL + "/" + id + "/extend");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Cookie", sessionCookie);
            conn.setDoOutput(true);

            Map<String, Object> data = new HashMap<>();
            if (limitation != null) {
                data.put("limitation", limitation.toString());
            }
            if (code != null && code.matches("\\d{6}")) {
                data.put("code", code);
            }
            try (OutputStream os = conn.getOutputStream()) {
                mapper.writeValue(os, data);
            }

            int status = conn.getResponseCode();
            if (status != 200) {
                throw new RuntimeException("Ошибка продления: HTTP " + status);
            }

        } catch (Exception e) {
            throw new RuntimeException("Ошибка продления пропуска: " + e.getMessage(), e);
        }
    }
}
