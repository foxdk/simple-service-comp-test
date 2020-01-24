package org.familysearch.david.fox;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import org.familysearch.david.fox.schema.local.BuildingPermitSchema;
import org.familysearch.david.fox.schema.remote.LoanSchema;
import org.familysearch.david.fox.schema.remote.PropertySchema;

@SuppressWarnings("unused")
@RestController
@RequestMapping(path = "/permits")
public class PermitsEndpoint {

  @Autowired
  private RestTemplate restTemplate;

  @GetMapping(path = "/building/{permitId}")
  public ResponseEntity<BuildingPermitSchema> getBuildingPermit(@PathVariable("permitId") String permitId) {
    PropertySchema property = fetchProperty(permitId);
    LoanSchema loan = fetchLoan(permitId);
    BuildingPermitSchema buildingPermit = mapResponse(property, loan);
    return ResponseEntity.ok(buildingPermit);
  }

  private PropertySchema fetchProperty(String permitId) {
    URI uri = URI.create("http://properties-service/properties-by-permit-id/" + permitId);
    ResponseEntity<PropertySchema> propertyResponse = restTemplate.exchange(RequestEntity.get(uri)
        .accept(MediaType.APPLICATION_JSON)
        .build(), PropertySchema.class);

    PropertySchema property = propertyResponse.getBody();
    if (property == null) {
      throw new RuntimeException("properties service response body not expected to be null");
    }
    return property;
  }

  private LoanSchema fetchLoan(String permitId) {
    URI uri = URI.create("http://loans-service/loans-by-permit-id/" + permitId);
    ResponseEntity<LoanSchema> loanResponse = restTemplate.exchange(RequestEntity.get(uri)
        .accept(MediaType.APPLICATION_JSON)
        .build(), LoanSchema.class);

    LoanSchema loan = loanResponse.getBody();
    if (loan == null) {
      throw new RuntimeException("loans service response body not expected to be null");
    }
    return loan;
  }

  private BuildingPermitSchema mapResponse(PropertySchema property, LoanSchema loan) {
    BuildingPermitSchema buildingPermit = new BuildingPermitSchema();
    buildingPermit.setPropertyOwner(property.getPropertyOwner());
    buildingPermit.setLoanApproved(loan.getLoanApproved());
    return buildingPermit;
  }
}
