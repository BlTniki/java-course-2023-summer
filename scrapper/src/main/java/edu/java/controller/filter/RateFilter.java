package edu.java.controller.filter;

import edu.java.controller.limiter.RateLimiterService;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.web.servlet.HandlerExceptionResolver;

/**
 * Данный класс реализует функционал фильтрации запросов на основе исчерпания
 * выделенного кол-ва запросов для клиента на основе его айпи адреса.
 * Используется Bucket4j.
 */
public class RateFilter implements Filter {
    public static final int NANOSECONDS_IN_SECOND = 1_000_000_000;
    private final RateLimiterService rateLimiterService;
    private final HandlerExceptionResolver resolver;

    public RateFilter(RateLimiterService rateLimiterService, HandlerExceptionResolver resolver) {
        this.rateLimiterService = rateLimiterService;
        this.resolver = resolver;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        // provide to next chain any non http request
        if (!(servletRequest instanceof HttpServletRequest)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        Bucket bucket = rateLimiterService.resolveBucket(servletRequest.getRemoteAddr());
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (!probe.isConsumed()) {
            long waitForRefillInSeconds = probe.getNanosToWaitForRefill() / NANOSECONDS_IN_SECOND;
            resolver.resolveException(
                (HttpServletRequest) servletRequest,
                (HttpServletResponse) servletResponse,
                null,
                new TooManyRequestsException(waitForRefillInSeconds)
            );
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    public static class TooManyRequestsException extends RuntimeException {
        public final long waitForRefillInSeconds;

        public TooManyRequestsException(long waitForRefillInSeconds) {
            this.waitForRefillInSeconds = waitForRefillInSeconds;
        }
    }
}
