// ratelimiter/SlidingWindowLog.java
package ratelimiter.strategies;

import ratelimiter.interfaces.RateLimiter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SlidingWindowLog implements RateLimiter {

    private final int maxRequests;
    private final long windowSizeMs;

    private final Map<String, Deque<Long>> logs = new ConcurrentHashMap<>();

    public SlidingWindowLog(int maxRequests, long windowSizeMs) {
        this.maxRequests = maxRequests;
        this.windowSizeMs = windowSizeMs;
    }

    @Override
    public synchronized boolean allowRequest(String clientId) {
        long now = System.currentTimeMillis();
        logs.putIfAbsent(clientId, new ArrayDeque<>());
        Deque<Long> log = logs.get(clientId);

        // remove timestamps outside the window
        while (!log.isEmpty() && now - log.peekFirst() >= windowSizeMs) {
            log.pollFirst();
        }

        if (log.size() < maxRequests) {
            log.addLast(now);
            return true;
        }
        return false;
    }

    @Override
    public String getAlgorithmName() {
        return "Sliding Window Log";
    }
}