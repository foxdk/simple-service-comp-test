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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.client.RestTemplate;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class MockMvcCallExampleTest {

  @SuppressWarnings("unused")
  @Autowired
  private MockMvc mvc;

  @SuppressWarnings("unused")
  @Autowired
  private RestTemplate restTemplate; // note that this RestTemplate must be the one used by MyFirstEndpoint above

  private MockRestServiceServer mockServer;

  @Before
  public void setUp() {
    this.mockServer = MockRestServiceServer.createServer(restTemplate);
  }

  @Test
  public void normalFlowThroughBothLocalAndRemoteServices() throws Exception {
    String remoteServiceResponseBody = "{\"remoteServiceResponseItem\": \"abc123\"}";
    String remoteServiceUrl = "http://some-remote-service/some-path";
    mockServer.reset();
    mockServer.expect(requestTo(remoteServiceUrl))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withSuccess(remoteServiceResponseBody, MediaType.APPLICATION_JSON));

    MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/first/endpoint").accept(MediaType.APPLICATION_JSON);

    mvc.perform(builder)
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("remoteServiceResponse.remoteServiceResponseItem")
            .value("abc123"));
    mockServer.verify(); //optional; this proves that the server call we expected was made
  }

}