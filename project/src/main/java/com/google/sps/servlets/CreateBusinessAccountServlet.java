package com.google.sps.servlets;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import com.google.sps.data.ServletLibrary;
import java.util.ArrayList;
import java.util.List;

// The purpose of this servlet is to collect data from the business account creation 
// form and add the information to the database. It is also used to update existing
// information in the database.
@WebServlet("/createBusinessAccount")
public class CreateBusinessAccountServlet extends HttpServlet {

  protected DatastoreService datastore;
  protected UserService userService;

  public CreateBusinessAccountServlet() {
    super();
    datastore = DatastoreServiceFactory.getDatastoreService();
    userService = UserServiceFactory.getUserService();
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    // Get required user information.
    String userId = userService.getCurrentUser().getUserId();
    String userEmail = userService.getCurrentUser().getEmail();
    String businessName = request.getParameter("businessName"); 
    String street = request.getParameter("street");
    String city = request.getParameter("city");
    String state = request.getParameter("state");
    String zipCode = request.getParameter("zipCode");
    List<String> searchHistory = new ArrayList<>(); // no search history initially

    // Set business information.
    boolean isUserBusinessOwner = true;
    List<String> productIds = new ArrayList<>();
    
    // check if account already exists, so we don't overwrite search history or
    // product ids. Null value indicates that account doesn't exist.
    Entity result = ServletLibrary.checkAccountIsRegistered(datastore, userId);

    // Create account entity object that will be stored.
    Entity account = new Entity("Account", userId);
    account.setProperty("userId", userId);
    account.setProperty("userEmail", userEmail);
    account.setProperty("nickname", businessName);
    account.setProperty("street", street);
    account.setProperty("city", city);
    account.setProperty("state", state);
    account.setProperty("zipCode", zipCode);
    if (result == null) account.setProperty("searchHistory", searchHistory);
    account.setProperty("isUserBusinessOwner", isUserBusinessOwner);

    // Set up business information to be stored.
    Entity business = new Entity("Business", userId);
    business.setProperty("businessId", userId); // Using user id as business id.
    business.setProperty("businessDisplayName", businessName);
    business.setProperty("street", street);
    business.setProperty("city", city);
    business.setProperty("state", state);
    business.setProperty("zipCode", zipCode);
    if (result == null) business.setProperty("productIds", productIds);

    // Store in datastore
    datastore.put(account);
    datastore.put(business);

    // Redirect to account page
    response.sendRedirect("/login");
  }

}