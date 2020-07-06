package com.google.sps.data;
import java.util.ArrayList;
import java.util.List;

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

  public Account(String userId,
                 String logoutUrl,
                 String nickname,
                 String userEmail,
                 boolean isUserBusinessOwner,
                 List<String> searchHistory,
                 String street,
                 String city,
                 String state,
                 String zipCode) {
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
  }

  public List<String> getSearchHistory() {
    return searchHistory;
  }

  public boolean getIsUserBusinessOwner() {
    return isUserBusinessOwner;
  }
}