package org.example;

import org.example.dto.WeatherResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class WeatherService {
    private static final String WEATHER_API = "https://api.open-meteo.com/v1/forecast?latitude=%s&longitude=%s&hourly=temperature_2m";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public WeatherResponse getWeather(double latitude, double longitude) {
        try {
            String latStr = String.valueOf(latitude).replace(',', '.');
            String lonStr = String.valueOf(longitude).replace(',', '.');
            String urlString = String.format(WEATHER_API, latStr, lonStr);
            System.out.println("Requesting weather from: " + urlString);

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                throw new RuntimeException("HTTP error: " + responseCode);
            }

            StringBuilder response = new StringBuilder();
            Scanner scanner = new Scanner(url.openStream());
            while (scanner.hasNext()) {
                response.append(scanner.nextLine());
            }
            scanner.close();

            return objectMapper.readValue(response.toString(), WeatherResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching weather: " + e.getMessage(), e);
        }
    }
}
