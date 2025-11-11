package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.WeatherResponse;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class WeatherCache {
    private static final int CACHE_TTL_SECONDS = 15 * 60;
    private final JedisPool jedisPool;
    private final ObjectMapper objectMapper;

    public WeatherCache() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        this.jedisPool = new JedisPool(poolConfig, "localhost", 6379, 2000);
        this.objectMapper = new ObjectMapper();
    }

    public void put(String city, WeatherResponse weatherResponse) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String key = "weather:" + city.toLowerCase();
            String json = objectMapper.writeValueAsString(weatherResponse);
            jedis.setex(key, CACHE_TTL_SECONDS, json);
        } catch (Exception e) {
            System.err.println("Cache error: " + e.getMessage());
        } finally {
            if (jedis != null) jedis.close();
        }
    }

    public WeatherResponse get(String city) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String key = "weather:" + city.toLowerCase();
            String json = jedis.get(key);
            if (json != null) {
                return objectMapper.readValue(json, WeatherResponse.class);
            }
        } catch (Exception e) {
            System.err.println("Cache error: " + e.getMessage());
        } finally {
            if (jedis != null) jedis.close();
        }
        return null;
    }
}
