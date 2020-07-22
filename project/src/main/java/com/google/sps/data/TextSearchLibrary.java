package com.google.sps.data;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

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


public class TextSearchLibrary {
  public static List<ProductEntity> textSearch(DatastoreService datastore, List<ProductEntity> products, String keyword) {
    List<ProductLabel> productLabels = getLabels(datastore, products);
    List<String> productIds = getValidProductIds(keyword, productLabels);
    List<ProductEntity> result = new ArrayList<>();
    for (String productId : productIds) {
      ProductEntity product = ServletLibrary.retrieveProductInfo(datastore, productId);
      if (product != null) result.add(product);
    }
    return result;
  }
  
  public static List<ProductLabel> getLabels(DatastoreService datastore,
                                             List<ProductEntity> products) {
    Set<String> labels = new HashSet<>();
    for (ProductEntity product : products) {
      labels.addAll(product.getLabels());
    }
    List<ProductLabel> result = new ArrayList<>();
    for (String label : labels) {
      ProductLabel productLabel = ServletLibrary.retrieveProductLabelInfo(datastore, label);
      if (productLabel != null) result.add(productLabel);
    }
    return result;
  }

  public static List<String> getValidProductIds(String keyword, List<ProductLabel> productLabels) {
    List<String> ids = new ArrayList<>();
    List<String> exactMatch = new ArrayList<>();
    List<String> closeMatch = new ArrayList<>();
    List<String> partialMatch = new ArrayList<>();

    for(ProductLabel productLabel : productLabels){
      if(compareLabels(keyword, productLabel.getLabel()).equals("Exact match")){
        exactMatch.addAll(productLabel.getProductIds());
      } else if(compareLabels(keyword, productLabel.getLabel()).equals("Close match")){
        closeMatch.addAll(productLabel.getProductIds());
      } else if(compareLabels(keyword, productLabel.getLabel()).equals("Partial match")){
        partialMatch.addAll(productLabel.getProductIds());
      }
    }

    ids.addAll(exactMatch);
    ids.addAll(closeMatch);
    ids.addAll(partialMatch);

    // Need to remove all duplicates but preserve order.
    Set<String> seen = new HashSet<>();
    List<String> result = new ArrayList<>();
    for (String id : ids) {
      if (seen.contains(id)) continue;
      seen.add(id);
      result.add(id);
    }
    return result;
  }
 
  public static String compareLabels(String keyword, String labelName){
    keyword = keyword.toLowerCase();
    labelName = labelName.toLowerCase();

    if (keyword.equals(labelName)) {
        return "Exact match";
    }

    if (labelName.contains(keyword)) {
        return "Partial match";
    }

    if (keyword.contains(" ")) {
      String[] keywords = keyword.split(" ");

      for(int i = 0; i < keywords.length; i++){
        if(keywords[i].equals(labelName)){
          return "Close match";
        }
        if(labelName.contains(keywords[i])){
          return "Partial match";
        }
      }
    }
    return "No match";
  }
}