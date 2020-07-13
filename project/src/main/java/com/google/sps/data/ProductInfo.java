package com.google.sps.data;

public class ProductInfo {
  private ProductEntity product;
  private ProductSetEntity productSet;
  private Business business;

  public ProductInfo(ProductEntity product, ProductSetEntity productSet, Business business) {
    this.product = product;
    this.productSet = productSet;
    this.business = business;
  }
}