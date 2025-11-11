package org.example;

import org.example.dto.GeoResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class GeoCodingService {
    private static final String GEOCODING_API = "https://geocoding-api.open-meteo.com/v1/search?name=%s";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public GeoResponse.GeoData getCoordinates(String city) {
        try {
            String urlString = String.format(GEOCODING_API, city.replace(" ", "+"));
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            StringBuilder response = new StringBuilder();
            Scanner scanner = new Scanner(url.openStream());
            while (scanner.hasNext()) {
                response.append(scanner.nextLine());
            }
            scanner.close();

            GeoResponse geoResponse = objectMapper.readValue(response.toString(), GeoResponse.class);

            if (geoResponse.getResults() == null || geoResponse.getResults().isEmpty()) {
                throw new RuntimeException("City not found: " + city);
            }

            return geoResponse.getResults().get(0);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching coordinates: " + e.getMessage(), e);
        }
    }
}
