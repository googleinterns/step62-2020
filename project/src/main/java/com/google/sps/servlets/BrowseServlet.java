package com.google.sps.servlets;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import com.google.sps.data.*;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.FetchOptions;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.blobstore.FileInfo;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;



@WebServlet("/browse")
public class BrowseServlet extends HttpServlet {

  protected Gson gson;
  protected DatastoreService datastore;
  protected BlobstoreService blobstore;
  protected UserService userService;

  public BrowseServlet() {
    super();
    gson = new Gson();
    datastore = DatastoreServiceFactory.getDatastoreService();
    blobstore = BlobstoreServiceFactory.getBlobstoreService();
    userService = UserServiceFactory.getUserService();
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // TODO: product search.

    // Retrieve parameters from the request
    String productSetDisplayName = request.getParameter("productSetDisplayName");
    String productCategory = request.getParameter("productCategory");
    String businessId = request.getParameter("businessId");
    String sortOrder = request.getParameter("sortOrder");
    String searchId = request.getParameter("searchId");

    // Set parameters to apprpriate defaults, if necessary.
    if (businessId.equals("none")) {
      businessId = null;
    }
    if (productCategory.equals("none")) {
      productCategory = null;
    }
    String productSetId = null;
    ProductSetEntity productSet = null;
    if (!productSetDisplayName.equals("none")) {
      // true indicates we are searching with the displayname instead of the id.
      productSet = ServletLibrary.retrieveProductSetInfo(datastore, productSetDisplayName, true);
    }
    if (productSet != null) {
      productSetId = productSet.getProductSetId();
    }

    // Search database based on the filters. 
    List<ProductEntity> products = 
      ServletLibrary.findProducts(datastore, 
                                  businessId,
                                  productSetId, 
                                  productCategory, 
                                  sortOrder);

    if (searchId != null) {
      SearchInfo searchInfo = ServletLibrary.retrieveSearchInfo(datastore, searchId);
      // TODO: integrate once Phillips finishes product search.
      // if (searchInfo.getGcsUrl() != null) {
      //   List <String> productSearchIds = ProductSearchLibrary.productSearch(searchInfo.getGcsUrl(), searchInfo.productCategory);
      //   List<ProductEntity> imageSearchProducts = new ArrayList<>();
      //   productSearchIds.forEach(productId->imageSearchProducts.add(ServletLibrary.retrieveProductInfo(datastore, productId));
      //   Set<ProductEntity> setProducts = new HashSet<>(products);
      //   List<ProductEntity> newProducts = new ArrayList<>();
      //   for (ProductEntity product : imageSearchProducts) {
      //     if (setProducts.contains(product)) newProducts.add(product);
      //   }
      //   products = newProducts;
      // }

      // Text query if it is specified, will take in this list and output a new
      // list that satisfies the query.
      if (searchInfo.getTextSearch() != null) {
        products = TextSearchLibrary.textSearch(datastore, products, 
                                                searchInfo.getTextSearch());
      }
    }
    
    List<ProductWithAddress> productsWithAddress = 
      ServletLibrary.convertToProductWithAddress(datastore, products);

    // TODO: call sorting mechanism here for location.
    // Send the response.
    String json = gson.toJson(products);
    response.setContentType("application/json;");
    response.getWriter().println(json);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String textSearch = request.getParameter("textSearch");
    boolean userUploadedImage = Boolean.parseBoolean(request.getParameter("userUploadedImage"));

    // Creates a new SearchInfo object, which will be stored in datastore.
    String searchId = ServletLibrary.generateUUID();
    Entity searchInfo = new Entity("SearchInfo", searchId);
    searchInfo.setProperty("searchId", searchId);
    searchInfo.setProperty("timestamp", System.currentTimeMillis());
    if (userService.isUserLoggedIn()) {
      searchInfo.setProperty("userId", userService.getCurrentUser().getUserId());
      ServletLibrary.addSearchInfoToSearchHistory(datastore, 
        userService.getCurrentUser().getUserId(), searchId);
    } else {
      searchInfo.setProperty("userId", null);
    }
    
    // Checks if the user sent a text search or a image search or both. Adds
    // query properties appropriately.
    searchInfo.setProperty("gcsUrl", null);
    searchInfo.setProperty("imageUrl", null);
    searchInfo.setProperty("textSearch", null);
    searchInfo.setProperty("productCategory", null);    
    if (userUploadedImage) {
      String gcsUrl = CloudStorageLibrary.getGcsFilePath(request, blobstore);
      BlobKey blobKey = blobstore.createGsBlobKey(gcsUrl);
      String imageUrl = "/serveBlobstoreImage?blobKey=" + blobKey.getKeyString();
      searchInfo.setProperty("gcsUrl", gcsUrl);
      searchInfo.setProperty("imageUrl", imageUrl);
      searchInfo.setProperty("productCategory", 
        request.getParameter("productCategorySearch"));
    } 
    if (!textSearch.isEmpty()) {
      searchInfo.setProperty("textSearch", textSearch);
    }
    datastore.put(searchInfo);
  
    response.sendRedirect("/browse.html?searchId="+searchId);
  }
}