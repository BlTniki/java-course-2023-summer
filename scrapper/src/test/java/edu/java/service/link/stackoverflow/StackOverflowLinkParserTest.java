package edu.java.service.link.stackoverflow;

import edu.java.ScrapperApplicationTests;
import edu.java.service.exception.ParseFailedException;
import edu.java.service.link.model.LinkDescriptor;
import edu.java.service.link.model.ServiceType;
import java.net.URI;
import java.net.URISyntaxException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StackOverflowLinkParserTest extends ScrapperApplicationTests {
    private final StackOverflowLinkParser stackOverflowLinkParser = new StackOverflowLinkParser();

    @Test
    @DisplayName("Проверим, что мы корректно создаём дескриптор")
    public void testValidStackOverflowLink() throws URISyntaxException {
        URI validUri = new URI("https://stackoverflow.com/questions/12345678/example-question");

        LinkDescriptor descriptor = stackOverflowLinkParser.parse(validUri);

        assertThat(descriptor.serviceType()).isEqualTo(ServiceType.StackOverflow);
        assertThat(descriptor.trackedData()).containsEntry("resource", "QUESTIONS");
        assertThat(descriptor.trackedData()).containsEntry("id", "12345678");
    }

    @Test
    @DisplayName("Проверим, что мы отлавливаем невалидную ссылку")
    public void testInvalidStackOverflowLink() throws URISyntaxException {
        URI invalidUri = new URI("https://stackoverflow.com/invalid/12345678");

        assertThatThrownBy(() -> stackOverflowLinkParser.parse(invalidUri))
            .isInstanceOf(ParseFailedException.class)
            .hasMessageContaining("This resource is not supported: invalid");
    }

    @Test
    @DisplayName("Проверим, что мы отлавливаем невалидную ссылку без id вопроса")
    public void testStackOverflowLinkWithNoId() throws URISyntaxException {
        URI noIdUri = new URI("https://stackoverflow.com/questions/");

        assertThatThrownBy(() -> stackOverflowLinkParser.parse(noIdUri))
            .isInstanceOf(ParseFailedException.class)
            .hasMessageContaining("Failed to parse path");
    }

    @Test
    @DisplayName("Проверим, что мы вызываем следующий парсер")
    public void parse_NonGithubUrl_ShouldCallNextParser() {
        String nonGithubUrl = "https://non-github.com/owner/repo";
        URI uri = URI.create(nonGithubUrl);

        assertThatThrownBy(() -> stackOverflowLinkParser.parse(uri))
            .isInstanceOf(ParseFailedException.class)
            .hasMessage("This service is not supported");
    }
}
