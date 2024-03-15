package edu.java.service.link.github;

import edu.java.controller.model.ErrorCode;
import edu.java.service.exception.ParseFailedException;
import edu.java.service.link.LinkParser;
import edu.java.service.link.model.LinkDescriptor;
import edu.java.service.link.model.ServiceType;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;

public class GithubLinkParser extends LinkParser {
    private static final String GITHUB_AUTHORITY = "github.com";
    private static final Pattern REPO_REGEX =
        Pattern.compile("^/(?<owner>[^/]+)/(?<repo>[^/]+)(/(?<additional>.+))?$");
    private static final String OWNER_KEY = "owner";
    private static final String REPO_KEY = "repo";
    private static final String ADDITIONAL_KEY = "additional";
    private static final String RESOURCE_KEY = "resource";

    @NotNull private static Map<String, String> createTrackedData(Matcher matcher) {
        String owner = matcher.group(OWNER_KEY);
        String repo = matcher.group(REPO_KEY);
        String additional = matcher.group(ADDITIONAL_KEY);

        if (additional != null) {
            throw new ParseFailedException(
                "This specific repo resource is not supported: " + additional,
                ErrorCode.URL_VALIDATION_FAILED
            );
        }

        Map<String, String> trackedData = new HashMap<>();
        trackedData.put(RESOURCE_KEY, ResourceType.REPO_ONLY.name());
        trackedData.put(OWNER_KEY, owner);
        trackedData.put(REPO_KEY, repo);
        return trackedData;
    }

    @Override
    public LinkDescriptor parse(URI url) throws ParseFailedException {
        // getAuthority() might be null
        if (!Objects.equals(url.getAuthority(), GITHUB_AUTHORITY)) {
            return parseNext(url);
        }

        String path = url.getPath();
        Matcher matcher = REPO_REGEX.matcher(path);
        if (!matcher.matches()) {
            throw new ParseFailedException(
                "Failed to parse path: " + path,
                ErrorCode.URL_VALIDATION_FAILED
            );
        }

        Map<String, String> trackedData = createTrackedData(matcher);

        return new LinkDescriptor(ServiceType.GitHub, trackedData);
    }
}
