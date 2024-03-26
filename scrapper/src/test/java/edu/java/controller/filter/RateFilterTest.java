package edu.java.controller.filter;

import edu.java.ScrapperApplicationTests;
import edu.java.controller.limiter.RateLimiterService;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.web.servlet.HandlerExceptionResolver;

import static org.mockito.Mockito.*;

public class RateFilterTest extends ScrapperApplicationTests {

    @Mock
    private RateLimiterService rateLimiterService;

    @Mock
    private HandlerExceptionResolver resolver;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private Bucket bucket;

    @Mock
    private ConsumptionProbe probe;

    private RateFilter rateFilter;

    @BeforeEach
    public void setup() {
        rateFilter = new RateFilter(rateLimiterService, resolver);
    }

    @Test
    public void testDoFilter() throws Exception {
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(rateLimiterService.resolveBucket(anyString())).thenReturn(bucket);
        when(bucket.tryConsumeAndReturnRemaining(1)).thenReturn(probe);
        when(probe.isConsumed()).thenReturn(true);

        rateFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void testDoFilter_WhenBucketIsFull() throws Exception {
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(rateLimiterService.resolveBucket(anyString())).thenReturn(bucket);
        when(bucket.tryConsumeAndReturnRemaining(1)).thenReturn(probe);
        when(probe.isConsumed()).thenReturn(false);
        when(probe.getNanosToWaitForRefill()).thenReturn(1000000000L);

        rateFilter.doFilter(request, response, filterChain);

        verify(resolver).resolveException(any(), any(), any(), any());
        verify(filterChain).doFilter(request, response);
    }
}
