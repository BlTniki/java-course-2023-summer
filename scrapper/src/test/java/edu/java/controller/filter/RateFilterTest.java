package edu.java.controller.filter;

import edu.java.ScrapperApplicationTests;
import edu.java.controller.limiter.RateLimiterService;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Vector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.web.servlet.HandlerExceptionResolver;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    public void testDoFilter_withProxy() throws Exception {
        Vector<String> v = new Vector<>();
        v.add("ahhh");
        v.add("Why there no Enumeration.of()");
        v.add("Or at least Vector.of");
        when(request.getHeaders(RateFilter.X_FORWARDED_FOR)).thenReturn(v.elements());
        when(rateLimiterService.resolveBucket(anyString())).thenReturn(bucket);
        when(bucket.tryConsumeAndReturnRemaining(1)).thenReturn(probe);
        when(probe.isConsumed()).thenReturn(true);

        rateFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void testDoFilter_withoutProxy() throws Exception {
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
