package org.familysearch.david.fox;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@SuppressWarnings("unused")
@Configuration
public class ApplicationConfig {

  @Bean
  public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
    //todo: is this the simplest way to create a RestTemplate? Go through spring boot tutorials
    return restTemplateBuilder
        .requestFactory(() -> new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()))
        .build();
  }

}