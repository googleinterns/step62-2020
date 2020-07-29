package com.google.sps.servlets;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.sps.data.*;
import java.util.List;
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
@WebServlet("/searchHistory")
public class SearchHistoryServlet extends HttpServlet {

  protected DatastoreService datastore;
  protected UserService userService;
  protected Gson gson;

  public SearchHistoryServlet() {
    super();
    datastore = DatastoreServiceFactory.getDatastoreService();
    userService = UserServiceFactory.getUserService();
    gson = new Gson();
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

  // Retrieve 50 most recent searches by the user.
  List<SearchInfo> searchHistory = 
    ServletLibrary.retrieveRecentSearchHistory(datastore, userService.getCurrentUser().getUserId(), 50);
  
  String json = gson.toJson(searchHistory);
  response.setContentType("application/json;");
  response.getWriter().println(json);

  }

}