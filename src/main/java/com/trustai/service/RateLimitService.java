package com.trustai.service;

import com.trustai.model.entity.User;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class RateLimitService {

    @Value("${app.security.rate-limit.default-limit:100}")
    private int defaultLimit;

    @Value("${app.security.rate-limit.default-window:60}")
    private int defaultWindow;

    @Value("#{${app.security.rate-limit.limits:{free:50,premium:500,enterprise:10000}}}")
    private Map<String, Integer> planLimits;

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    @Cacheable(value = "rateLimitBuckets", key = "#user.email")
    public Bucket resolveBucket(User user) {
        return cache.computeIfAbsent(user.getEmail(), key -> {
            int limit = getLimitForPlan(user.getPlan());
            Bandwidth limitBandwidth = Bandwidth.classic(
                    limit,
                    Refill.intervally(limit, Duration.ofSeconds(defaultWindow))
            );
            return Bucket.builder()
                    .addLimit(limitBandwidth)
                    .build();
        });
    }

    public boolean tryConsume(User user) {
        Bucket bucket = resolveBucket(user);
        return bucket.tryConsume(1);
    }

    public ConsumptionProbe tryConsumeAndReturnRemaining(User user) {
        Bucket bucket = resolveBucket(user);
        boolean consumed = bucket.tryConsume(1);
        long availableTokens = bucket.getAvailableTokens();
        return ConsumptionProbe.builder()
                .consumed(consumed)
                .remaining(availableTokens)
                .build();
    }

    private int getLimitForPlan(User.Plan plan) {
        if (plan == null) {
            return defaultLimit;
        }
        return planLimits.getOrDefault(plan.name().toLowerCase(), defaultLimit);
    }

    @lombok.Data
    @lombok.Builder
    public static class ConsumptionProbe {
        private boolean consumed;
        private long remaining;
    }
}

