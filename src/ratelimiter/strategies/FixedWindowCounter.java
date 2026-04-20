// ratelimiter/FixedWindowCounter.java
package ratelimiter.strategies;

import ratelimiter.interfaces.RateLimiter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FixedWindowCounter implements RateLimiter {

    private final int maxRequests;
    private final long windowSizeMs;

    private final Map<String, Integer> requestCount = new ConcurrentHashMap<>();
    private final Map<String, Long> windowStart = new ConcurrentHashMap<>();

    public FixedWindowCounter(int maxRequests, long windowSizeMs) {
        this.maxRequests = maxRequests;
        this.windowSizeMs = windowSizeMs;
    }

    @Override
    public synchronized boolean allowRequest(String clientId) {
        long now = System.currentTimeMillis();
        windowStart.putIfAbsent(clientId, now);
        requestCount.putIfAbsent(clientId, 0);

        if (now - windowStart.get(clientId) >= windowSizeMs) {
            windowStart.put(clientId, now);
            requestCount.put(clientId, 0);
        }

        int count = requestCount.get(clientId);
        if (count < maxRequests) {
            requestCount.put(clientId, count + 1);
            return true;
        }
        return false;
    }

    @Override
    public String getAlgorithmName() {
        return "Fixed Window Counter";
    }
}