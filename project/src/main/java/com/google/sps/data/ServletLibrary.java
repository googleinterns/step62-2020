package com.google.sps.data;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Text;
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

  // Returns a product set object with all the information stored in the database.
  // If there is no product set, returns null. 
  public static ProductSetEntity retrieveProductSetInfo(DatastoreService datastore, String inputQuery, boolean inputIsDisplayName) {
    Filter filter = new FilterPredicate("productSetId", FilterOperator.EQUAL, inputQuery);
    if (inputIsDisplayName) {
      filter = new FilterPredicate("productSetDisplayName", FilterOperator.EQUAL, inputQuery);
    }
    Query query = new Query("ProductSet").setFilter(filter);
    PreparedQuery pq = datastore.prepare(query);
    Entity entity = pq.asSingleEntity();

    if (entity == null) return null;

    // Retrieving properties from entity, and checking they are valid types.
    Object _productSetId = entity.getProperty("productSetId");
    Object _productSetDisplayName = entity.getProperty("productSetDisplayName");
    String productSetId;
    String productSetDisplayName;
    if ((_productSetId instanceof String) &&
        (_productSetDisplayName instanceof String)) {
      productSetId = _productSetId.toString();
      productSetDisplayName = _productSetDisplayName.toString();
    } else {
      System.err.println("Entity properties are of an incorrect type.");
      return null;
    }
    @SuppressWarnings("unchecked") // Documentation says to suppress warning this way
      List<String> productIds = (ArrayList<String>) entity.getProperty("productIds"); 

    return new ProductSetEntity(productSetId, productSetDisplayName, productIds);
  } 

  // Returns an Account object with all the information that is stored in datstore.
  // If the account is not in datastore, returns null.
  public static Account retrieveAccountInfo(DatastoreService datastore, UserService userService, String userId) {
    Filter filter = new FilterPredicate("userId", FilterOperator.EQUAL, userId);
    Query query = new Query("Account").setFilter(filter);
    PreparedQuery pq = datastore.prepare(query);
    Entity entity = pq.asSingleEntity();

    if (entity == null) return null;

    // Retrieve entity properties, and check if they are valid types.
    Object _nickname = entity.getProperty("nickname");
    Object _userEmail = entity.getProperty("userEmail");
    Object _isUserBusinessOwner = entity.getProperty("isUserBusinessOwner");
    Object _street = entity.getProperty("street");
    Object _city = entity.getProperty("city");
    Object _state = entity.getProperty("state");
    Object _zipCode = entity.getProperty("zipCode");
    String nickname;
    String userEmail;
    boolean isUserBusinessOwner;
    String street;
    String city;
    String state;
    String zipCode;

    if ((_nickname instanceof String) && 
        (_userEmail instanceof String) && 
        (_isUserBusinessOwner instanceof Boolean) &&
        (_street instanceof String) &&
        (_city instanceof String) &&
        (_state instanceof String) &&
        (_zipCode instanceof String)) {
      nickname = _nickname.toString();
      userEmail = _userEmail.toString();
      isUserBusinessOwner = (boolean) _isUserBusinessOwner;
      street = _street.toString();
      city = _city.toString();
      state = _state.toString();
      zipCode = _zipCode.toString();
    } else {
      System.err.println("Entity properties are of an incorrect type.");
      return null;
    }

    String logoutUrl = userService.createLogoutURL("/index.html");
    @SuppressWarnings("unchecked") // Documentation says to suppress warning this way
      List<String> searchHistory = (ArrayList<String>) entity.getProperty("searchHistory"); 
    

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
      // Retrieving properties from entity, and checking they are valid types.
      Object _productSetId = entity.getProperty("productSetId");
      Object _productSetDisplayName = entity.getProperty("productSetDisplayName");
      String productSetId;
      String productSetDisplayName;
      if ((_productSetId instanceof String) &&
          (_productSetDisplayName instanceof String)) {
        productSetId = _productSetId.toString();
        productSetDisplayName = _productSetDisplayName.toString();
      } else {
        System.err.println("Entity properties are of an incorrect type.");
        return null;
      }
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
    
    // If there is no business account, simply return null.
    if (entity == null) return null;

    // Formatting entity into the business class. Checking if the types are valid.
    Object _businessDisplayName = entity.getProperty("businessDisplayName");
    Object _street = entity.getProperty("street");
    Object _city = entity.getProperty("city");
    Object _state = entity.getProperty("state");
    Object _zipCode = entity.getProperty("zipCode");
    Object _annotationObject = entity.getProperty("tempVisionAnnotation");
    String businessDisplayName;
    String street;
    String city;
    String state;
    String zipCode;
    Text annotationObject;
    if ((_businessDisplayName instanceof String) &&
        (_street instanceof String) &&
        (_city instanceof String) &&
        (_state instanceof String) &&
        (_zipCode instanceof String) &&
        (_annotationObject instanceof Text)) {
      businessDisplayName = _businessDisplayName.toString();
      street = _street.toString();
      city = _city.toString();
      state = _state.toString();
      zipCode = _zipCode.toString();
      annotationObject = (Text) _annotationObject;
    } else {
      System.err.println("Entity properties are of an incorrect type.");
      return null;
    }
    @SuppressWarnings("unchecked") // Documentation says to suppress warning this way
      List<String> productIds = (ArrayList<String>) entity.getProperty("productIds"); 
    String tempVisionAnnotation = null;
    if (annotationObject != null) {
      tempVisionAnnotation = annotationObject.getValue();
    }

    return new Business(businessId,
                        businessDisplayName,
                        street,
                        city,
                        state,
                        zipCode,
                        productIds,
                        tempVisionAnnotation);
  }

  public static void addProductToLabels(DatastoreService datastore, String productId, List<String> labels) {
    for (String label : labels) {
      Filter filter = new FilterPredicate("label", FilterOperator.EQUAL, label.toLowerCase());
      Query query = new Query("ProductLabel").setFilter(filter);
      PreparedQuery pq = datastore.prepare(query);
      Entity entity = pq.asSingleEntity();
      // If the label doesnt already exist, create a new one and store. Otherwise,
      // add the product id to the list contained in the existing entity.
      if (entity == null) {
        Entity productLabel = new Entity("ProductLabel");
        productLabel.setProperty("label", label.toLowerCase());
        List<String> productIds = new ArrayList<>();
        productIds.add(productId);
        productLabel.setProperty("productIds", productIds);
        datastore.put(productLabel);
      } else {
        @SuppressWarnings("unchecked") // Documentation says to suppress warning this way
          List<String> productIds = (ArrayList<String>) entity.getProperty("productIds"); 
        productIds.add(productId);
        entity.setProperty("productIds", productIds);
        datastore.put(entity);
      }
    }
  }

  public static void addProductToProductSet(DatastoreService datastore, String productId, String productSetId) {
    Filter filter = new FilterPredicate("productSetId", FilterOperator.EQUAL, productSetId);
    Query query = new Query("ProductSet").setFilter(filter);
    PreparedQuery pq = datastore.prepare(query);
    Entity entity = pq.asSingleEntity();
    if (entity == null) {
      System.err.println("Product Set must be created first before adding a product!");
    } else {
      @SuppressWarnings("unchecked") // Documentation says to suppress warning this way
        List<String> productIds = (ArrayList<String>) entity.getProperty("productIds"); 
      productIds.add(productId);
      entity.setProperty("productIds", productIds);
      datastore.put(entity);
    }
  }

  public static void addProductToProductCategory(DatastoreService datastore, String productId, String productCategory) {
    Filter filter = new FilterPredicate("productCategory", FilterOperator.EQUAL, productCategory);
    Query query = new Query("ProductCategory").setFilter(filter);
    PreparedQuery pq = datastore.prepare(query);
    Entity entity = pq.asSingleEntity();
    // If the category doesnt already exist, create a new one and store. Otherwise,
    // add the product id to the list contained in the existing entity.
    if (entity == null) {
      entity = new Entity("ProductCategory");
      entity.setProperty("productCategory", productCategory);
      List<String> productIds = new ArrayList<>();
      productIds.add(productId);
      entity.setProperty("productIds", productIds);
      datastore.put(entity);
    } else {
      @SuppressWarnings("unchecked") // Documentation says to suppress warning this way
        List<String> productIds = (ArrayList<String>) entity.getProperty("productIds"); 
      productIds.add(productId);
      entity.setProperty("productIds", productIds);
      datastore.put(entity);
    }
  }

  public static void addProductToBusiness(DatastoreService datastore, String productId, String businessId) {
    Filter filter = new FilterPredicate("businessId", FilterOperator.EQUAL, businessId);
    Query query = new Query("Business").setFilter(filter);
    PreparedQuery pq = datastore.prepare(query);
    Entity entity = pq.asSingleEntity();
    if (entity == null) {
      System.err.println("Business must be created first before adding a product!");
    } else {
      @SuppressWarnings("unchecked") // Documentation says to suppress warning this way
        List<String> productIds = (ArrayList<String>) entity.getProperty("productIds"); 
      productIds.add(productId);
      entity.setProperty("productIds", productIds);
      datastore.put(entity);
    }
  }
}