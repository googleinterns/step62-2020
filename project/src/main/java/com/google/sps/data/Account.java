package com.google.sps.data;
import java.util.ArrayList;
import java.util.List;
import com.google.appengine.api.datastore.GeoPt;

public class Account {
  private String userId;
  private String logoutUrl; 
  private String nickname;
  private String userEmail;
  private boolean isUserBusinessOwner;
  private List<String> searchHistory; 
  private String street;
  private String city;
  private String state;
  private String zipCode;
  private GeoPt latLng;

  public Account(String userId,
                 String logoutUrl,
                 String nickname,
                 String userEmail,
                 boolean isUserBusinessOwner,
                 List<String> searchHistory,
                 String street,
                 String city,
                 String state,
                 String zipCode,
                 GeoPt latLng) {
    this.userId = userId;
    this.logoutUrl = logoutUrl;
    this.nickname = nickname;
    this.userEmail = userEmail;
    this.isUserBusinessOwner = isUserBusinessOwner;
    this.searchHistory = searchHistory;
    this.street = street;
    this.city = city;
    this.state = state;
    this.zipCode = zipCode;
    this.latLng = latLng;
  }

  public List<String> getSearchHistory() {
    return searchHistory;
  }

  public boolean getIsUserBusinessOwner() {
    return isUserBusinessOwner;
  }

  public GeoPt getLatLng() {
    return latLng;
  }
}