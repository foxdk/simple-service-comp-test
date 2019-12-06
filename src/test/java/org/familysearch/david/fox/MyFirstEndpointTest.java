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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.client.RestTemplate;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// todo: is this the best example of a simple test? go through tutorials
// todo: this project is really an example of component testing, not integration testing, so rename it

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class MyFirstEndpointTest {

  @SuppressWarnings("unused")
  @Autowired
  private MyFirstEndpoint myFirstEndpoint;

  // note that this RestTemplate must be the one used by MyFirstEndpoint above
  @SuppressWarnings("unused")
  @Autowired
  private RestTemplate restTemplate;

  @SuppressWarnings("unused")
  @Autowired
  private MockMvc mvc;

  private MockRestServiceServer mockServer;

  @Before
  public void setUp() {
    this.mockServer = MockRestServiceServer.createServer(restTemplate);
  }

  public void testGetAThing_directEndpointCall() {
    String serviceResponseBody = "{'field1': 'abcdef', 'field2': 1234 }";
    String url = "http://some-remote-service/some-path";
    this.mockServer.expect(requestTo(url))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withSuccess(serviceResponseBody, MediaType.APPLICATION_JSON));

    ResponseEntity<String> responseEntity = myFirstEndpoint.getAThing();

    assertEquals(serviceResponseBody, responseEntity.getBody());
  }

  @Test
  public void testGetAThing_restEndpointCall() throws Exception {
    String serviceResponseBody = "{'field1': 'abcdef', 'field2': 1234 }";
    String url = "http://some-remote-service/some-path";
    this.mockServer.expect(requestTo(url))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withSuccess(serviceResponseBody, MediaType.APPLICATION_JSON));

    mvc.perform(MockMvcRequestBuilders.get("/first/endpoint").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.content().string(equalTo(serviceResponseBody)));
  }

}