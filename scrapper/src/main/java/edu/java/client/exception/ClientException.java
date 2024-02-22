package edu.java.client.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.HttpClientErrorException;

public class ClientException extends Exception {
    public ClientException(String message) {
        super(message);
    }

    @SuppressWarnings({"checkstyle:MagicNumber", "RedundantLabeledSwitchRuleCodeBlock"})
    public static ClientException wrapException(HttpClientErrorException e) {

        HttpStatusCode statusCode = e.getStatusCode();

        ClientException exception;
        switch (statusCode.value()) {
            case 301 -> {
                exception = new MovedPermanentlyClientException("Resource moved permanently");
            }
            case 402 -> {
                exception = new UnauthorizedClientException("Bad credentials");
            }
            case 403 -> {
                exception = new ForbiddenClientException("Forbidden");
            }
            case 404 -> {
                exception = new ResourceNotFoundClientException("Resource not found");
            }
            default -> {
                exception = new ClientException("Unexpected error");
            }
        }

        return exception;
    }
}
