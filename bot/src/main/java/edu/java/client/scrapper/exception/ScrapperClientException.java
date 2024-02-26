package edu.java.client.scrapper.exception;

import edu.java.client.exception.ClientException;
import edu.java.client.scrapper.exception.chat.ChatAlreadyExistException;
import edu.java.client.scrapper.exception.chat.ChatNotExistException;
import edu.java.client.scrapper.exception.link.AliasAlreadyExistException;
import edu.java.client.scrapper.exception.link.AliasNotExistException;
import edu.java.client.scrapper.exception.link.BadAliasException;
import edu.java.client.scrapper.exception.link.BadUrlException;
import edu.java.client.scrapper.exception.link.BadUrlNotSupportedException;
import edu.java.client.scrapper.exception.link.UrlAlreadyExistException;

public class ScrapperClientException extends ClientException {
    public ScrapperClientException(String message, Throwable cause) {
        super(message, cause);
    }

    @SuppressWarnings({"RedundantLabeledSwitchRuleCodeBlock"})
    public static ScrapperClientException wrapException(ErrorResponseException e) {
        ScrapperClientException exception;
        switch (e.getErrorResponse().code()) {
            case TG_CHAT_NOT_FOUND -> {
                exception = new ChatNotExistException("Chat not exist", e);
            }
            case TG_CHAT_ALREADY_EXIST -> {
                exception = new ChatAlreadyExistException("Chat already exist", e);
            }
            case URL_ALREADY_EXIST -> {
                exception = new UrlAlreadyExistException("Url already exist", e);
            }
            case URL_VALIDATION_FAILED -> {
                exception = new BadUrlException("Url validation failed", e);
            }
            case URL_NOT_SUPPORTED -> {
                exception = new BadUrlNotSupportedException("Url not supported", e);
            }
            case ALIAS_NOT_FOUND -> {
                exception = new AliasNotExistException("Alias not exist", e);
            }
            case ALIAS_ALREADY_EXIST -> {
                exception = new AliasAlreadyExistException("Alias already exist", e);
            }
            case ALIAS_VALIDATION_FAILED -> {
                exception = new BadAliasException("Alias validation failed", e);
            }
            case null, default -> {
                exception = new ScrapperClientException("Unexpected error", e);
            }
        }
        return exception;
    }
}
