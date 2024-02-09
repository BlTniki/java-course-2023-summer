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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScrapperSdkStub implements ScrapperSdk {
    private final Map<Long, User> users;
    private final Map<User, List<Link>> linksByUser;

    public ScrapperSdkStub() {
        this.users = new HashMap<>();
        this.linksByUser = new HashMap<>();
    }

    public ScrapperSdkStub(Map<Long, User> users, Map<User, List<Link>> links) {
        this.users = users;
        this.linksByUser = links;
    }

    @Override
    public void registerUser(long userTelegramId) throws UserAlreadyExistException {
        if (users.containsKey(userTelegramId)) {
            throw new UserAlreadyExistException("User already registered in db");
        }
        var user = new User(userTelegramId);
        users.put(userTelegramId, user);
        linksByUser.put(user, new ArrayList<>());
    }

    @Override
    public User getUser(long userTelegramId) throws UserNotExistException {
        if (!users.containsKey(userTelegramId)) {
            throw new UserNotExistException("User is not registered in db");
        }
        return users.get(userTelegramId);
    }

    @Override
    public void trackNewUrl(long userTelegramId, String url, String alias)
            throws UserNotExistException, UrlAlreadyExistException, AliasAlreadyExistException {
        var user = getUser(userTelegramId);
        var userLinks = getAllUserTracks(user.telegramId());

        if (userLinks.stream().anyMatch(link -> link.url().equals(url))) {
            throw new UrlAlreadyExistException("This url already exist");
        }
        if (userLinks.stream().anyMatch(link -> link.alias().equals(alias))) {
            throw new AliasAlreadyExistException("This alias already exist");
        }

        linksByUser.get(user).add(new Link(url, alias, user));
    }

    @Override
    public void trackNewUrl(long userTelegramId, String url)
        throws UserNotExistException, UrlAlreadyExistException, AliasAlreadyExistException {
        trackNewUrl(userTelegramId, url, url);
    }

    @Override
    public void untrackUrl(long userTelegramId, @NotEmpty String alias)
        throws UserNotExistException, LinkNotExistException {
        var link = getAllUserTracks(userTelegramId).stream()
            .filter(l -> l.alias().equals(alias))
            .findAny()
            .orElseThrow(() -> new LinkNotExistException("Failed to find link with this alias"));

        linksByUser.get(getUser(userTelegramId)).remove(link);
    }

    @Override
    public @NotNull List<Link> getAllUserTracks(long userTelegramId) throws UserNotExistException {
        var user = getUser(userTelegramId);
        return linksByUser.get(user);
    }
}
