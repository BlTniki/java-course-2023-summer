package edu.java.client.scrapper.exception;

import edu.java.BotApplicationTests;
import edu.java.client.scrapper.exception.chat.ChatAlreadyExistException;
import edu.java.client.scrapper.exception.chat.ChatNotExistException;
import edu.java.client.scrapper.exception.link.AliasAlreadyExistException;
import edu.java.client.scrapper.exception.link.AliasNotExistException;
import edu.java.client.scrapper.exception.link.BadAliasException;
import edu.java.client.scrapper.exception.link.BadUrlException;
import edu.java.client.scrapper.exception.link.BadUrlNotSupportedException;
import edu.java.client.scrapper.exception.link.UrlAlreadyExistException;
import edu.java.client.scrapper.model.ErrorCode;
import edu.java.client.scrapper.model.ErrorResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatusCode;
import static org.assertj.core.api.Assertions.assertThat;

class ScrapperClientExceptionTest extends BotApplicationTests {
    public static Arguments[] codes() {
        return new Arguments[] {
            Arguments.of(ErrorCode.ALIAS_ALREADY_EXIST, AliasAlreadyExistException.class),
            Arguments.of(ErrorCode.ALIAS_NOT_FOUND, AliasNotExistException.class),
            Arguments.of(ErrorCode.ALIAS_VALIDATION_FAILED, BadAliasException.class),
            Arguments.of(ErrorCode.URL_ALREADY_EXIST, UrlAlreadyExistException.class),
            Arguments.of(ErrorCode.URL_NOT_SUPPORTED, BadUrlNotSupportedException.class),
            Arguments.of(ErrorCode.URL_VALIDATION_FAILED, BadUrlException.class),
            Arguments.of(ErrorCode.URL_NOT_FOUND, ScrapperClientException.class),
            Arguments.of(ErrorCode.TG_CHAT_ALREADY_EXIST, ChatAlreadyExistException.class),
            Arguments.of(ErrorCode.TG_CHAT_NOT_FOUND, ChatNotExistException.class),
            Arguments.of(ErrorCode.TG_CHAT_VALIDATION_FAILED, ScrapperClientException.class),
            Arguments.of(ErrorCode.BAD_REQUEST, ScrapperClientException.class),
            Arguments.of(ErrorCode.INTERNAL_SERVER_ERROR, ScrapperClientException.class),
            Arguments.of(null, ScrapperClientException.class)
        };
    }

    @ParameterizedTest
    @MethodSource("codes")
    @DisplayName("Проверим что мы правильно оборачиваем по кодам")
    void wrapException(ErrorCode errorCode, Class<? extends ScrapperClientException> expectedClass) {
        var r = new ErrorResponse(null, errorCode, null, null, null);
        var e = new ErrorResponseException(HttpStatusCode.valueOf(418), r);

        var actualClass = ScrapperClientException.wrapException(e);

        assertThat(actualClass)
            .isInstanceOf(expectedClass);
    }
}
