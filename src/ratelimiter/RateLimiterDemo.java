// RateLimiter/RateLimiterDemo.java
package ratelimiter;

import ratelimiter.context.RateLimiterContext;
import ratelimiter.interfaces.RateLimiter;
import ratelimiter.strategies.FixedWindowCounter;
import ratelimiter.strategies.SlidingWindowCounter;
import ratelimiter.strategies.SlidingWindowLog;
import ratelimiter.strategies.TokenBucket;

import java.util.Scanner;

public class RateLimiterDemo {

    public static void main(String[] args) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Pick a rate limiting algorithm:");
        System.out.println("1. Token Bucket");
        System.out.println("2. Fixed Window Counter");
        System.out.println("3. Sliding Window Log");
        System.out.println("4. Sliding Window Counter");
        System.out.print("Choice: ");

        int choice = scanner.nextInt();

        RateLimiter algo = switch (choice) {
            case 1 -> new TokenBucket(5, 1);
            case 2 -> new FixedWindowCounter(5, 1000);
            case 3 -> new SlidingWindowLog(5, 1000);
            case 4 -> new SlidingWindowCounter(5, 1000);
            default -> throw new IllegalArgumentException("Invalid choice");
        };

        RateLimiterContext context = new RateLimiterContext(algo);

        System.out.println("\n--- Running: " + context.getCurrentAlgorithm() + " ---");

        for (int i = 1; i <= 7; i++) {
            boolean allowed = context.allowRequest("client1");
            System.out.println("Request " + i + ": " + (allowed ? "ALLOWED" : "BLOCKED"));
        }

        Thread.sleep(1100);
        System.out.println("After 1s cooldown:");
        System.out.println("Request 8: " + (context.allowRequest("client1") ? "ALLOWED" : "BLOCKED"));

        // swap strategy at runtime
        System.out.println("\nSwapping to Token Bucket...");
        context.setStrategy(new TokenBucket(5, 1));
        System.out.println("Now using: " + context.getCurrentAlgorithm());
    }
}