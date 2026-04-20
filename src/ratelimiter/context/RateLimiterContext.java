// RateLimiter/RateLimiterContext.java
package ratelimiter.context;

import ratelimiter.interfaces.RateLimiter;

public class RateLimiterContext {

    private RateLimiter strategy;

    public RateLimiterContext(RateLimiter strategy) {
        this.strategy = strategy;
    }

    // swap algorithm at runtime
    public void setStrategy(RateLimiter strategy) {
        this.strategy = strategy;
    }

    public boolean allowRequest(String clientId) {
        return strategy.allowRequest(clientId);
    }

    public String getCurrentAlgorithm() {
        return strategy.getAlgorithmName();
    }
}