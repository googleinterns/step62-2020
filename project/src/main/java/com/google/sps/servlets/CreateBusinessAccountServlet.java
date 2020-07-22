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
import com.google.appengine.api.datastore.Text;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import com.google.sps.data.*;
import java.util.ArrayList;
import java.util.List;

import java.util.stream.Collectors;  
import java.io.ByteArrayOutputStream;

// The purpose of this servlet is to collect data from the business account creation 
// form and add the information to the database. It is also used to update existing
// information in the database.
@WebServlet("/createBusinessAccount")
public class CreateBusinessAccountServlet extends HttpServlet {

  protected Gson gson;
  protected DatastoreService datastore;
  protected UserService userService;

  public CreateBusinessAccountServlet() {
    super();
    gson = new Gson();
    datastore = DatastoreServiceFactory.getDatastoreService();
    userService = UserServiceFactory.getUserService();
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Return all product set display names from the database.
    List<Business> businesses = ServletLibrary.listAllBusinesses(datastore);
    List<BusinessName> names = 
      businesses.stream()
                .map(elem -> new BusinessName(elem.getBusinessId(), elem.getBusinessDisplayName()))
                .collect(Collectors.toList());

    String json = gson.toJson(names);

    // Send the JSON as the response.
    response.setContentType("application/json;");
    response.getWriter().println(json);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    // Get required userId.
    String userId = userService.getCurrentUser().getUserId();
    
    // check if account already exists, so we don't overwrite search history or
    // product ids. Null value indicates that account doesn't exist.
    Account account = ServletLibrary.retrieveAccountInfo(datastore, userService, userId);
    Business business = ServletLibrary.retrieveBusinessInfo(datastore, userId);

    // Create account entity object that will be stored.
    Entity newAccount = new Entity("Account", userId);
    newAccount.setProperty("userId", userId);
    newAccount.setProperty("userEmail", userService.getCurrentUser().getEmail());
    newAccount.setProperty("nickname", request.getParameter("businessName"));
    newAccount.setProperty("street", request.getParameter("street"));
    newAccount.setProperty("city", request.getParameter("city"));
    newAccount.setProperty("state", request.getParameter("state"));
    newAccount.setProperty("zipCode", request.getParameter("zipCode"));
    if (account == null) {
      newAccount.setProperty("searchHistory", new ArrayList<String>());
    } else {
      newAccount.setProperty("searchHistory", account.getSearchHistory());
    }
    newAccount.setProperty("isUserBusinessOwner", true);

    // Set up business information to be stored.
    Entity newBusiness = new Entity("Business", userId);
    newBusiness.setProperty("businessId", userId); // Using user id as business id.
    newBusiness.setProperty("businessDisplayName", request.getParameter("businessName"));
    newBusiness.setProperty("street", request.getParameter("street"));
    newBusiness.setProperty("city", request.getParameter("city"));
    newBusiness.setProperty("state", request.getParameter("state"));
    newBusiness.setProperty("zipCode", request.getParameter("zipCode"));
    if (business == null) {
      newBusiness.setProperty("productIds", new ArrayList<String>());
    } else {
      newBusiness.setProperty("productIds", business.getProductIds());
    }
    newBusiness.setProperty("tempVisionAnnotation", new Text(""));

    // Store in datastore
    datastore.put(newAccount);
    datastore.put(newBusiness);

    // Redirect to account page
    response.sendRedirect("/login");
  }

}