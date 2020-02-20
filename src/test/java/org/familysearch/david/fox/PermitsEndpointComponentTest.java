/*
 * Copyright (c) 2019  Intellectual Reserve, Inc.  All rights reserved.
 */
package org.familysearch.david.fox;

import java.net.URI;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import org.familysearch.david.fox.schema.local.BuildingPermitSchema;

import static org.junit.Assert.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class PermitsEndpointComponentTest {

  @SuppressWarnings("unused")
  @Autowired
  private TestRestTemplate testRestTemplate;

  @LocalServerPort
  int randomServerPort;

  @SuppressWarnings("unused")
  @Autowired
  private RestTemplate restTemplate; // note that this RestTemplate must be the one used by PermitsEndpoint

  private MockRestServiceServer mockServer;

  @Before
  public void setUp() {
    this.mockServer = MockRestServiceServer.createServer(restTemplate);
  }

  @Test
  public void normalFlowThroughBothLocalAndRemoteServices() {
    mockServer.reset();

    mockServer.expect(requestTo("http://properties-service/properties-by-permit-id/12345"))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withSuccess("{\"propertyOwner\": \"Mike Jones\"}", MediaType.APPLICATION_JSON));

    mockServer.expect(requestTo("http://loans-service/loans-by-permit-id/12345"))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withSuccess("{\"loanApproved\": \"true\"}", MediaType.APPLICATION_JSON));

    URI localServiceUrl = URI.create("http://localhost:" + randomServerPort + "/permits/building/12345");
    ResponseEntity<BuildingPermitSchema> buildingPermitResponse = testRestTemplate.exchange(RequestEntity.get(localServiceUrl)
        .accept(MediaType.APPLICATION_JSON)
        .build(), BuildingPermitSchema.class);

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

    URI localServiceUrl = URI.create("http://localhost:" + randomServerPort + "/permits/building/12345");
    ResponseEntity<BuildingPermitSchema> responseEntity = testRestTemplate.exchange(RequestEntity.get(localServiceUrl)
        .accept(MediaType.APPLICATION_JSON)
        .build(), BuildingPermitSchema.class);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    mockServer.verify(); //optional; this proves that the server call we expected was made
  }

  @Test
  public void remoteServerReturns400() {
    mockServer.reset();

    mockServer.expect(requestTo("http://properties-service/properties-by-permit-id/12345"))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withStatus(HttpStatus.BAD_REQUEST));

    URI localServiceUrl = URI.create("http://localhost:" + randomServerPort + "/permits/building/12345");
    ResponseEntity<BuildingPermitSchema> responseEntity = testRestTemplate.exchange(RequestEntity.get(localServiceUrl)
        .accept(MediaType.APPLICATION_JSON)
        .build(), BuildingPermitSchema.class);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    mockServer.verify(); //optional; this proves that the server call we expected was made
  }

  @Test
  public void localServerThrowsException() {
    mockServer.reset();

    mockServer.expect(requestTo("http://properties-service/properties-by-permit-id/12345"))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withSuccess()); // the body returned will be null, causing an exception

    URI localServiceUrl = URI.create("http://localhost:" + randomServerPort + "/permits/building/12345");
    ResponseEntity<BuildingPermitSchema> responseEntity = testRestTemplate.exchange(RequestEntity.get(localServiceUrl)
        .accept(MediaType.APPLICATION_JSON)
        .build(), BuildingPermitSchema.class);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    mockServer.verify(); //optional; this proves that the server call we expected was made
  }

}