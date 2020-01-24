/*
 * Copyright (c) 2019  Intellectual Reserve, Inc.  All rights reserved.
 */
package org.familysearch.david.fox.schema.remote;

/**
 * The response object that the remote service returns.
 */
public class LoanSchema {
  private Boolean loanApproved;

  public Boolean getLoanApproved() {
    return loanApproved;
  }

  public void setLoanApproved(Boolean loanApproved) {
    this.loanApproved = loanApproved;
  }
}
