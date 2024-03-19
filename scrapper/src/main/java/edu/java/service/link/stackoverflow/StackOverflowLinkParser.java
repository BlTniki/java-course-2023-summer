package edu.java.service.link.stackoverflow;

import edu.java.controller.model.ErrorCode;
import edu.java.service.exception.ParseFailedException;
import edu.java.service.link.LinkParser;
import edu.java.service.link.model.LinkDescriptor;
import edu.java.service.link.model.ServiceType;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StackOverflowLinkParser extends LinkParser {
    private static final String STACKOVERFLOW_AUTHORITY = "stackoverflow.com";
    private static final Pattern RESOURCE_REGEX =
        Pattern.compile("^/(?<resource>[^/]+)/(?<id>\\d+)(/.+)?$");
    private static final String RESOURCE_KEY = "resource";
    private static final String ID_KEY = "id";

    private ResourceType parse(String raw) {
        ResourceType result = null;

        for (var resourceType : ResourceType.values()) {
            if (resourceType.raw.equals(raw)) {
                result = resourceType;
                break;
            }
        }

        if (result == null) {
            throw new ParseFailedException(
                "This resource is not supported: " + raw,
                ErrorCode.URL_VALIDATION_FAILED
            );
        }

        return result;
    }

    @Override
    public LinkDescriptor parse(URI url) throws ParseFailedException {
        if (url.getAuthority() == null || !url.getAuthority().endsWith(STACKOVERFLOW_AUTHORITY)) {
            return parseNext(url);
        }

        String path = url.getPath();
        Matcher matcher = RESOURCE_REGEX.matcher(path);
        if (!matcher.matches()) {
            throw new ParseFailedException(
                "Failed to parse path: " + path,
                ErrorCode.URL_VALIDATION_FAILED
            );
        }

        ResourceType resource = parse(matcher.group(RESOURCE_KEY));
        String id = matcher.group(ID_KEY);

        Map<String, String> trackedData = new HashMap<>();
        trackedData.put(RESOURCE_KEY, resource.name());
        trackedData.put(ID_KEY, id);

        return new LinkDescriptor(
            ServiceType.StackOverflow,
            trackedData
        );
    }
}
