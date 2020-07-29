package com.google.sps.data;

public class ProductWithAddress {
  private ProductEntity product;
  private Address address;

  public ProductWithAddress(ProductEntity product, Address address) {
    this.product = product;
    this.address = address;
  }

  public ProductEntity getProduct() {
    return product;
  }

  public Address getAddress() {
    return address;
  }
}