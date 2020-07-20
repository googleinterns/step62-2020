package com.google.sps.servlets;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Date;
import java.awt.Color;
import com.google.sps.data.*;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.FetchOptions;

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
  protected UserService userService;

  public BrowseServlet() {
    super();
    gson = new Gson();
    datastore = DatastoreServiceFactory.getDatastoreService();
    userService = UserServiceFactory.getUserService();
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
    // TODO: Check for uploaded files. As part of the querystring, we should 
    // get the blobKey.
    String queryString = "/browse.html?";

    // TODO: check the textString
    if (!textSearch.isEmpty()) {
      queryString = queryString + "textSearch=" + textSearch;
    }

    response.sendRedirect(queryString);
  }
}