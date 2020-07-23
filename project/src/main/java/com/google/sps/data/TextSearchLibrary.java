package com.google.sps.data;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.LinkedHashSet;
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

  enum Match {
    EXACT, CLOSE, PARTIAL, NONE;
  }

  public static List<String> getValidProductIds(String keyword, List<ProductLabel> productLabels) {
    Set<String> ids = new LinkedHashSet<>();
    List<String> exactMatch = new ArrayList<>();
    List<String> closeMatch = new ArrayList<>();
    List<String> partialMatch = new ArrayList<>();

    for(ProductLabel productLabel : productLabels){
      switch (compareLabels(keyword, productLabel.getLabel())) {
        case EXACT:
          exactMatch.addAll(productLabel.getProductIds());
          break;
        case CLOSE:
          closeMatch.addAll(productLabel.getProductIds());
          break;
        case PARTIAL:
          partialMatch.addAll(productLabel.getProductIds());
          break;
        default:
          break;
      }
    }

    ids.addAll(exactMatch);
    ids.addAll(closeMatch);
    ids.addAll(partialMatch);
    List<String> result = new ArrayList<>();
    result.addAll(ids);
    return result;
  }
 
  public static Match compareLabels(String keyword, String labelName){
    keyword = keyword.toLowerCase();
    labelName = labelName.toLowerCase();

    if (keyword.equals(labelName)) {
        return Match.EXACT;
    }

    if (labelName.contains(keyword)) {
        return Match.PARTIAL;
    }

    if (keyword.contains(" ")) {
      String[] keywords = keyword.split(" ");

      for(int i = 0; i < keywords.length; i++){
        if(keywords[i].equals(labelName)){
          return Match.CLOSE;
        }
        if(labelName.contains(keywords[i])){
          return Match.PARTIAL;
        }
      }
    }
    return Match.NONE;
  }
}