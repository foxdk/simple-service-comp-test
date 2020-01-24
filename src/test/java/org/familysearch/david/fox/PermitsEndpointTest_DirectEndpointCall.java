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

import org.familysearch.david.fox.schema.local.BuildingPermitSchema;

import static org.junit.Assert.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class PermitsEndpointTest_DirectEndpointCall {

  @SuppressWarnings("unused")
  @Autowired
  private PermitsEndpoint permitsEndpoint; // this is our service's endpoint -- the endpoint under test

  @SuppressWarnings("unused")
  @Autowired
  private RestTemplate restTemplate; // note that this RestTemplate must be the one used by PermitsEndpoint above

  private MockRestServiceServer mockServer;

  @Before
  public void setUp() {
    this.mockServer = MockRestServiceServer.createServer(restTemplate);
  }

  @Test
  public void normalFlowThroughLocalAndRemoteServices() {
    mockServer.reset();

    mockServer.expect(requestTo("http://properties-service/properties-by-permit-id/12345"))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withSuccess("{\"propertyOwner\": \"Mike Jones\"}", MediaType.APPLICATION_JSON));

    mockServer.expect(requestTo("http://loans-service/loans-by-permit-id/12345"))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withSuccess("{\"loanApproved\": \"true\"}", MediaType.APPLICATION_JSON));

    ResponseEntity<BuildingPermitSchema> buildingPermitResponse = permitsEndpoint.getBuildingPermit("12345");

    assertEquals(HttpStatus.OK, buildingPermitResponse.getStatusCode());
    BuildingPermitSchema buildingPermit = buildingPermitResponse.getBody();
    assertNotNull(buildingPermit);
    assertTrue(buildingPermit.getLoanApproved());
    assertEquals("Mike Jones", buildingPermit.getPropertyOwner());
    mockServer.verify(); //optional; this proves that the server call we expected was made
  }

  @Test
  public void remoteServerReturns500() {
    mockServer.reset();

    mockServer.expect(requestTo("http://properties-service/properties-by-permit-id/12345"))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

    try {
      permitsEndpoint.getBuildingPermit("12345");
      fail("expected exception");
    }
    catch (HttpServerErrorException e) {
      assertTrue(e.getMessage(), true);
    }

    mockServer.verify(); //optional; this proves that the server call we expected was made
  }

  @Test
  public void remoteServerReturns400() {
    mockServer.reset();

    mockServer.expect(requestTo("http://properties-service/properties-by-permit-id/12345"))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withStatus(HttpStatus.BAD_REQUEST));

    try {
      permitsEndpoint.getBuildingPermit("12345");
      fail("expected exception");
    }
    catch (HttpClientErrorException e) {
      assertTrue(e.getMessage(), true);
    }

    mockServer.verify(); //optional; this proves that the server call we expected was made
  }

  @Test
  public void localServerThrowsException() {
    mockServer.reset();

    mockServer.expect(requestTo("http://properties-service/properties-by-permit-id/12345"))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withSuccess()); // the body returned will be null, causing an exception

    try {
      permitsEndpoint.getBuildingPermit("12345");
      fail("expected exception");
    }
    catch (RuntimeException e) {
      assertEquals("properties service response body not expected to be null", e.getMessage());
    }

    mockServer.verify(); //optional; this proves that the server call we expected was made
  }

}