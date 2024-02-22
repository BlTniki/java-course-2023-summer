package edu.java.client.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.HttpClientErrorException;

public class ClientException extends Exception {
    public ClientException(String message, Throwable cause) {
        super(message, cause);
    }

    @SuppressWarnings({"checkstyle:MagicNumber", "RedundantLabeledSwitchRuleCodeBlock"})
    public static ClientException wrapException(HttpClientErrorException e) {

        HttpStatusCode statusCode = e.getStatusCode();
        String msg = e.getMessage();

        ClientException exception;
        switch (statusCode.value()) {
            case 301 -> {
                exception = new MovedPermanentlyClientException("Resource moved permanently", e);
            }
            case 402 -> {
                exception = new UnauthorizedClientException("Bad credentials", e);
            }
            case 403 -> {
                exception = new ForbiddenClientException("Forbidden", e);
            }
            case 404 -> {
                exception = new ResourceNotFoundClientException("Resource not found", e);
            }
            default -> {
                exception = new ClientException("Unexpected error", e);
            }
        }

        return exception;
    }
}
