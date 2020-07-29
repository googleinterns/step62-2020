package com.google.sps.data;

public class Address {
  private String street;
  private String city;
  private String state;
  private String zipCode;

  public Address(String street,
                 String city,
                 String state,
                 String zipCode) {
    this.street = street;
    this.city = city;
    this.state = state;
    this.zipCode = zipCode;
  }
}