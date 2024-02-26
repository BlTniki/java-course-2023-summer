package edu.java.client.scrapper.exception;

import edu.java.client.exception.ClientException;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.HttpClientErrorException;

public class ScrapperClientException extends ClientException {
    public ScrapperClientException(String message, Throwable cause) {
        super(message, cause);
    }

//    @SuppressWarnings({"checkstyle:MagicNumber", "RedundantLabeledSwitchRuleCodeBlock"})
//    public static ScrapperClientException wrapException(HttpClientErrorException e) {
//        HttpStatusCode statusCode = e.getStatusCode();
//
//        ScrapperClientException exception;
//        switch (statusCode.value()) {
//            case 400 -> {
//                exception = new
//            }
//            default -> {
//                exception = new ScrapperClientException("Scrapper client return error", e);
//            }
//        }
//        return exception;
//    }
}
