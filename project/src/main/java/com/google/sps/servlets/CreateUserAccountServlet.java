package com.google.sps.servlets;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.GeoPt;
import com.google.appengine.api.datastore.Entity;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import com.google.sps.data.*;
import java.util.ArrayList;
import java.util.List;

// The purpose of this servlet is to collect data from the user account creation 
// form and add the information to the database.  It is also used to update existing
// information in the database.
@WebServlet("/createUserAccount")
public class CreateUserAccountServlet extends HttpServlet {

  protected DatastoreService datastore;
  protected UserService userService;

  public CreateUserAccountServlet() {
    super();
    datastore = DatastoreServiceFactory.getDatastoreService();
    userService = UserServiceFactory.getUserService();
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get required userId.
    String userId = userService.getCurrentUser().getUserId();

    // check if account already exists, so we don't overwrite search history or
    // product ids. Null value indicates that account doesn't exist.
    Account account = ServletLibrary.retrieveAccountInfo(datastore, userService, userId);
    
    // Get the latitude and longitude
    float lat;
    float lng;
    try {
      lat = Float.parseFloat(request.getParameter("lat"));
      lng = Float.parseFloat(request.getParameter("lng"));
    } catch (NumberFormatException e) {
      System.err.println("Float was not able to be parsed! Error:"+e);
      lat = 0.0f;
      lng = 0.0f;
    }
    GeoPt latLng = new GeoPt(lat, lng);

    // Create entity object that will be stored
    Entity newAccount = new Entity("Account", userId);
    newAccount.setProperty("userId", userId);
    newAccount.setProperty("userEmail", userService.getCurrentUser().getEmail());
    newAccount.setProperty("nickname", request.getParameter("nickname"));
    newAccount.setProperty("street", request.getParameter("street"));
    newAccount.setProperty("city", request.getParameter("city"));
    newAccount.setProperty("state", request.getParameter("state"));
    newAccount.setProperty("zipCode", request.getParameter("zipCode"));
    newAccount.setProperty("latLng", latLng);
    if (account == null) {
      newAccount.setProperty("searchHistory", new ArrayList<String>());
    } else {
      newAccount.setProperty("searchHistory", account.getSearchHistory());
    }
    newAccount.setProperty("isUserBusinessOwner", false);

    // Store in datastore
    datastore.put(newAccount);

    // Redirect to account page
    response.sendRedirect("/login");
  }

}