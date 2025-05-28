package company.desktop.service;

import company.desktop.model.Address;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class AddressService {

    private static final String BASE_URL = "http://localhost:8081/api/addresses";
    private static final ObjectMapper mapper = new ObjectMapper();

    public static List<Address> fetchAll(String sessionCookie) {
        try {
            URL url = new URL(BASE_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Cookie", sessionCookie);  // ← ОБЯЗАТЕЛЬНО
            int code = conn.getResponseCode();
            if (code == 200) {
                try (InputStream is = conn.getInputStream()) {
                    Address[] addresses = mapper.readValue(is, Address[].class);
                    return Arrays.asList(addresses);
                }
            } else {
                throw new RuntimeException("Ошибка загрузки адресов: HTTP " + code);
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка загрузки адресов: " + e.getMessage(), e);
        }
    }

    public static void createAddress(Address address, String sessionCookie) {
        try {
            URL url = new URL(BASE_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Cookie", sessionCookie);  // ← ОБЯЗАТЕЛЬНО

            try (OutputStream os = conn.getOutputStream()) {
                mapper.writeValue(os, address);
            }

            int code = conn.getResponseCode();
            if (code != 200 && code != 201) {
                throw new RuntimeException("Ошибка создания адреса: HTTP " + code);
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка создания адреса: " + e.getMessage(), e);
        }
    }

    public static void updateAddress(Long id, Address address, String sessionCookie) {
        try {
            URL url = new URL(BASE_URL + "/" + id);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Cookie", sessionCookie);  // ← ОБЯЗАТЕЛЬНО

            try (OutputStream os = conn.getOutputStream()) {
                mapper.writeValue(os, address);
            }

            int code = conn.getResponseCode();
            if (code != 200) {
                throw new RuntimeException("Ошибка обновления адреса: HTTP " + code);
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка обновления адреса: " + e.getMessage(), e);
        }
    }

    public static void deleteAddress(Long id, String sessionCookie) {
        try {
            URL url = new URL(BASE_URL + "/" + id);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");
            conn.setRequestProperty("Cookie", sessionCookie);  // ← ОБЯЗАТЕЛЬНО

            int code = conn.getResponseCode();
            if (code != 200 && code != 204) {
                throw new RuntimeException("Ошибка удаления адреса: HTTP " + code);
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка удаления адреса: " + e.getMessage(), e);
        }
    }
}
