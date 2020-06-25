package com.google.sps.servlets;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.sps.data.Account;
import com.google.sps.data.ServletLibrary;
import com.google.gson.Gson;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.Filter;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

// This servlet retrieves the account information of the user and sends it as
// a response.
@WebServlet("/account")
public class AccountServlet extends HttpServlet {

  protected DatastoreService datastore;
  protected UserService userService;
  protected Gson gson;

  public AccountServlet() {
    super();
    datastore = DatastoreServiceFactory.getDatastoreService();
    userService = UserServiceFactory.getUserService();
    gson = new Gson();
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    // First check if the user is logged in, otherwise redirect to google login page.
    if (userService.isUserLoggedIn()) {
      // Next check if the account is registered in the database, otherwise go to account creation.
      Entity result = ServletLibrary.checkAccountIsRegistered(datastore, userService.getCurrentUser().getUserId());
      if (result != null) {
        // Retrieve all account information.
        Account account = ServletLibrary.retrieveAccountInfo(result, userService);
        String json = gson.toJson(account);
        response.setContentType("application/json;");
        response.getWriter().println(json);
      } else {
        response.sendRedirect("/chooseAccount.html");
      }
    } else {
      String loginUrl = userService.createLoginURL("/login");
      response.sendRedirect(loginUrl);
    }
  }

}