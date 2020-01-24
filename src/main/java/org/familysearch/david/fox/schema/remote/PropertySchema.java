/*
 * Copyright (c) 2019  Intellectual Reserve, Inc.  All rights reserved.
 */
package org.familysearch.david.fox.schema.remote;

/**
 * The response object that the remote service returns.
 */
public class PropertySchema {
  private String propertyOwner;

  public String getPropertyOwner() {
    return propertyOwner;
  }

  public void setPropertyOwner(String propertyOwner) {
    this.propertyOwner = propertyOwner;
  }
}
