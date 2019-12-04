package org.familysearch.david.fox;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SuppressWarnings("unused")
@RestController
@RequestMapping(path = "/first")
public class MyFirstEndpoint {

  @GetMapping(path = "/endpoint")
  public ResponseEntity<String> getAThing() {
    return ResponseEntity.ok("a thing");
  }
}
