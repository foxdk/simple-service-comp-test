/*
 * Copyright (c) 2019  Intellectual Reserve, Inc.  All rights reserved.
 */
package org.familysearch.david.fox;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

// todo: is this the best example of a simple test? go through tutorials

@RunWith(SpringRunner.class)
@SpringBootTest
public class MyFirstEndpointTest {

  @SuppressWarnings("unused")
  @Autowired
  private MyFirstEndpoint myFirstEndpoint;

  // note that this RestTemplate must be the one used by MyFirstEndpoint above
  @SuppressWarnings("unused")
  @Autowired
  private RestTemplate restTemplate;

  private MockRestServiceServer mockServer;

  @Before
  public void setUp() {
    this.mockServer = MockRestServiceServer.createServer(restTemplate);
  }

  @Test
  public void testMyService() {
    String serviceResponseBody = "{'field1': 'abcdef', 'field2': 1234 }";
    String url = "http://some-remote-service/some-path";
    this.mockServer.expect(requestTo(url))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withSuccess(serviceResponseBody, MediaType.APPLICATION_JSON));

    ResponseEntity<String> responseEntity = myFirstEndpoint.getAThing();

    assertEquals(serviceResponseBody, responseEntity.getBody());
  }

}