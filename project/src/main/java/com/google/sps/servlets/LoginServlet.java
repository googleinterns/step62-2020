package com.google.sps.servlets;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Filter;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

// This servlet redirects to the correct page depending on the login status of the user.
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

  protected DatastoreService datastore;
  protected UserService userService;

  public LoginServlet() {
    super();
    datastore = DatastoreServiceFactory.getDatastoreService();
    userService = UserServiceFactory.getUserService();
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    // First check if the user is logged in, otherwise redirect to google login page.
    if (userService.isUserLoggedIn()) {
      // Next check if the account is registered in the database, otherwise go to account creation.
      Filter filter = new FilterPredicate("userId", FilterOperator.EQUAL, userService.getCurrentUser().getUserId());
      Query query = new Query("userId").setFilter(filter);
      PreparedQuery pq = datastore.prepare(query);
      Entity result = pq.asSingleEntity();
      if (result != null) {
        // Check if account is a business owner or not and redirect appropriately.
        boolean isUserBusinessOwner = (boolean) result.getProperty("isUserBusinessOwner");
        if (isUserBusinessOwner) {
          response.sendRedirect("/businessAccount.html");
        } else {
          response.sendRedirect("/userAccount.html");
        }
      } else {
        response.sendRedirect("/chooseAccount.html");
      }
    } else {
      String loginUrl = userService.createLoginURL("/login");
      response.sendRedirect(loginUrl);
    }
  }

}