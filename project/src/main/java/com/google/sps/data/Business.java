package com.google.sps.data;
import java.util.ArrayList;
import java.util.List;

public class Business {
	private String businessId;
  private String businessDisplayName;
	private String street;
  private String city;
  private String state;
  private String zipCode;
  private List<String> productIds;
  private String tempVisionAnnotation;

  public Business(String businessId,
                  String businessDisplayName,
                  String street,
                  String city,
                  String state,
                  String zipCode,
                  List<String> productIds,
                  String tempVisionAnnotation) {
    this.businessId = businessId;
    this.businessDisplayName = businessDisplayName;
    this.street = street;
    this.city = city;
    this.state = state;
    this.zipCode = zipCode;
    this.productIds = productIds;
    this.tempVisionAnnotation = tempVisionAnnotation;
  }

  public String getTempVisionAnnotation() {
    return tempVisionAnnotation;
  }
}