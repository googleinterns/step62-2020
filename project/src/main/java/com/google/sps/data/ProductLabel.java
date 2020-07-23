package com.google.sps.data;
import java.util.ArrayList;
import java.util.List;

public class ProductLabel {
  private String productLabel;
  private List<String> productIds;

  public ProductLabel(String productLabel, List<String> productIds) {
    this.productLabel = productLabel;
    this.productIds = productIds;
  }

  public String getLabel() {
    return productLabel;
  }

  public List<String> getProductIds() {
    return productIds;
  }
}