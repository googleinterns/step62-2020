package com.google.sps.data;
import com.google.appengine.api.datastore.GeoPt;

public class Address {
  private String street;
  private String city;
  private String state;
  private String zipCode;
  private GeoPt latLng;

  public Address(String street,
                 String city,
                 String state,
                 String zipCode,
                 GeoPt latLng) {
    this.street = street;
    this.city = city;
    this.state = state;
    this.zipCode = zipCode;
    this.latLng = latLng;
  }

  public GeoPt getLatLng() {
    return latLng;
  }
}