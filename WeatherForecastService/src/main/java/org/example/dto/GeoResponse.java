package org.example.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GeoResponse {
    private List<GeoData> results;

    public List<GeoData> getResults() { return results; }
    public void setResults(List<GeoData> results) { this.results = results; }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GeoData {
        private String name;
        private double latitude;
        private double longitude;
        private String country;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public double getLatitude() { return latitude; }
        public void setLatitude(double latitude) { this.latitude = latitude; }
        public double getLongitude() { return longitude; }
        public void setLongitude(double longitude) { this.longitude = longitude; }
        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }
    }
}
