/*
 * Copyright (c) 2019  Intellectual Reserve, Inc.  All rights reserved.
 */
package org.familysearch.david.fox;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import static org.junit.Assert.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class TestRestTemplateExampleTest {

  @SuppressWarnings("unused")
  @Autowired
  private TestRestTemplate testRestTemplate;

  @LocalServerPort
  int randomServerPort;

  @SuppressWarnings("unused")
  @Autowired
  private RestTemplate restTemplate; // note that this RestTemplate must be the one used by MyFirstEndpoint above

  private MockRestServiceServer mockServer;

  @Before
  public void setUp() {
    this.mockServer = MockRestServiceServer.createServer(restTemplate);
  }

  @Test
  public void normalFlowThroughBothLocalAndRemoteServices() {
    String remoteServiceResponseBody = "{\"remoteServiceResponseCode\": \"def456\"}";
    String remoteServiceUrl = "http://some-remote-service/some-path";
    mockServer.reset();
    mockServer.expect(requestTo(remoteServiceUrl))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withSuccess(remoteServiceResponseBody, MediaType.APPLICATION_JSON));

    URI localServiceUrl = URI.create("http://localhost:" + randomServerPort + "/first/endpoint");
    ResponseEntity<ResponseSchema> responseEntity = testRestTemplate.exchange(RequestEntity.get(localServiceUrl)
        .accept(MediaType.APPLICATION_JSON)
        .build(), ResponseSchema.class);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    ResponseSchema responseSchema = responseEntity.getBody();
    assertNotNull(responseSchema);
    RemoteServiceResponseSchema remoteServiceResponse = responseSchema.getRemoteServiceResponse();
    assertEquals("def456", remoteServiceResponse.getRemoteServiceResponseCode());
    mockServer.verify(); //optional; this proves that the server call we expected was made
  }

  @Test
  public void remoteServerReturns500() {
    String remoteServiceUrl = "http://some-remote-service/some-path";
    mockServer.reset();
    mockServer.expect(requestTo(remoteServiceUrl))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

    URI localServiceUrl = URI.create("http://localhost:" + randomServerPort + "/first/endpoint");
    ResponseEntity<ResponseSchema> responseEntity = testRestTemplate.exchange(RequestEntity.get(localServiceUrl)
        .accept(MediaType.APPLICATION_JSON)
        .build(), ResponseSchema.class);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    mockServer.verify(); //optional; this proves that the server call we expected was made
  }

  @Test
  public void remoteServerReturns400() {
    String remoteServiceUrl = "http://some-remote-service/some-path";
    mockServer.reset();
    mockServer.expect(requestTo(remoteServiceUrl))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withStatus(HttpStatus.BAD_REQUEST));

    URI localServiceUrl = URI.create("http://localhost:" + randomServerPort + "/first/endpoint");
    ResponseEntity<ResponseSchema> responseEntity = testRestTemplate.exchange(RequestEntity.get(localServiceUrl)
        .accept(MediaType.APPLICATION_JSON)
        .build(), ResponseSchema.class);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    mockServer.verify(); //optional; this proves that the server call we expected was made
  }

}