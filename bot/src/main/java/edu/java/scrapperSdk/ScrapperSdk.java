package edu.java.scrapperSdk;

import edu.java.scrapperSdk.exception.AliasAlreadyExistException;
import edu.java.scrapperSdk.exception.LinkNotExistException;
import edu.java.scrapperSdk.exception.UrlAlreadyExistException;
import edu.java.scrapperSdk.exception.UserAlreadyExistException;
import edu.java.scrapperSdk.exception.UserNotExistException;
import edu.java.scrapperSdk.model.Link;
import edu.java.scrapperSdk.model.User;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * Класс для общения со скраппером.
 */
public interface ScrapperSdk {
    void registerUser(long userTelegramId) throws UserAlreadyExistException;

    @NotNull User getUser(long userTelegramId) throws UserNotExistException;

    void trackNewUrl(long userTelegramId, @NotEmpty String url, @NotEmpty String alias)
    throws UserNotExistException, UrlAlreadyExistException, AliasAlreadyExistException;

    void trackNewUrl(long userTelegramId, @NotEmpty String url)
        throws UserNotExistException, UrlAlreadyExistException, AliasAlreadyExistException;

    void untrackUrl(long userTelegramId, @NotEmpty String alias) throws UserNotExistException, LinkNotExistException;

    @NotNull List<Link> getAllUserTracks(long userTelegramId) throws UserNotExistException;
}
