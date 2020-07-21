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

import com.google.sps.data.*;

// This servlet retrieves the account information of the user and sends it as
// a response.
@WebServlet("/searchInfo")
public class SearchInfoServlet extends HttpServlet {

  protected DatastoreService datastore;
  protected Gson gson;

  public SearchInfoServlet() {
    super();
    datastore = DatastoreServiceFactory.getDatastoreService();
    gson = new Gson();
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String searchId = request.getParameter("searchId");
    SearchInfo searchInfo = ServletLibrary.retrieveSearchInfo(datastore, searchId);

    String json = gson.toJson(searchInfo);

    // Send the response.
    response.setContentType("application/json;");
    response.getWriter().println(json);
  }

}