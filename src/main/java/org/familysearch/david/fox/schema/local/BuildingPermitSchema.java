/*
 * Copyright (c) 2019  Intellectual Reserve, Inc.  All rights reserved.
 */
package org.familysearch.david.fox.schema.local;

/**
 * The response object that this service returns.
 */
public class BuildingPermitSchema {
  private String propertyOwner;
  private Boolean loanApproved;

  public String getPropertyOwner() {
    return propertyOwner;
  }

  public void setPropertyOwner(String propertyOwner) {
    this.propertyOwner = propertyOwner;
  }

  public Boolean getLoanApproved() {
    return loanApproved;
  }

  public void setLoanApproved(Boolean loanApproved) {
    this.loanApproved = loanApproved;
  }
}
