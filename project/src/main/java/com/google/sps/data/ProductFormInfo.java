package com.google.sps.data;
import java.util.List;

public class ProductFormInfo {
  private CloudVisionAnnotation annotation;
  private List<String> labels;
  private String description;

  public ProductFormInfo(CloudVisionAnnotation annotation, List<String> labels, String description) {
    this.annotation = annotation;
    this.labels = labels;
    this.description = description;
  }
}