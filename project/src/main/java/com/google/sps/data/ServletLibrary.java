package com.google.sps.data;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.GeoPt;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
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
    if (datastore == null) {
      System.err.println("RetrieveProductSetInfo: Datastore is null!");
      return null;
    }
    Filter filter = new FilterPredicate("productSetId", FilterOperator.EQUAL, inputQuery);
    if (inputIsDisplayName) {
      filter = new FilterPredicate("productSetDisplayName", FilterOperator.EQUAL, inputQuery);
    }
    Query query = new Query("ProductSet").setFilter(filter);
    PreparedQuery pq = datastore.prepare(query);
    Entity entity = pq.asSingleEntity();

    if (entity == null) return null;

    // Retrieving properties from entity, and checking they are valid types.
    String productSetId = getPropertyStringNotNull(entity, "productSetId");
    String productSetDisplayName = getPropertyStringNotNull(entity, "productSetDisplayName");
    List<String> productIds = getPropertyStringList(entity, "productIds");

    return new ProductSetEntity(productSetId, productSetDisplayName, productIds);
  } 

  // Returns an Account object with all the information that is stored in datstore.
  // If the account is not in datastore, returns null.
  public static Account retrieveAccountInfo(DatastoreService datastore, UserService userService, String userId) {
    if (datastore == null) {
      System.err.println("RetrieveAccountInfo: Datastore is null!");
      return null;
    }
    if (userService == null) {
      System.err.println("RetrieveAccountInfo: UserService is null!");
      return null;
    }
    Filter filter = new FilterPredicate("userId", FilterOperator.EQUAL, userId);
    Query query = new Query("Account").setFilter(filter);
    PreparedQuery pq = datastore.prepare(query);
    Entity entity = pq.asSingleEntity();

    if (entity == null) return null;

    // Retrieve entity properties, and check if they are valid types.
    String nickname = getPropertyStringNotNull(entity, "nickname");
    String userEmail = getPropertyStringNotNull(entity, "userEmail");
    String street = getPropertyStringNotNull(entity, "street");
    String city = getPropertyStringNotNull(entity, "city");
    String state = getPropertyStringNotNull(entity, "state");
    String zipCode = getPropertyStringNotNull(entity, "zipCode");
    Object _isUserBusinessOwner = entity.getProperty("isUserBusinessOwner");
    boolean isUserBusinessOwner;
    Object _latLng = entity.getProperty("latLng");
    GeoPt latLng;
    if ((_isUserBusinessOwner instanceof Boolean) &&
        (_latLng instanceof GeoPt)) {
      isUserBusinessOwner = (boolean) _isUserBusinessOwner;
      latLng = (GeoPt) _latLng;
    } else {
      System.err.println("Entity properties are of an incorrect type.");
      return null;
    }

    String logoutUrl = userService.createLogoutURL("/index.html");
    List<String> searchHistory = getPropertyStringList(entity, "searchHistory");

    return new Account(userId,
                       logoutUrl,
                       nickname,
                       userEmail,
                       isUserBusinessOwner,
                       searchHistory,
                       street,
                       city,
                       state,
                       zipCode,
                       latLng);
  }

  // Returns a list of product set objects, taken from datastore.
  public static List<ProductSetEntity> listAllProductSets(DatastoreService datastore) {
    if (datastore == null) {
      System.err.println("ListAllProductSets: Datastore is null!");
      return null;
    }
    Query query = new Query("ProductSet").addSort("productSetDisplayName", SortDirection.ASCENDING);
    PreparedQuery pq = datastore.prepare(query);
    List<ProductSetEntity> results = new ArrayList<>();
    for (Entity entity : pq.asIterable()) {
      // Retrieving properties from entity, and checking they are valid types.
      String productSetId = getPropertyStringNotNull(entity, "productSetId");
      String productSetDisplayName = getPropertyStringNotNull(entity, "productSetDisplayName");
      List<String> productIds = getPropertyStringList(entity, "productIds");

      results.add(new ProductSetEntity(productSetId, productSetDisplayName, productIds));
    }
    return results;
  }

  // Returns a business object containing all the business information stored in 
  // datastore.
  public static Business retrieveBusinessInfo(DatastoreService datastore, String businessId) {
    if (datastore == null) {
      System.err.println("RetrieveBusinessInfo: Datastore is null!");
      return null;
    }

    // Retrieving from datastore.
    Filter filter = new FilterPredicate("businessId", FilterOperator.EQUAL, businessId);
    Query query = new Query("Business").setFilter(filter);
    PreparedQuery pq = datastore.prepare(query);
    Entity entity = pq.asSingleEntity();
    
    // If there is no business account, simply return null.
    if (entity == null) return null;

    // Formatting entity into the business class. Checking if the types are valid.
    String businessDisplayName = getPropertyStringNotNull(entity, "businessDisplayName");
    String street = getPropertyStringNotNull(entity, "street");
    String city = getPropertyStringNotNull(entity, "city");
    String state = getPropertyStringNotNull(entity, "state");
    String zipCode = getPropertyStringNotNull(entity, "zipCode");
    Object _annotationObject = entity.getProperty("tempVisionAnnotation");
    Text annotationObject;
    Object _latLng = entity.getProperty("latLng");
    GeoPt latLng;
    if ((_annotationObject instanceof Text) && (_latLng instanceof GeoPt)) {
      annotationObject = (Text) _annotationObject;
      latLng = (GeoPt) _latLng;
    } else {
      System.err.println("Entity properties are of an incorrect type.");
      return null;
    }
    List<String> productIds = getPropertyStringList(entity, "productIds");

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
                        latLng,
                        productIds,
                        tempVisionAnnotation);
  }

  // Lists all businesses stored in the database.
  public static List<Business> listAllBusinesses(DatastoreService datastore) {
    if (datastore == null) {
      System.err.println("ListAllBusinesses: datastore is null!");
    }
    Query query = new Query("Business").addSort("businessDisplayName", SortDirection.ASCENDING);
    PreparedQuery pq = datastore.prepare(query);
    List<Business> results = new ArrayList<>();
    for (Entity entity : pq.asIterable()) {
      // Formatting entity into the business class. Checking if the types are valid.
      String businessId = getPropertyStringNotNull(entity, "businessId");
      String businessDisplayName = getPropertyStringNotNull(entity, "businessDisplayName");
      String street = getPropertyStringNotNull(entity, "street");
      String city = getPropertyStringNotNull(entity, "city");
      String state = getPropertyStringNotNull(entity, "state");
      String zipCode = getPropertyStringNotNull(entity, "zipCode");
      Object _annotationObject = entity.getProperty("tempVisionAnnotation");
      Text annotationObject;
      Object _latLng = entity.getProperty("latLng");
      GeoPt latLng;
      if ((_annotationObject instanceof Text) && (_latLng instanceof GeoPt)) {
        annotationObject = (Text) _annotationObject;
        latLng = (GeoPt) _latLng;
      } else {
        System.err.println("Entity properties are of an incorrect type.");
        return null;
      }
      List<String> productIds = getPropertyStringList(entity, "productIds");

      String tempVisionAnnotation = null;
      if (annotationObject != null) {
        tempVisionAnnotation = annotationObject.getValue();
      }

      results.add(new Business(businessId,
                               businessDisplayName,
                               street,
                               city,
                               state,
                               zipCode,
                               latLng,
                               productIds,
                               tempVisionAnnotation));
    }

    return results;
  }

  // For every label that a product has, we assign the product to that label in
  // the labels table in datastore.
  public static void addProductToLabels(DatastoreService datastore, String productId, List<String> labels) {
    if (datastore == null || productId == null || labels == null) {
      System.err.println("AddProductToLabels: At least one of the inputs was null!");
      return;
    }
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
        List<String> productIds = getPropertyStringList(entity, "productIds");
        productIds.add(productId);
        entity.setProperty("productIds", productIds);
        datastore.put(entity);
      }
    }
  }

  public static void deleteProductFromLabels(DatastoreService datastore, 
                                             String productId, 
                                             List<String> labels) {
    if (datastore == null || productId == null || labels == null) {
      System.err.println("DeleteProductFromLabels: At least one of the inputs was null!");
      return;
    }                                          
    for (String label : labels) {
      Filter filter = new FilterPredicate("label", FilterOperator.EQUAL, label.toLowerCase());
      Query query = new Query("ProductLabel").setFilter(filter);
      PreparedQuery pq = datastore.prepare(query);
      Entity entity = pq.asSingleEntity();
      // If the label exists, we remove the product from that label.
      if (entity != null) {
        List<String> productIds = getPropertyStringList(entity, "productIds");
        productIds.remove(productId);
        entity.setProperty("productIds", productIds);
        datastore.put(entity);
      }
    }
  }

  // Update product labels for an existing product.
  public static void updateProductLabels(DatastoreService datastore, 
                                         String productId,
                                         List<String> oldLabels,
                                         List<String> labels) {
    if (datastore == null || productId == null || labels == null || oldLabels == null) {
      System.err.println("UpdateProductLabels: At least one of the inputs was null!");
      return;
    }
    // Check what needs to be deleted and added.
    Set<String> oldLabelsSet = new HashSet<>(oldLabels);
    Set<String> labelsSet = new HashSet<>(labels);
    List<String> itemsToDelete = new ArrayList<>();
    List<String> itemsToAdd = new ArrayList<>();
    for (String oldLabel : oldLabels) {
      if (!labelsSet.contains(oldLabel)) itemsToDelete.add(oldLabel);
    }
    for (String label : labels) {
      if (!oldLabelsSet.contains(label)) itemsToAdd.add(label);
    }

    // Add new labels and delete old labels.
    addProductToLabels(datastore, productId, itemsToAdd);
    deleteProductFromLabels(datastore, productId, itemsToDelete);
  }

  // Add product to the specified product set.
  public static void addProductToProductSet(DatastoreService datastore, String productId, String productSetId) {
    if (datastore == null || productId == null || productSetId == null) {
      System.err.println("AddProductToProductSet: At least one of the inputs was null!");
      return;
    }
    Filter filter = new FilterPredicate("productSetId", FilterOperator.EQUAL, productSetId);
    Query query = new Query("ProductSet").setFilter(filter);
    PreparedQuery pq = datastore.prepare(query);
    Entity entity = pq.asSingleEntity();
    if (entity == null) {
      System.err.println("Product Set must be created first before adding a product!");
    } else {
      List<String> productIds = getPropertyStringList(entity, "productIds");
      productIds.add(productId);
      entity.setProperty("productIds", productIds);
      datastore.put(entity);
    }
  }

  // Delete product from the specifiec set.
  public static void deleteProductFromProductSet(DatastoreService datastore, String productId, String productSetId) {
    if (datastore == null || productId == null || productSetId == null) {
      System.err.println("DeleteProductFromProductSet: At least one of the inputs was null!");
      return;
    }
    Filter filter = new FilterPredicate("productSetId", FilterOperator.EQUAL, productSetId);
    Query query = new Query("ProductSet").setFilter(filter);
    PreparedQuery pq = datastore.prepare(query);
    Entity entity = pq.asSingleEntity();
    if (entity != null) {
      List<String> productIds = getPropertyStringList(entity, "productIds");
      productIds.remove(productId);
      entity.setProperty("productIds", productIds);
      datastore.put(entity);
    }
  }

  public static void updateProductSets(DatastoreService datastore, 
                                       String productId, 
                                       String oldProductSetId,
                                       String productSetId) {
    if (datastore == null || productId == null || productSetId == null || oldProductSetId == null) {
      System.err.println("UpdateProductSets: At least one of the inputs was null!");
      return;
    }
    if (oldProductSetId.equals(productSetId)) return;
    deleteProductFromProductSet(datastore, productId, oldProductSetId);
    addProductToProductSet(datastore, productId, productSetId);
  }

  // Add product to the specified product category.
  public static void addProductToProductCategory(DatastoreService datastore, String productId, String productCategory) {
    if (datastore == null || productId == null || productCategory == null) {
      System.err.println("AddProductToProductCategory: At least one of the inputs was null!");
      return;
    }
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
      List<String> productIds = getPropertyStringList(entity, "productIds");
      productIds.add(productId);
      entity.setProperty("productIds", productIds);
      datastore.put(entity);
    }
  }

  // Delete a product from the specified product category.
  public static void deleteProductFromProductCategory(DatastoreService datastore, 
                                                      String productId, 
                                                      String productCategory) {
    if (datastore == null || productId == null || productCategory == null) {
      System.err.println("DeleteProductFromProductCategory: At least one of the inputs was null!");
      return;
    }                                                  
    Filter filter = new FilterPredicate("productCategory", FilterOperator.EQUAL, productCategory);
    Query query = new Query("ProductCategory").setFilter(filter);
    PreparedQuery pq = datastore.prepare(query);
    Entity entity = pq.asSingleEntity();
    // If the category exists, remove the product.
    if (entity != null) {
      List<String> productIds = getPropertyStringList(entity, "productIds");
      productIds.remove(productId);
      entity.setProperty("productIds", productIds);
      datastore.put(entity);
    }
  }

  public static void updateProductCategories(DatastoreService datastore, 
                                       String productId, 
                                       String oldProductCategory,
                                       String productCategory) {
    if (datastore == null || productId == null || productCategory == null || 
        oldProductCategory == null) {
      System.err.println("UpdateProductCategories: At least one of the inputs was null!");
      return;
    }
    if (oldProductCategory.equals(productCategory)) return;
    deleteProductFromProductCategory(datastore, productId, oldProductCategory);
    addProductToProductCategory(datastore, productId, productCategory);
  }

  // Add product to the list of products offerec by the business.
  public static void addProductToBusiness(DatastoreService datastore, String productId, String businessId) {
    if (datastore == null || productId == null || businessId == null) {
      System.err.println("AddProductToBusiness: At least one of the inputs was null!");
      return;
    }
    Filter filter = new FilterPredicate("businessId", FilterOperator.EQUAL, businessId);
    Query query = new Query("Business").setFilter(filter);
    PreparedQuery pq = datastore.prepare(query);
    Entity entity = pq.asSingleEntity();
    if (entity == null) {
      System.err.println("Business must be created first before adding a product!");
    } else {
      List<String> productIds = getPropertyStringList(entity, "productIds");
      productIds.add(productId);
      entity.setProperty("productIds", productIds);
      datastore.put(entity);
    }
  }

  // Delete a given product from the business.
  public static void deleteProductFromBusiness(DatastoreService datastore, String productId, String businessId) {
    if (datastore == null || productId == null || businessId == null) {
      System.err.println("DeleteProductFromBusiness: At least one of the inputs was null!");
      return;
    }
    Filter filter = new FilterPredicate("businessId", FilterOperator.EQUAL, businessId);
    Query query = new Query("Business").setFilter(filter);
    PreparedQuery pq = datastore.prepare(query);
    Entity entity = pq.asSingleEntity();
    if (entity != null) {
      List<String> productIds = getPropertyStringList(entity, "productIds");
      productIds.remove(productId);
      entity.setProperty("productIds", productIds);
      datastore.put(entity);
    }
  }

  // Retrieve a list of all the products offered by the business.
  // TODO: integrate label searching with the textQuery.
  public static List<ProductEntity> findProducts(DatastoreService datastore, 
                                                 String businessId,
                                                 String productSetId,
                                                 String productCategory,
                                                 String sortOrder) {
    if (datastore == null) {
      System.err.println("FindProducts: Datastore was null!");
      return null;
    }
    // Set the filters.
    List<Filter> filters = new ArrayList<>();
    if (businessId != null) {
      filters.add(new FilterPredicate("businessId", FilterOperator.EQUAL, businessId));
    }
    if (productSetId != null) {
      filters.add(new FilterPredicate("productSetId", FilterOperator.EQUAL, productSetId));
    }
    if (productCategory != null) {
      filters.add(new FilterPredicate("productCategory", FilterOperator.EQUAL, productCategory));
    }
    CompositeFilter allFilters = null;
    Filter singleFilter = null;
    if (!filters.isEmpty()) {
      if (filters.size() == 1) {
        singleFilter = filters.get(0);
      } else {
        allFilters = CompositeFilterOperator.and(filters);
      }
    }

    // Set the sort direction.
    String sortCategory = null;
    SortDirection sortDirection = null;
    if (sortOrder.equals("name_descending")) {
      sortCategory = "productDisplayName";
      sortDirection = SortDirection.DESCENDING;
    } else if (sortOrder.equals("name_ascending")) {
      sortCategory = "productDisplayName";
      sortDirection = SortDirection.ASCENDING;
    } else if (sortOrder.equals("price_descending")) {
      sortCategory = "price";
      sortDirection = SortDirection.DESCENDING;
    } else if (sortOrder.equals("price_ascending")) {
      sortCategory = "price";
      sortDirection = SortDirection.ASCENDING;
    }

    // Create the query.
    Query query = new Query("Product");
    if (allFilters != null) {
      query.setFilter(allFilters);
    } else if (singleFilter != null) {
      query.setFilter(singleFilter);
    }
    if (sortCategory != null) {
      query.addSort(sortCategory, sortDirection);
    }
    
    // Run the query and return the results after formatting.
    PreparedQuery pq = datastore.prepare(query);
    List<ProductEntity> products = new ArrayList<>();
    for (Entity entity : pq.asIterable()) {

      // Extract and verify types of the different properties of a product.
      String productId = getPropertyStringNotNull(entity, "productId");
      String productDisplayName = getPropertyStringNotNull(entity, "productDisplayName");
      productSetId = getPropertyStringNotNull(entity, "productSetId");
      productCategory = getPropertyStringNotNull(entity, "productCategory");
      businessId = getPropertyStringNotNull(entity, "businessId");
      String productDescription = getPropertyString(entity, "productDescription");
      
      Object _price = entity.getProperty("price");
      Object _cloudVisionAnnotation = entity.getProperty("cloudVisionAnnotation");
      float price; 
      String cloudVisionAnnotation;
      // Verifying types of the values in a product entity.
      if ((_price instanceof Double) &&
          (_cloudVisionAnnotation instanceof Text)) {
        Double doublePrice = (Double) _price;
        price = doublePrice.floatValue();
        Text textVisionAnnotation = (Text) _cloudVisionAnnotation;
        if (textVisionAnnotation == null) {
          cloudVisionAnnotation = null;
        } else {
          cloudVisionAnnotation = textVisionAnnotation.getValue();
        }
      } else {
        System.err.println("Entity properties are of an incorrect type.");
        return null;
      }

      List<String> gcsUrls = getPropertyStringList(entity, "gcsUrls");
      List<String> imageUrls = getPropertyStringList(entity, "imageUrls");
      List<String> labels = getPropertyStringList(entity, "labels");

      // Add the product to the products list.
      products.add(new ProductEntity(productId,
                                     productDisplayName,
                                     productSetId,
                                     productCategory,
                                     businessId,
                                     price,
                                     gcsUrls,
                                     imageUrls,
                                     labels,
                                     productDescription,
                                     cloudVisionAnnotation));
    }
    return products;
  }

  // Retrieves product information based on the product id. 
  public static ProductEntity retrieveProductInfo(DatastoreService datastore, String productId) {
    if (datastore == null || productId == null) {
      System.err.println("RetrieveProductInfo: At least one of the inputs was null!");
      return null;
    }
    // Retrieving from datastore.
    Filter filter = new FilterPredicate("productId", FilterOperator.EQUAL, productId);
    Query query = new Query("Product").setFilter(filter);
    PreparedQuery pq = datastore.prepare(query);
    Entity entity = pq.asSingleEntity();

    // Return null if the product doesn't exist in the database.
    if (entity == null) return null;

    // Extracting properties of the product entity.
    String businessId = getPropertyStringNotNull(entity, "businessId");
    String productDisplayName = getPropertyStringNotNull(entity, "productDisplayName");
    String productSetId = getPropertyStringNotNull(entity, "productSetId");
    String productCategory = getPropertyStringNotNull(entity, "productCategory");
    String productDescription = getPropertyString(entity, "productDescription");

    Object _price = entity.getProperty("price");
    Object _cloudVisionAnnotation = entity.getProperty("cloudVisionAnnotation");
    String cloudVisionAnnotation;
    float price; 

    // Verifying the types of the properties are valid. 
    if ((_price instanceof Double) &&
        (_cloudVisionAnnotation instanceof Text)) {
      Double doublePrice = (Double) _price;
      price = doublePrice.floatValue();
      Text textVisionAnnotation = (Text) _cloudVisionAnnotation;
      if (textVisionAnnotation == null) {
        cloudVisionAnnotation = null;
      } else {
        cloudVisionAnnotation = textVisionAnnotation.getValue();
      }
    } else {
      System.err.println("Entity properties are of an incorrect type.");
      return null;
    }
    List<String> gcsUrls = getPropertyStringList(entity, "gcsUrls");
    List<String> imageUrls = getPropertyStringList(entity, "imageUrls");
    List<String> labels = getPropertyStringList(entity, "labels");

    return new ProductEntity(productId,
                             productDisplayName,
                             productSetId,
                             productCategory,
                             businessId,
                             price,
                             gcsUrls,
                             imageUrls,
                             labels,
                             productDescription,
                             cloudVisionAnnotation);
  }

  private static String getPropertyString(Entity entity, String propertyName) throws IllegalArgumentException {
    Object property = entity.getProperty(propertyName);
    if (property == null) {
      return null;
    } else if (property instanceof String) {
      return property.toString();
    } else {
      throw new IllegalArgumentException(propertyName + " property is of an incorrect type.");
    }
  }

  private static String getPropertyStringNotNull(Entity entity, 
                                                 String propertyName) 
                                                 throws IllegalArgumentException {
    Object property = entity.getProperty(propertyName);
    if (property == null) {
      throw new IllegalArgumentException(propertyName + " property is null.");
    } else if (property instanceof String) {
      return property.toString();
    } else {
      throw new IllegalArgumentException(propertyName + " property is of an incorrect type.");
    }
  }

  private static List<String> getPropertyStringList(Entity entity, String propertyName) {
    @SuppressWarnings("unchecked") // Documentation says to suppress warning this way
    List<String> result = (ArrayList<String>) entity.getProperty(propertyName); 

    if (result == null) result = new ArrayList<String>();
    return result;
  }

  public static SearchInfo retrieveSearchInfo(DatastoreService datastore, String searchId) {
    if (datastore == null || searchId == null) {
      System.err.println("RetrieveSearchInfo: At least one of the inputs was null!");
      return null;
    }
    Filter filter = new FilterPredicate("searchId", FilterOperator.EQUAL, searchId);
    Query query = new Query("SearchInfo").setFilter(filter);
    PreparedQuery pq = datastore.prepare(query);
    Entity entity = pq.asSingleEntity();

    // Return null if the object doesn't exist in the database.
    if (entity == null) return null;

    String textSearch = getPropertyString(entity, "textSearch");
    String gcsUrl = getPropertyString(entity, "gcsUrl");
    String imageUrl = getPropertyString(entity, "imageUrl");
    String userId = getPropertyString(entity, "userId");
    String productCategory = getPropertyString(entity, "productCategory");
    
    return new SearchInfo(searchId, textSearch, gcsUrl, imageUrl, userId, 
                          productCategory);
  }

  public static ProductLabel retrieveProductLabelInfo(DatastoreService datastore, String label) {
    Filter filter = new FilterPredicate("label", FilterOperator.EQUAL, label.toLowerCase());
    Query query = new Query("ProductLabel").setFilter(filter);
    PreparedQuery pq = datastore.prepare(query);
    Entity entity = pq.asSingleEntity();

    if (entity == null) return null;

    String productLabel = getPropertyStringNotNull(entity, "label");
    List<String> productIds = getPropertyStringList(entity, "productIds");

    return new ProductLabel(productLabel, productIds);

  }

  // SearchId corresponds to the id of the search object, which contains information 
  // about the content of the search (Text search, image search, product category, etc.)
  public static void addSearchInfoToSearchHistory(DatastoreService datastore, String userId, String searchId) {
    Filter filter = new FilterPredicate("userId", FilterOperator.EQUAL, userId);
    Query query = new Query("Account").setFilter(filter);
    PreparedQuery pq = datastore.prepare(query);
    Entity entity = pq.asSingleEntity();

    if (entity != null) {
      List<String> searchHistory = getPropertyStringList(entity, "searchHistory");
      searchHistory.add(searchId);
      entity.setProperty("searchHistory", searchHistory);
      datastore.put(entity);
    }
  }

  // Retrieves most recent search history as a list.
  public static List<SearchInfo> retrieveRecentSearchHistory(DatastoreService datastore, 
                                                             String userId, 
                                                             int numberToDisplay) {
    Filter filter = new FilterPredicate("userId", FilterOperator.EQUAL, userId);
    Query query = new Query("SearchInfo")
                        .setFilter(filter)
                        .addSort("timestamp", SortDirection.DESCENDING);
    PreparedQuery pq = datastore.prepare(query);
    List<SearchInfo> results = new ArrayList<>();
    int count = 1;
    for (Entity entity : pq.asIterable()) {
      if (count > numberToDisplay) break;
      count++;

      String searchId = getPropertyStringNotNull(entity, "searchId");
      String textSearch = getPropertyString(entity, "textSearch");
      String gcsUrl = getPropertyString(entity, "gcsUrl");
      String imageUrl = getPropertyString(entity, "imageUrl");
      String productCategory = getPropertyString(entity, "productCategory");
      
      results.add(new SearchInfo(searchId, textSearch, gcsUrl, imageUrl, userId, 
                                 productCategory));
    }
    return results;
  }

  // Append the address of the product and return a new list with this 
  // extra information.
  public static List<ProductWithAddress> convertToProductWithAddress(
    DatastoreService datastore, List<ProductEntity> products) {
    List<ProductWithAddress> results = new ArrayList<>();
    for (ProductEntity product : products) {
      Filter filter = new FilterPredicate("businessId", FilterOperator.EQUAL, product.getBusinessId());
      Query query = new Query("Business").setFilter(filter);
      PreparedQuery pq = datastore.prepare(query);
      Entity entity = pq.asSingleEntity();

      if (entity == null) {
        System.err.println("convertToProductWithAddress: Business is null!");
        return null;
      }

      String street = getPropertyStringNotNull(entity, "street");
      String city = getPropertyStringNotNull(entity, "city");
      String state = getPropertyStringNotNull(entity, "state");
      String zipCode = getPropertyStringNotNull(entity, "zipCode");
      Object _latLng = entity.getProperty("latLng");
      GeoPt latLng;
      if (_latLng instanceof GeoPt) {
        latLng = (GeoPt) _latLng;
      } else {
        System.err.println("Entity properties are of an incorrect type.");
        return null;
      }

      results.add(new ProductWithAddress(product, 
                  new Address(street, city, state, zipCode, latLng)));

    }

    return results;
  }

  // Updates the temp annotation object that is stored in the business account.
  public static void updateTempAnnotation(DatastoreService datastore, 
                                          String businessId, Text annotation) {
    // Retrieving from datastore.
    Filter filter = new FilterPredicate("businessId", FilterOperator.EQUAL, businessId);
    Query query = new Query("Business").setFilter(filter);
    PreparedQuery pq = datastore.prepare(query);
    Entity entity = pq.asSingleEntity();
    
    // Update the tempVisionAnnotation property.
    if (entity != null) {
      entity.setProperty("tempVisionAnnotation", annotation);
      datastore.put(entity);
    }
  }
}