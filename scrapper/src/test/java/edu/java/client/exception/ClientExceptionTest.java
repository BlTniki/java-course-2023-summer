package edu.java.client.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.HttpClientErrorException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ClientExceptionTest {
    private static Arguments[] codes() {
        return new Arguments[] {
            Arguments.of(301, MovedPermanentlyClientException.class),
            Arguments.of(402, UnauthorizedClientException.class),
            Arguments.of(403, ForbiddenClientException.class),
            Arguments.of(404, ResourceNotFoundClientException.class),
            Arguments.of(418, ClientException.class),
            Arguments.of(500, ClientException.class),
        };
    }

    @ParameterizedTest
    @MethodSource("codes")
    @DisplayName("Проверим, что метод правильно оборачивает статус коды")
    void wrapException(int code, Class<? extends ClientException> expectedClass) {
        var e = mock(HttpClientErrorException.class);
        when(e.getStatusCode()).thenReturn(HttpStatusCode.valueOf(code));
        when(e.getMessage()).thenReturn("wololo");

        Class<? extends ClientException> actualClass = ClientException.wrapException(e).getClass();

        assertThat(actualClass).isEqualTo(expectedClass);
    }
}
