package ratelimiter.strategies;
// ratelimiter/TokenBucket.java

import ratelimiter.interfaces.RateLimiter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TokenBucket implements RateLimiter {

    private final int capacity;
    private final double refillRatePerSecond;

    private final Map<String, Double> tokens = new ConcurrentHashMap<>();
    private final Map<String, Long> lastRefillTime = new ConcurrentHashMap<>();

    public TokenBucket(int capacity, double refillRatePerSecond) {
        this.capacity = capacity;
        this.refillRatePerSecond = refillRatePerSecond;
    }

    @Override
    public synchronized boolean allowRequest(String clientId) {
        long now = System.currentTimeMillis();
        tokens.putIfAbsent(clientId, (double) capacity);
        lastRefillTime.putIfAbsent(clientId, now);

        long elapsed = now - lastRefillTime.get(clientId);
        double newTokens = (elapsed / 1000.0) * refillRatePerSecond;
        double current = Math.min(capacity, tokens.get(clientId) + newTokens);

        lastRefillTime.put(clientId, now);

        if (current >= 1) {
            tokens.put(clientId, current - 1);
            return true;
        }
        tokens.put(clientId, current);
        return false;
    }

    @Override
    public String getAlgorithmName() {
        return "Token Bucket";
    }
}