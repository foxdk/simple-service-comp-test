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

@SuppressWarnings("unused")
@RestController
@RequestMapping(path = "/first")
public class MyFirstEndpoint {

  @Autowired
  private RestTemplate restTemplate;

  @GetMapping(path = "/endpoint")
  public ResponseEntity<ResponseSchema> getAThing() {

    // make a call to a remote service, so that we have something interesting to test
    URI uri = URI.create("http://some-remote-service/some-path");
    ResponseEntity<RemoteServiceResponseSchema> responseEntity = restTemplate.exchange(RequestEntity.get(uri)
        .accept(MediaType.APPLICATION_JSON)
        .build(), RemoteServiceResponseSchema.class);

    // package up the response from the remote server and return our own schema object containing it
    ResponseSchema responseSchema = mapResponse(responseEntity.getBody());
    return ResponseEntity.ok(responseSchema);
  }

  private ResponseSchema mapResponse(RemoteServiceResponseSchema remoteServiceResponse) {
    if (remoteServiceResponse == null) {
      throw new IllegalArgumentException("remoteServiceResponse must not be null");
    }
    ResponseSchema responseSchema = new ResponseSchema();
    responseSchema.setRemoteServiceResponse(remoteServiceResponse);
    if (remoteServiceResponse.getRemoteServiceResponseItem() != null) {
      responseSchema.setResponseItemHash(remoteServiceResponse.getRemoteServiceResponseItem().hashCode());
    }
    return responseSchema;
  }
}
