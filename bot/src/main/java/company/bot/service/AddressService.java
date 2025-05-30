package company.bot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import company.bot.models.Address;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AddressService {
    private static final String BASE_URL = "http://localhost:8081/api/addresses";
    private static final ObjectMapper mapper = new ObjectMapper();

    public static Address getAddressForPass(Long addressId, String sessionCookie) {
        try {
            URL url = new URL(BASE_URL + "/" + addressId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Cookie", sessionCookie);
            conn.setRequestProperty("Accept", "application/json");

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                try (InputStream is = conn.getInputStream()) {
                    return mapper.readValue(is, Address.class);
                }
            } else {
                throw new RuntimeException("Не удалось получить адрес: HTTP " + responseCode);
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при получении адреса: " + e.getMessage(), e);
        }
    }
}
