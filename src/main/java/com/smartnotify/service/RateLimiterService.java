package com.smartnotify.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class RateLimiterService {

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${app.rate-limit.max-requests}")
    private int maxRequests;

    @Value("${app.rate-limit.window-seconds}")
    private long windowSeconds;

    public boolean isAllowed(String recipient) {
        String key = "rate_limit:" + recipient;

        Long count = redisTemplate.opsForValue().increment(key);

        if (count == 1) {
            redisTemplate.expire(key, Duration.ofSeconds(windowSeconds));
        }

        log.info("Rate limit check for {}: {}/{} requests", recipient, count, maxRequests);

        return count <= maxRequests;
    }

    public long getRemainingRequests(String recipient) {
        String key = "rate_limit:" + recipient;
        String value = redisTemplate.opsForValue().get(key);
        if (value == null) return maxRequests;
        return Math.max(0, maxRequests - Long.parseLong(value));
    }
}