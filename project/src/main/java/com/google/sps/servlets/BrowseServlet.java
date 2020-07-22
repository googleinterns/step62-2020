package com.google.sps.servlets;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Map;
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
    // TODO: text based search and product search.

    // Retrieve parameters from the request
    String productSetDisplayName = request.getParameter("productSetDisplayName");
    String productCategory = request.getParameter("productCategory");
    String businessId = request.getParameter("businessId");
    String sortOrder = request.getParameter("sortOrder");

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
                                  sortOrder, 
                                  null); // textQuery
    String json = gson.toJson(products);

    // Send the response.
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
      // TODO: add it to user search history in database
    } else {
      searchInfo.setProperty("userId", null);
    }
    
    // Checks if the user sent a text search or a image search or both. Adds
    // query properties appropriately.
    searchInfo.setProperty("gcsUrl", null);
    searchInfo.setProperty("imageUrl", null);
    searchInfo.setProperty("textSearch", null);
    if (userUploadedImage) {
      //Map<String, List<FileInfo>> files = blobstore.getFileInfos(request);
      // String gcsUrl = CloudStorageLibrary.getGcsFilePath(files);
      String gcsUrl = CloudStorageLibrary.getGcsFilePath(request, blobstore);
      BlobKey blobKey = blobstore.createGsBlobKey(gcsUrl);
      String imageUrl = "/serveBlobstoreImage?blobKey=" + blobKey.getKeyString();
      searchInfo.setProperty("gcsUrl", gcsUrl);
      searchInfo.setProperty("imageUrl", imageUrl);
    } 
    if (!textSearch.isEmpty()) {
      searchInfo.setProperty("textSearch", textSearch);
    }
    datastore.put(searchInfo);
  
    response.sendRedirect("/browse.html?searchId="+searchId);
  }
}