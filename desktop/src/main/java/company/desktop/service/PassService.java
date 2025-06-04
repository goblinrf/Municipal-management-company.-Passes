package company.desktop.service;

import company.desktop.model.Pass;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import company.desktop.model.PassForReport;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
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
    public static List<PassForReport> fetchAllPassesForReport(String sessionCookie, LocalDate start, LocalDate end) {
        try {
            URL url = new URL("http://localhost:8081/api/reports?from=" + start.toString() + "&to=" + end.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Cookie", sessionCookie);
            int code = conn.getResponseCode();
            if (code == 200) {
                try (InputStream is = conn.getInputStream()) {
                    PassForReport[] passes = mapper.readValue(is, PassForReport[].class);
                    return Arrays.asList(passes);
                }
            } else {
                throw new RuntimeException("Ошибка загрузки пропусков: HTTP " + code);
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка загрузки пропусков: " + e.getMessage(), e);
        }
    }
    public static List<Pass> fetchAll(String sessionCookie) {
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

    public static void createPass(Pass pass, String sessionCookie) {
        try {
            URL url = new URL(BASE_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Cookie", sessionCookie);

            try (OutputStream os = conn.getOutputStream()) {
                mapper.writeValue(os, pass);
            }

            int code = conn.getResponseCode();
            if (code != 200 && code != 201) {
                throw new RuntimeException("Ошибка создания пропуска: HTTP " + code);
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка создания пропуска: " + e.getMessage(), e);
        }
    }

    public static void updatePass(Long id, Pass pass, String sessionCookie) {
        try {
            URL url = new URL(BASE_URL + "/" + id);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Cookie", sessionCookie);

            try (OutputStream os = conn.getOutputStream()) {
                mapper.writeValue(os, pass);
            }

            int code = conn.getResponseCode();
            if (code != 200) {
                throw new RuntimeException("Ошибка обновления пропуска: HTTP " + code);
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка обновления пропуска: " + e.getMessage(), e);
        }
    }
    public static void deactivatePasses(List<Long> ids, String sessionCookie) {
        try {
            URL url = new URL(BASE_URL + "/deactivate");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Cookie", sessionCookie);

            try (OutputStream os = conn.getOutputStream()) {
                mapper.writeValue(os, ids);
            }

            int code = conn.getResponseCode();
            if (code != 200) {
                throw new RuntimeException("Ошибка деактивации: HTTP " + code);
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка деактивации пропусков: " + e.getMessage(), e);
        }
    }
    public static void deletePass(Long id, String sessionCookie) {
        try {
            URL url = new URL(BASE_URL + "/" + id);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");
            conn.setRequestProperty("Cookie", sessionCookie);

            int code = conn.getResponseCode();
            if (code != 200 && code != 204) {
                throw new RuntimeException("Ошибка удаления пропуска: HTTP " + code);
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка удаления пропуска: " + e.getMessage(), e);
        }
    }
    public static Long generateCode() {
        return Long.valueOf((int) (100000 + Math.random() * 900000));
    }
    public static void extendPass(Long id, Map<String, Object> data, String token) {
        try {
            URL url = new URL(BASE_URL + "/" + id + "/extend");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Cookie", token);
            conn.setDoOutput(true);
            try (OutputStream os = conn.getOutputStream()) {
                mapper.writeValue(os, data);
            }
            int status = conn.getResponseCode();
            if (status != 200) {
                throw new RuntimeException("Ошибка продления: HTTP " + status);
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка продления пропуска", e);
        }
    }
}
