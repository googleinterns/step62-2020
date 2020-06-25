package com.google.sps.data;
import java.util.UUID;
import com.google.appengine.api.datastore.Entity;
import java.util.List;
import java.util.ArrayList;


public class ServletLibrary {
  public static String generateUUID() {
    return UUID.randomUUID().toString();
  }

  public static Account retrieveAccountInfo(Entity entity) {
    String userId = entity.getProperty("userId").toString();
    String logoutUrl = entity.getProperty("logoutUrl").toString();
    String nickname = entity.getProperty("nickname").toString();
    String userEmail = entity.getProperty("userEmail").toString();
    boolean isUserBusinessOwner = (boolean) entity.getProperty("isUserBusinessOwner");
    String businessId = entity.getProperty("businessId").toString();

    @SuppressWarnings("unchecked") // Documentation says to suppress warning this way
      List<String> searchHistory = (ArrayList<String>) entity.getProperty("searchHistory"); 
      
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