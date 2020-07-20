package com.google.sps.servlets;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.Filter;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.blobstore.FileInfo;

import com.google.sps.data.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@WebServlet("/viewProducts")
public class ViewProductsServlet extends HttpServlet {

  protected DatastoreService datastore;
  protected BlobstoreService blobstore;
  protected Gson gson;
  protected UserService userService;

  public ViewProductsServlet() {
    super();
    datastore = DatastoreServiceFactory.getDatastoreService();
    userService = UserServiceFactory.getUserService();
    blobstore = BlobstoreServiceFactory.getBlobstoreService();
    gson = new Gson();
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // TODO: text based search.

    // Retrieve parameters from the request
    String productSetDisplayName = request.getParameter("productSetDisplayName");
    String productCategory = request.getParameter("productCategory");
    String businessId = request.getParameter("businessId");
    String sortOrder = request.getParameter("sortOrder");

    // Set parameters to apprpriate defaults, if necessary.
    if (businessId.equals("getFromDatabase")) {
      businessId = userService.getCurrentUser().getUserId();
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

    String queryString = "/viewProducts.html?";

    // Checks if the user sent a text search or a image search or both. Adds
    // query properties appropriately.
    if (userUploadedImage) {
      Map<String, List<FileInfo>> files = blobstore.getFileInfos(request);
      String gcsUrl = CloudStorageLibrary.getGcsFilePath(files);
      BlobKey blobKey = blobstore.createGsBlobKey(gcsUrl);
      queryString = queryString + "blobKey=" + blobKey.getKeyString();
      if (!textSearch.isEmpty()) {
        queryString = queryString + "&textSearch=" + textSearch;
      }
    } else if (!textSearch.isEmpty()) {
      queryString = queryString + "textSearch=" + textSearch;
    }
  
    response.sendRedirect(queryString);
  }
}