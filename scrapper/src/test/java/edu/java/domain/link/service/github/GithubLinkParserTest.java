package edu.java.domain.link.service.github;

import edu.java.ScrapperApplicationTests;
import edu.java.controller.model.ErrorCode;
import edu.java.domain.exception.ParseFailedException;
import edu.java.domain.link.dto.LinkDescriptor;
import edu.java.domain.link.dto.ServiceType;
import java.net.URI;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GithubLinkParserTest extends ScrapperApplicationTests {
    private final GithubLinkParser githubLinkParser = new GithubLinkParser();

    @Test
    @DisplayName("Проверим, что мы корректно создаём дескриптор")
    public void parse_ValidGithubRepoUrl_ShouldReturnCorrectLinkDescriptor() {
        String validUrl = "https://github.com/owner/repo";
        URI uri = URI.create(validUrl);

        LinkDescriptor descriptor = githubLinkParser.parse(uri);

        assertThat(descriptor.serviceType()).isEqualTo(ServiceType.GitHub);
        assertThat(descriptor.trackedData())
            .containsEntry("resource", "REPO_ONLY")
            .containsEntry("owner", "owner")
            .containsEntry("repo", "repo");
    }

    @Test
    @DisplayName("Проверим, что мы отлавливаем невалидную ссылку")
    public void parse_InvalidGithubUrl_ShouldThrowParseFailedException() {
        String invalidUrl = "https://github.com/invalid_url";
        URI uri = URI.create(invalidUrl);

        assertThatThrownBy(() -> githubLinkParser.parse(uri))
            .isInstanceOf(ParseFailedException.class)
            .hasMessageContaining("Failed to parse path")
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.URL_VALIDATION_FAILED);
    }

    @Test
    @DisplayName("Проверим, что мы вызываем следующий парсер")
    public void parse_NonGithubUrl_ShouldCallNextParser() {
        String nonGithubUrl = "https://non-github.com/owner/repo";
        URI uri = URI.create(nonGithubUrl);

        assertThatThrownBy(() -> githubLinkParser.parse(uri))
            .isInstanceOf(ParseFailedException.class)
            .hasMessage("This service is not supported");
    }

    @Test
    @DisplayName("Проверим, что мы валидируем ресурс")
    public void parse_GithubUrlWithAdditionalResource_ShouldThrowParseFailedException() {
        String urlWithAdditionalResource = "https://github.com/owner/repo/additional";
        URI uri = URI.create(urlWithAdditionalResource);

        assertThatThrownBy(() -> githubLinkParser.parse(uri))
            .isInstanceOf(ParseFailedException.class)
            .hasMessageContaining("This specific repo resource is not supported")
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.URL_VALIDATION_FAILED);
    }
}
