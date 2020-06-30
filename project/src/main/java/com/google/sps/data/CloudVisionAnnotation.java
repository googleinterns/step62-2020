package com.google.sps.data;
import java.util.List;

public class CloudVisionAnnotation {
  private String imageURL;
  private List<ImageLabel> genericLabels;
  private List<ImageLabel> webLabels;
  private List<String> webBestLabels;
  private List<String> webUrls;
  private List<String> textInImage;
  private List<ImageLabel> dominantColors;
  private List<ImageLabel> objectsInImage;
  private List<ImageLabel> logosInImage;


  public CloudVisionAnnotation(String imageURL,
                               List<ImageLabel> genericLabels,
                               List<ImageLabel> webLabels,
                               List<String> webBestLabels,
                               List<String> webUrls,
                               List<String> textInImage,
                               List<ImageLabel> dominantColors,
                               List<ImageLabel> objectsInImage,
                               List<ImageLabel> logosInImage) {
    this.imageURL = imageURL;
    this.genericLabels = genericLabels;
    this.webLabels = webLabels;
    this.webBestLabels = webBestLabels;
    this.webUrls = webUrls;
    this.textInImage = textInImage;
    this.dominantColors = dominantColors;
    this.objectsInImage = objectsInImage;
    this.logosInImage = logosInImage;
  }

  public List<ImageLabel> getGenericLabels() {
    return genericLabels;
  }

  public List<ImageLabel> getWebLabels() {
    return webLabels;
  }

  public List<String> getWebBestLabels() {
    return webBestLabels;
  }

  public List<String> getWebUrls() {
    return webUrls;
  }

  public List<String> getTextInImage() {
    return textInImage;
  }

  public List<ImageLabel> getDominantColors() {
    return dominantColors;
  }  

  public List<ImageLabel> getObjectsInImage() {
    return objectsInImage;
  }

  public List<ImageLabel> getLogosInImage() {
    return logosInImage;
  }
}