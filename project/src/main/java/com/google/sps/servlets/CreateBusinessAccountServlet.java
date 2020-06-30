package com.google.sps.servlets;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Text;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import com.google.sps.data.*;
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
    // This is used to store the cloud vision annotation of the most recent image
    // that the account uploaded. 
    Text tempVisionAnnotation = null; 

    
    // check if account already exists, so we don't overwrite search history or
    // product ids. Null value indicates that account doesn't exist.
    Account account = ServletLibrary.retrieveAccountInfo(datastore, userService, userId);
    Business business = ServletLibrary.retrieveBusinessInfo(datastore, userId);

    // Create account entity object that will be stored.
    Entity newAccount = new Entity("Account", userId);
    newAccount.setProperty("userId", userId);
    newAccount.setProperty("userEmail", userEmail);
    newAccount.setProperty("nickname", businessName);
    newAccount.setProperty("street", street);
    newAccount.setProperty("city", city);
    newAccount.setProperty("state", state);
    newAccount.setProperty("zipCode", zipCode);
    if (account == null) {
      newAccount.setProperty("searchHistory", searchHistory);
    } else {
      newAccount.setProperty("searchHistory", account.getSearchHistory());
    }
    newAccount.setProperty("isUserBusinessOwner", isUserBusinessOwner);

    // Set up business information to be stored.
    Entity newBusiness = new Entity("Business", userId);
    newBusiness.setProperty("businessId", userId); // Using user id as business id.
    newBusiness.setProperty("businessDisplayName", businessName);
    newBusiness.setProperty("street", street);
    newBusiness.setProperty("city", city);
    newBusiness.setProperty("state", state);
    newBusiness.setProperty("zipCode", zipCode);
    if (business == null) {
      newBusiness.setProperty("productIds", productIds);
    } else {
      newBusiness.setProperty("productIds", business.getProductIds());
    }
    newBusiness.setProperty("tempVisionAnnotation", tempVisionAnnotation);

    // Store in datastore
    datastore.put(newAccount);
    datastore.put(newBusiness);

    // Redirect to account page
    response.sendRedirect("/login");
  }

}