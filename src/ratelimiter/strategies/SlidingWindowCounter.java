// ratelimiter/SlidingWindowCounter.java
package ratelimiter.strategies;

import ratelimiter.interfaces.RateLimiter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SlidingWindowCounter implements RateLimiter {

    private final int maxRequests;
    private final long windowSizeMs;

    private final Map<String, Integer> prevCount = new ConcurrentHashMap<>();
    private final Map<String, Integer> currCount = new ConcurrentHashMap<>();
    private final Map<String, Long> windowStart = new ConcurrentHashMap<>();

    public SlidingWindowCounter(int maxRequests, long windowSizeMs) {
        this.maxRequests = maxRequests;
        this.windowSizeMs = windowSizeMs;
    }

    @Override
    public synchronized boolean allowRequest(String clientId) {
        long now = System.currentTimeMillis();
        windowStart.putIfAbsent(clientId, now);
        prevCount.putIfAbsent(clientId, 0);
        currCount.putIfAbsent(clientId, 0);

        long elapsed = now - windowStart.get(clientId);

        if (elapsed >= windowSizeMs) {
            prevCount.put(clientId, currCount.get(clientId));
            currCount.put(clientId, 0);
            windowStart.put(clientId, now);
            elapsed = 0;
        }

        // weighted count = prev window's count * overlap ratio + current count
        double overlap = 1.0 - (double) elapsed / windowSizeMs;
        double estimated = prevCount.get(clientId) * overlap + currCount.get(clientId);

        if (estimated < maxRequests) {
            currCount.put(clientId, currCount.get(clientId) + 1);
            return true;
        }
        return false;
    }

    @Override
    public String getAlgorithmName() {
        return "Sliding Window Counter";
    }
}