package com.google.sps.data;

public class ImageLabel {
  private String description;
  private float score;

  public ImageLabel(String description, float score) {
    this.description = description;
    this.score = score;
  }

  public String getDescription() {
    return description;
  }

  public float getScore() {
    return score;
  }

}