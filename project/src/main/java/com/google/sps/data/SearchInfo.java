package com.google.sps.data;
import java.util.ArrayList;
import java.util.List;

public class SearchInfo {
  private String searchId;
  private String textSearch;
  private String gcsUrl;
  private String imageUrl;
  private String userId;

  public SearchInfo(String searchId, String textSearch, String gcsUrl, 
                    String imageUrl, String userId) {
    this.searchId = searchId;
    this.textSearch = textSearch;
    this.gcsUrl = gcsUrl;
    this.imageUrl = imageUrl;
    this.userId = userId;
  }

  public String getSearchId() {
    return searchId;
  }

  public String getTextSearch() {
    return textSearch;
  }

  public String getGcsUrl() {
    return gcsUrl;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public String getUserId() {
    return userId;
  }

}