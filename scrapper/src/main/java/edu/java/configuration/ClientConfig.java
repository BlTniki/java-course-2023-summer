package edu.java.configuration;

import edu.java.client.github.GitHubClient;
import edu.java.client.github.GitHubClientRestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.RestClient;

//@Validated
//@Configuration
//@ConfigurationProperties(prefix = "client")
public class ClientConfig {
//    @Autowired
//    private GitHub gitHub;
//    @Bean
//    public GitHubClient gitHubClient(RestClient.Builder builder) {
//        System.out.println("token: " + gitHub.getBaseUrl());
//        var c = new GitHubClientRestClient(builder);
//        return c;
//    }
}
