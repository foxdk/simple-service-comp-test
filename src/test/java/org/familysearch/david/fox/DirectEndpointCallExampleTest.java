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
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

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
    String remoteServiceResponseBody = "{\"remoteServiceResponseCode\": \"abc123\"}";
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
    assertEquals("abc123", remoteServiceResponse.getRemoteServiceResponseCode());
    mockServer.verify(); //optional; this proves that the server call we expected was made
  }

}