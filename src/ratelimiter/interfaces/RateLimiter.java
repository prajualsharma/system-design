package ratelimiter.interfaces;

public interface RateLimiter  {
    boolean allowRequest(String clientId);
    String getAlgorithmName();
}
