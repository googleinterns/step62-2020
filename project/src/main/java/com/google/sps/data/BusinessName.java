package com.google.sps.data;


public class BusinessName {
  private String businessId;
  private String businessDisplayName;

  public BusinessName(String businessId,
                      String businessDisplayName) {
    this.businessId = businessId;
    this.businessDisplayName = businessDisplayName;
  }

  public String getBusinessId() {
    return businessId;
  }

  public String getBusinessDisplayName() {
    return businessDisplayName;
  }
}