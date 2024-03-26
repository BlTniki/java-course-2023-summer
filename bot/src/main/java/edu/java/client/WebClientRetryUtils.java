package edu.java.client;

import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.client.HttpClientErrorException;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

/**
 * Этот класс содержит методы для конфигурации retry стратегий.
 */
public final class WebClientRetryUtils {
    private WebClientRetryUtils() {
    }

    static final BiFunction<Long, Retry.RetrySignal, Throwable> EXCEPTION_GENERATOR = (maxAttempts, rs) ->
        Exceptions.retryExhausted("Retries exhausted: " + (
            rs.totalRetries() + "/" + maxAttempts
        ), rs.failure()
        );

    @SuppressWarnings("checkstyle:MagicNumber")
    public static @NotNull Set<Integer> buildDefaultRetryOnCodes() {
        Set<Integer> integerSet = new HashSet<>();

        for (int i = 100; i <= 599; i++) {
            if (i < 200 || i > 299) {
                integerSet.add(i);
            }
        }
        return integerSet;
    }

    /**
     * Создаёт фильтр по кодам ошибок.
     * @param retryOnCodes Коды ошибок, при которых следует повторить запрос
     * @return фильтр по кодам ошибок
     */
    public static @NotNull Predicate<Throwable> buildFilter(@Nullable Set<Integer> retryOnCodes) {
        final Set<Integer> efficientRetryOnCodes = retryOnCodes != null ? retryOnCodes : buildDefaultRetryOnCodes();

        return throwable -> {
            if (!(throwable instanceof HttpClientErrorException)) {
                return false;
            }
            return efficientRetryOnCodes.contains(
                ((HttpClientErrorException) throwable).getStatusCode().value()
            );
        };
    }

    /**
     * Возвращает стратегию retry с константной задержкой.
     * @param maxAttempts кол-во попыток
     * @param baseDelayMs длительность задержки в мс
     * @param filter фильтр ошибок для повторения
     * @return Константная стратегия retry
     */
    public static @NotNull Retry buildConstantRetry(
        long maxAttempts,
        long baseDelayMs,
        @NotNull Predicate<Throwable> filter
    ) {
        return Retry.fixedDelay(maxAttempts, Duration.ofMillis(baseDelayMs))
            .filter(filter);
    }

    /**
     * Возвращает стратегию retry с экспоненциальным увеличением задержки.
     * @param maxAttempts кол-во попыток
     * @param baseDelayMs длительность первой задержки в мс
     * @param filter фильтр ошибок для повторения
     * @return Стратегия retry с экспоненциальным увеличением задержки
     */
    public static @NotNull Retry buildExponentialRetry(
        long maxAttempts,
        long baseDelayMs,
        @NotNull Predicate<Throwable> filter
    ) {
        return Retry.backoff(maxAttempts, Duration.ofMillis(baseDelayMs))
            .filter(filter);
    }

    /**
     * Возвращает стратегию retry с линейным увеличением задержки.
     * Первая задержка равна baseDelayMs, далее она удваивается, следом утраивается
     * и так далее до maxAttempts.
     * @param maxAttempts кол-во попыток
     * @param baseDelayMs длительность первой задержки в мс
     * @param filter фильтр ошибок для повторения
     * @return Стратегия retry с экспоненциальным увеличением задержки
     */
    public static @NotNull Retry buildLinearRetry(
        long maxAttempts,
        long baseDelayMs,
        @NotNull Predicate<Throwable> filter
    ) {
        final Duration minBackoff = Duration.ofMillis(baseDelayMs);
        return Retry.from(
            t -> Flux.deferContextual(cv ->
                t.contextWrite(cv)
                    .concatMap(retryWhenState -> {
                        //capture the state immediately
                        Retry.RetrySignal copy = retryWhenState.copy();
                        Throwable currentFailure = copy.failure();
                        long iteration = copy.totalRetries();

                        if (currentFailure == null) {
                            return Mono.error(
                                new IllegalStateException("Retry.RetrySignal#failure() not expected to be null")
                            );
                        }

                        if (!filter.test(currentFailure)) {
                            return Mono.error(currentFailure);
                        }

                        if (iteration >= maxAttempts) {
                            return Mono.error(EXCEPTION_GENERATOR.apply(maxAttempts, copy));
                        }

                        Duration nextBackoff = minBackoff.multipliedBy(iteration);
                        return Mono.delay(nextBackoff, Schedulers.parallel());
                    })
            )
        );
    }
}
