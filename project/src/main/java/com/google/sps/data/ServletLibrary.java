package com.google.sps.data;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.Filter;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class ServletLibrary {
  public static String generateUUID() {
    return UUID.randomUUID().toString();
  }

  public static Entity checkAccountIsRegistered(DatastoreService datastore, String userId) {
    Filter filter = new FilterPredicate("userId", FilterOperator.EQUAL, userId);
    Query query = new Query("userId").setFilter(filter);
    PreparedQuery pq = datastore.prepare(query);
    Entity result = pq.asSingleEntity();
    return result;
  }

  public static Account retrieveAccountInfo(Entity entity, UserService userService) {
    String userId = entity.getProperty("userId").toString();
    String logoutUrl = userService.createLogoutURL("/index.html");
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