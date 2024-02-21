package edu.java.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

//@Data
//@Configuration
//@ConfigurationProperties(prefix = "client.github")
public class GitHub {
    private String token;
    private String baseUrl;
}
