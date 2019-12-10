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
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class TestRestTemplateExampleTest {

  @SuppressWarnings("unused")
  @Autowired
  private RestTemplate restTemplate; // note that this RestTemplate must be the one used by MyFirstEndpoint above

  @SuppressWarnings("unused")
  @Autowired
  private TestRestTemplate testRestTemplate;

  @LocalServerPort
  int randomServerPort;

  private MockRestServiceServer mockServer;

  @Before
  public void setUp() {
    this.mockServer = MockRestServiceServer.createServer(restTemplate);
  }

  @Test
  public void testGetAThing_usingTestRestTemplate() {
    String serviceResponseBody = "{'field1': 'wxyz', 'field2': 9876 }";
    String url = "http://some-remote-service/some-path";
    mockServer.reset();
    mockServer.expect(requestTo(url))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withSuccess(serviceResponseBody, MediaType.APPLICATION_JSON));

    URI uri = URI.create("http://localhost:" + randomServerPort + "/first/endpoint");

    ResponseEntity<String> result = testRestTemplate.exchange(RequestEntity.get(uri)
        .accept(MediaType.APPLICATION_JSON)
        .build(), String.class);

    assertEquals(200, result.getStatusCodeValue());
    assertEquals(serviceResponseBody, result.getBody());

    mockServer.verify(); //optional; this proves that the server call we expected was made
  }

}