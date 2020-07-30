package com.google.sps.data;
import java.util.ArrayList;
import java.util.List;
import com.google.appengine.api.datastore.GeoPt;

public class Business {
  private String businessId;
  private String businessDisplayName;
  private String street;
  private String city;
  private String state;
  private String zipCode;
  private GeoPt latLng;
  private List<String> productIds;
  private String tempVisionAnnotation;

  public Business(String businessId,
                  String businessDisplayName,
                  String street,
                  String city,
                  String state,
                  String zipCode,
                  GeoPt latLng,
                  List<String> productIds,
                  String tempVisionAnnotation) {
    this.businessId = businessId;
    this.businessDisplayName = businessDisplayName;
    this.street = street;
    this.city = city;
    this.state = state;
    this.zipCode = zipCode;
    this.latLng = latLng;
    this.productIds = productIds;
    this.tempVisionAnnotation = tempVisionAnnotation;
  }

  public String getBusinessId() {
    return businessId;
  }

  public String getBusinessDisplayName() {
    return businessDisplayName;
  }

  public String getStreet() {
    return street;
  }

  public String getCity() {
    return city;
  }

  public String getState() {
    return state;
  }

  public String getZipCode() {
    return zipCode;
  }

  public List<String> getProductIds() {
    return productIds;
  }

  public String getTempVisionAnnotation() {
    return tempVisionAnnotation;
  }

  public GeoPt getLatLng() {
    return latLng;
  }
}