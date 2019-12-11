/*
 * Copyright (c) 2019  Intellectual Reserve, Inc.  All rights reserved.
 */
package org.familysearch.david.fox;

/**
 * The response object that this service returns.
 */
public class ResponseSchema {
  private RemoteServiceResponseSchema remoteServiceResponse;
  private int responseCodeHash;

  public RemoteServiceResponseSchema getRemoteServiceResponse() {
    return remoteServiceResponse;
  }

  public void setRemoteServiceResponse(RemoteServiceResponseSchema remoteServiceResponse) {
    this.remoteServiceResponse = remoteServiceResponse;
  }

  public int getResponseCodeHash() {
    return responseCodeHash;
  }

  public void setResponseCodeHash(int responseCodeHash) {
    this.responseCodeHash = responseCodeHash;
  }
}
