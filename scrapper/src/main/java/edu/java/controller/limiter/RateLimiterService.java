package edu.java.controller.limiter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Данный класс реализует функционал лимитирования запросов по айпи адресу.
 * Для лимитирования используется Bucket4j.
 */
public class RateLimiterService {
    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();
    private final Bandwidth limiter;

    public RateLimiterService(Bandwidth limiter) {
        this.limiter = limiter;
    }

    /**
     * Возвращает bucket данного клиента.
     * @param ipAddress айпи адрес клиента
     * @return bucket клиента
     */
    public @NotNull Bucket resolveBucket(@NotNull String ipAddress) {
        return cache.computeIfAbsent(ipAddress, this::newBucket);
    }

    private Bucket newBucket(String ipAddress) {
        return Bucket.builder()
            .addLimit(limiter)
            .build();
    }
}
