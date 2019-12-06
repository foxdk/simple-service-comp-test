package org.familysearch.david.fox;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Objects;

@SuppressWarnings("unused")
@RestController
@RequestMapping(path = "/first")
public class MyFirstEndpoint {

  @Autowired
  private RestTemplate restTemplate;

  @GetMapping(path = "/endpoint")
  public ResponseEntity<String> getAThing() {

    // make a call to a remote service, so that we have something interesting to test
    URI uri = URI.create("http://some-remote-service/some-path");
    ResponseEntity<String> result = restTemplate.exchange(RequestEntity.get(uri)
        .accept(MediaType.APPLICATION_JSON)
        .build(), String.class);

    return ResponseEntity.ok(Objects.requireNonNull(result.getBody()));
  }
}
