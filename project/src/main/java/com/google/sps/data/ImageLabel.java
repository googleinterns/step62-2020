package com.google.sps.data;

public class ImageLabel {
  private String description;
  private float score;

  public ImageLabel(String description, float score) {
    this.description = description;
    this.score = score;
  }

  // we only really care about the description when comparing two given labels.
  @Override
  public int hashCode() {
    return description.hashCode();
  }
  
  @Override
  public boolean equals(Object obj) {
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    ImageLabel other = (ImageLabel) obj;
    return (description.toLowerCase().equals(other.getDescription().toLowerCase()));
  }

  public String getDescription() {
    return description;
  }

  public float getScore() {
    return score;
  }

}