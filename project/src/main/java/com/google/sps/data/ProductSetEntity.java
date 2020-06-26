package com.google.sps.data;
import java.util.ArrayList;
import java.util.List;

public class ProductSetEntity {
	private String productSetId;
	private String productSetDisplayName;
  private List<string> productIds;

  public ProductSetEntity(String productSetId, String productSetDisplayName, List<String> productIds) {
    this.productSetId = productSetId;
    this.productSetDisplayName = productSetDisplayName;
    this.productIds = productIds;
  }

  public String getProductSetId() {
    return productSetId;
  }
  
  public String getProductSetDisplayName() {
    return productSetDisplayName;
  }

  public List<String> getProductIds() {
    return productIds;
  }
}