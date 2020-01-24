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
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class DirectEndpointCallExampleTest {

  @SuppressWarnings("unused")
  @Autowired
  private MyFirstEndpoint myFirstEndpoint;

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
    String remoteServiceResponseBody = "{\"remoteServiceResponseItem\": \"abc123\"}";
    String remoteServiceUrl = "http://some-remote-service/some-path";
    mockServer.reset();
    mockServer.expect(requestTo(remoteServiceUrl))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withSuccess(remoteServiceResponseBody, MediaType.APPLICATION_JSON));

    ResponseEntity<ResponseSchema> responseEntity = myFirstEndpoint.getAThing();

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    ResponseSchema responseSchema = responseEntity.getBody();
    assertNotNull(responseSchema);
    RemoteServiceResponseSchema remoteServiceResponse = responseSchema.getRemoteServiceResponse();
    assertEquals("abc123", remoteServiceResponse.getRemoteServiceResponseItem());
    mockServer.verify(); //optional; this proves that the server call we expected was made
  }

  @Test
  public void remoteServerReturns500() {
    String remoteServiceUrl = "http://some-remote-service/some-path";
    mockServer.reset();
    mockServer.expect(requestTo(remoteServiceUrl))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

    try {
      myFirstEndpoint.getAThing();
      fail("expected exception");
    }
    catch (HttpServerErrorException e) {
      assertTrue(e.getMessage(), true);
    }

    mockServer.verify(); //optional; this proves that the server call we expected was made
  }

  @Test
  public void remoteServerReturns400() {
    String remoteServiceUrl = "http://some-remote-service/some-path";
    mockServer.reset();
    mockServer.expect(requestTo(remoteServiceUrl))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withStatus(HttpStatus.BAD_REQUEST));

    try {
      myFirstEndpoint.getAThing();
      fail("expected exception");
    }
    catch (HttpClientErrorException e) {
      assertTrue(e.getMessage(), true);
    }

    mockServer.verify(); //optional; this proves that the server call we expected was made
  }

  @Test
  public void localServerThrowsException() {
    String remoteServiceUrl = "http://some-remote-service/some-path";
    mockServer.reset();
    mockServer.expect(requestTo(remoteServiceUrl))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withSuccess()); // the body returned will be null, causing an exception

    try {
      myFirstEndpoint.getAThing();
      fail("expected exception");
    }
    catch (IllegalArgumentException e) {
      assertTrue(e.getMessage(), true);
    }

    mockServer.verify(); //optional; this proves that the server call we expected was made
  }


}