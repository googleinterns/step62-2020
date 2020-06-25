package com.google.sps.data;
import java.util.UUID;
import com.google.appengine.api.datastore.Entity;


public ServletLibrary() {
  public String generateUUID() {
    return UUID.randomUUID().toString();
  }

  public Account retrieveAccountInfo(Entity Entity) {
    String userId = entity.getProperty("userId").toString();
    String logoutUrl = entity.getProperty("logoutUrl").toString();
    String nickname = entity.getProperty("nickname").toString();
    String userEmail = entity.getProperty("userEmail").toString();
    boolean isUserBusinessOwner = (boolean) entity.getProperty("isUserBusinessOwner");
    String businessId = entity.getProperty("businessId").toString();
    List<String> searchHistory = entity.getProperty("searchHistory"); // Not exactly sure how typecasting will work in this case.
    String street = entity.getProperty("street").toString();
    String city = entity.getProperty("city").toString();
    String state = entity.getProperty("state").toString();
    String zipCode = entity.getProperty("zipcode").toString();

    return new Account(userId,
                       logoutUrl,
                       nickname,
                       userEmail,
                       isUserBusinessOwner,
                       businessId,
                       searchHistory,
                       street,
                       city,
                       state,
                       zipCode);
  }
}