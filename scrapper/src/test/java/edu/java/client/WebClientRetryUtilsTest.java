package edu.java.client;

import java.util.Collections;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.HttpClientErrorException;
import static org.assertj.core.api.Assertions.assertThat;

class WebClientRetryUtilsTest {
    @Test
    void buildDefaultRetryOnCodes_shouldContainAllNon2xxCodes() {
        Set<Integer> defaultRetryOnCodes = WebClientRetryUtils.buildDefaultRetryOnCodes();

        assertThat(defaultRetryOnCodes).hasSize(500 - 100); // Exclude 2xx codes
        assertThat(defaultRetryOnCodes).doesNotContain(200, 201, 202, 203, 204, 205, 206);
        assertThat(defaultRetryOnCodes).contains(400, 500);
    }

    @Test
    void buildFilter_shouldFilterOutNonRetryableStatusCodes() {
        Set<Integer> retryOnCodes = Collections.singleton(500);
        HttpClientErrorException exception = new HttpClientErrorException(HttpStatusCode.valueOf(500), "");

        boolean shouldRetry = WebClientRetryUtils.buildFilter(retryOnCodes).test(exception);

        assertThat(shouldRetry).isTrue();
    }

    @Test
    void buildFilter_shouldNotRetryOnNonConfiguredStatusCodes() {
        Set<Integer> retryOnCodes = Collections.singleton(500);
        HttpClientErrorException exception = new HttpClientErrorException(HttpStatusCode.valueOf(404), "");

        boolean shouldRetry = WebClientRetryUtils.buildFilter(retryOnCodes).test(exception);

        assertThat(shouldRetry).isFalse();
    }

//    @Test
//    void buildConstantRetry_shouldRetryWithConstantDelay() {
//        Retry retry = WebClientRetryUtils.buildConstantRetry(3, 100, e -> true);
//
//        StepVerifier.withVirtualTime(() -> Mono.error(new RuntimeException("test"))
//                .retryWhen(retry))
//            .thenAwait(Duration.ofMillis(300))
//            .verifyErrorMatches(e -> e.getMessage().contains("Retries exhausted"));
//    }
//
//    @Test
//    void buildExponentialRetry_shouldRetryWithExponentialDelay() {
//        Retry retry = WebClientRetryUtils.buildExponentialRetry(3, 100, e -> true);
//
//        StepVerifier.withVirtualTime(() -> Mono.error(new RuntimeException("test"))
//                .retryWhen(retry))
//            .thenAwait(Duration.ofMillis(800)) // 100ms + 200ms + 400ms + jitter
//            .verifyErrorMatches(e -> e.getMessage().contains("Retries exhausted"));
//    }
//
//    @Test
//    void buildLinearRetry_shouldRetryWithLinearDelay() {
//        Retry retry = WebClientRetryUtils.buildLinearRetry(3, 100, e -> true);
//
//        StepVerifier.withVirtualTime(() -> Mono.error(new RuntimeException("test"))
//                .retryWhen(retry))
//            .thenAwait(Duration.ofMillis(600)) // 100ms + 200ms + 300ms
//            .verifyErrorMatches(e -> e.getMessage().contains("Retries exhausted"));
//    }
}
