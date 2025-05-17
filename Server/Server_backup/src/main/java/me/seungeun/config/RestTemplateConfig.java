package me.seungeun.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;


@Configuration
public class RestTemplateConfig {

    @Bean // Registers a RestTemplate bean to be used in the application context
    public RestTemplate restTemplate() {
        return new RestTemplate(); // Creates a default RestTemplate for making HTTP requests
    }
}

