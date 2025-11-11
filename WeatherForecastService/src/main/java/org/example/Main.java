package org.example;

import org.example.dto.GeoResponse;
import org.example.dto.WeatherResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

import static spark.Spark.*;

public class Main {
    private static final GeoCodingService geoCodingService = new GeoCodingService();
    private static final WeatherService weatherService = new WeatherService();
    private static final WeatherCache cache = new WeatherCache();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) {
        port(8080);
        staticFiles.location("/static");

        get("/weather", (request, response) -> {
            String city = request.queryParams("city");

            if (city == null || city.trim().isEmpty()) {
                response.status(400);
                response.type("application/json");
                return objectMapper.writeValueAsString(Map.of("error", "City parameter is required"));
            }

            try {
                WeatherResponse weatherResponse = cache.get(city);

                if (weatherResponse == null) {
                    System.out.println("Fetching data from API for: " + city);
                    GeoResponse.GeoData geoData = geoCodingService.getCoordinates(city);
                    System.out.println("Coordinates for " + city + ": lat=" + geoData.getLatitude() + ", lon=" + geoData.getLongitude());

                    weatherResponse = weatherService.getWeather(geoData.getLatitude(), geoData.getLongitude());
                    cache.put(city, weatherResponse);
                } else {
                    System.out.println("Using cached data for: " + city);
                }

                response.type("application/json");
                return objectMapper.writeValueAsString(weatherResponse);

            } catch (Exception e) {
                System.err.println("Error for city " + city + ": " + e.getMessage());
                response.status(500);
                response.type("application/json");
                return objectMapper.writeValueAsString(Map.of("error", e.getMessage()));
            }
        });

        System.out.println("Server started: http://localhost:8080");
    }
}