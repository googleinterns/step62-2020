package com.google.sps.data;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
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
    if (productIds == null) productIds = new ArrayList<String>();
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
    if (searchHistory == null) searchHistory = new ArrayList<String>();

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
    if (datastore == null) {
      System.err.println("ListAllProductSets: Datastore is null!");
      return null;
    }
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
      if (productIds == null) productIds = new ArrayList<String>();

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
    if (productIds == null) productIds = new ArrayList<String>();

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
        @SuppressWarnings("unchecked") // Documentation says to suppress warning this way
          List<String> productIds = (ArrayList<String>) entity.getProperty("productIds"); 
        if (productIds == null) productIds = new ArrayList<String>();
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
        @SuppressWarnings("unchecked") // Documentation says to suppress warning this way
          List<String> productIds = (ArrayList<String>) entity.getProperty("productIds"); 
        if (productIds == null) productIds = new ArrayList<String>();
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
      @SuppressWarnings("unchecked") // Documentation says to suppress warning this way
        List<String> productIds = (ArrayList<String>) entity.getProperty("productIds"); 
      if (productIds == null) productIds = new ArrayList<String>();
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
      @SuppressWarnings("unchecked") // Documentation says to suppress warning this way
        List<String> productIds = (ArrayList<String>) entity.getProperty("productIds"); 
      if (productIds == null) productIds = new ArrayList<String>();
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
      @SuppressWarnings("unchecked") // Documentation says to suppress warning this way
        List<String> productIds = (ArrayList<String>) entity.getProperty("productIds"); 
      if (productIds == null) productIds = new ArrayList<String>();
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
      @SuppressWarnings("unchecked") // Documentation says to suppress warning this way
        List<String> productIds = (ArrayList<String>) entity.getProperty("productIds"); 
      if (productIds == null) productIds = new ArrayList<String>();
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
      @SuppressWarnings("unchecked") // Documentation says to suppress warning this way
        List<String> productIds = (ArrayList<String>) entity.getProperty("productIds"); 
      if (productIds == null) productIds = new ArrayList<String>();
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
      @SuppressWarnings("unchecked") // Documentation says to suppress warning this way
        List<String> productIds = (ArrayList<String>) entity.getProperty("productIds"); 
      if (productIds == null) productIds = new ArrayList<String>();
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
                                                 String sortOrder,
                                                 String textQuery) {
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
    if (sortOrder.equals("alphabetical_descending")) {
      sortCategory = "productDisplayName";
      sortDirection = SortDirection.DESCENDING;
    } else if (sortOrder.equals("alphabetical_ascending")) {
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
      Object _productId = entity.getProperty("productId");
      Object _productDisplayName = entity.getProperty("productDisplayName");
      Object _productSetId = entity.getProperty("productSetId");
      Object _productCategory = entity.getProperty("productCategory");
      Object _businessId = entity.getProperty("businessId");
      Object _price = entity.getProperty("price");
      Object _productDescription = entity.getProperty("productDescription");
      Object _cloudVisionAnnotation = entity.getProperty("cloudVisionAnnotation");
      String productId;
      String productDisplayName;
      float price; 
      String productDescription;
      String cloudVisionAnnotation;

      // Verifying types of the values in a product entity.
      if ((_productId instanceof String) &&
          (_productDisplayName instanceof String) &&
          (_productSetId instanceof String) &&
          (_productCategory instanceof String) &&
          (_businessId instanceof String) &&
          (_price instanceof Double) &&
          (_productDescription instanceof String) &&
          (_cloudVisionAnnotation instanceof Text)) {
        productId = _productId.toString();
        productDisplayName = _productDisplayName.toString();
        productSetId = _productSetId.toString();
        productCategory = _productCategory.toString();
        businessId = _businessId.toString();
        Double doublePrice = (Double) _price;
        price = doublePrice.floatValue();
        productDescription = _productDescription.toString();
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
      @SuppressWarnings("unchecked") // Documentation says to suppress warning this way
        List<String> gcsUrls = (ArrayList<String>) entity.getProperty("gcsUrls"); 
      if (gcsUrls == null) gcsUrls = new ArrayList<String>();
      @SuppressWarnings("unchecked") // Documentation says to suppress warning this way
        List<String> imageUrls = (ArrayList<String>) entity.getProperty("imageUrls"); 
      if (imageUrls == null) imageUrls = new ArrayList<String>();
      @SuppressWarnings("unchecked") // Documentation says to suppress warning this way
        List<String> labels = (ArrayList<String>) entity.getProperty("labels"); 
      if (labels == null) labels = new ArrayList<>();

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
    Object _productDisplayName = entity.getProperty("productDisplayName");
    Object _productSetId = entity.getProperty("productSetId");
    Object _productCategory = entity.getProperty("productCategory");
    Object _businessId = entity.getProperty("businessId");
    Object _price = entity.getProperty("price");
    Object _productDescription = entity.getProperty("productDescription");
    Object _cloudVisionAnnotation = entity.getProperty("cloudVisionAnnotation");
    String businessId;
    String productDisplayName;
    String productSetId;
    String productCategory;
    float price; 
    String productDescription;
    String cloudVisionAnnotation;

    // Verifying the types of the properties are valid. 
    if ((_businessId instanceof String) &&
        (_productDisplayName instanceof String) &&
        (_productSetId instanceof String) &&
        (_productCategory instanceof String) &&
        (_price instanceof Double) &&
        (_productDescription instanceof String) &&
        (_cloudVisionAnnotation instanceof Text)) {
      businessId = _businessId.toString();
      productDisplayName = _productDisplayName.toString();
      productSetId = _productSetId.toString();
      productCategory = _productCategory.toString();
      Double doublePrice = (Double) _price;
      price = doublePrice.floatValue();
      productDescription = _productDescription.toString();
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
    @SuppressWarnings("unchecked") // Documentation says to suppress warning this way
        List<String> gcsUrls = (ArrayList<String>) entity.getProperty("gcsUrls"); 
      if (gcsUrls == null) gcsUrls = new ArrayList<String>();
    @SuppressWarnings("unchecked") // Documentation says to suppress warning this way
      List<String> imageUrls = (ArrayList<String>) entity.getProperty("imageUrls"); 
    if (imageUrls == null) imageUrls = new ArrayList<String>();
    @SuppressWarnings("unchecked") // Documentation says to suppress warning this way
      List<String> labels = (ArrayList<String>) entity.getProperty("labels"); 
    if (labels == null) labels = new ArrayList<>();

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
}