package com.proj.springsecrest.security;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitService {

    private static class RequestInfo {
        int count;
        long windowStart;
    }

    private final Map<String, RequestInfo> limiters = new ConcurrentHashMap<>();

    public synchronized boolean allowRequest(String key, int maxRequests, int durationInSeconds) {
        long currentWindow = Instant.now().getEpochSecond() / durationInSeconds;
        RequestInfo info = limiters.computeIfAbsent(key, k -> new RequestInfo());

        if (info.windowStart != currentWindow) {
            // New window, reset counter
            info.windowStart = currentWindow;
            info.count = 1;
            return true;
        }

        if (info.count < maxRequests) {
            info.count++;
            return true;
        }

        // Exceeded limit
        return false;
    }
}

