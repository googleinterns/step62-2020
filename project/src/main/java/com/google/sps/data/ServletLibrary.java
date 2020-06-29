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
import com.google.appengine.api.datastore.Query.SortDirection;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class ServletLibrary {
  // Generates a random unique identifier.
  public static String generateUUID() {
    return UUID.randomUUID().toString();
  }

  // Checks database to see if account exists, if it does, returns the entitu from datastore.
  public static Entity checkAccountIsRegistered(DatastoreService datastore, String userId) {
    Filter filter = new FilterPredicate("userId", FilterOperator.EQUAL, userId);
    Query query = new Query("Account").setFilter(filter);
    PreparedQuery pq = datastore.prepare(query);
    Entity result = pq.asSingleEntity();
    return result;
  }

  // Checks database to see if product set exists, if it does, returns the entity.
  public static Entity checkProductSetExists(DatastoreService datastore, String productSetDisplayName) {
    Filter filter = new FilterPredicate("productSetDisplayName", FilterOperator.EQUAL, productSetDisplayName);
    Query query = new Query("ProductSet").setFilter(filter);
    PreparedQuery pq = datastore.prepare(query);
    Entity result = pq.asSingleEntity();
    return result;
  }

  // Formats an account entity object from datastore into an account class.
  public static Account retrieveAccountInfo(Entity entity, UserService userService) {
    String userId = entity.getProperty("userId").toString();
    String logoutUrl = userService.createLogoutURL("/index.html");
    String nickname = entity.getProperty("nickname").toString();
    String userEmail = entity.getProperty("userEmail").toString();
    boolean isUserBusinessOwner = (boolean) entity.getProperty("isUserBusinessOwner");

    @SuppressWarnings("unchecked") // Documentation says to suppress warning this way
      List<String> searchHistory = (ArrayList<String>) entity.getProperty("searchHistory"); 
      
    String street = entity.getProperty("street").toString();
    String city = entity.getProperty("city").toString();
    String state = entity.getProperty("state").toString();
    String zipCode = entity.getProperty("zipCode").toString();

    return new Account(userId,
                       logoutUrl,
                       nickname,
                       userEmail,
                       isUserBusinessOwner,
                       searchHistory,
                       street,
                       city,
                       state,
                       zipCode);
  }

  // Returns a list of product set objects, taken from datastore.
  public static List<ProductSetEntity> listAllProductSets(DatastoreService datastore) {
    Query query = new Query("ProductSet").addSort("productSetDisplayName", SortDirection.ASCENDING);
    PreparedQuery pq = datastore.prepare(query);
    List<ProductSetEntity> results = new ArrayList<>();
    for (Entity entity : pq.asIterable()) {
      String productSetId = entity.getProperty("productSetId").toString();
      String productSetDisplayName = entity.getProperty("productSetDisplayName").toString();

      @SuppressWarnings("unchecked") // Documentation says to suppress warning this way
        List<String> productIds = (ArrayList<String>) entity.getProperty("productIds"); 

      results.add(new ProductSetEntity(productSetId, productSetDisplayName, productIds));
    }
    return results;
  }

  // Returns a business object containing all the business information stored in 
  // datastore.
  public static Business retrieveBusinessInfo(DatastoreService datastore, String businessId) {
    // Retrieving from datastore.
    Filter filter = new FilterPredicate("businessId", FilterOperator.EQUAL, businessId);
    Query query = new Query("Business").setFilter(filter);
    PreparedQuery pq = datastore.prepare(query);
    Entity entity = pq.asSingleEntity();
    
    // Formatting entity into the business class
    String businessDisplayName = entity.getProperty("businessDisplayName").toString();
    String street = entity.getProperty("street").toString();
    String city = entity.getProperty("city").toString();
    String state = entity.getProperty("state").toString();
    String zipCode = entity.getProperty("zipCode").toString();
    @SuppressWarnings("unchecked") // Documentation says to suppress warning this way
      List<String> productIds = (ArrayList<String>) entity.getProperty("productIds"); 
    String tempVisionAnnotation = entity.getProperty("tempVisionAnnotation").toString();

    return new Business(businessId,
                        businessDisplayName,
                        street,
                        city,
                        state,
                        zipCode,
                        productIds,
                        tempVisionAnnotation);
  }
}