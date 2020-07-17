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

import com.google.sps.data.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

// Change this.
@WebServlet("/productInfo")
public class ProductInfoServlet extends HttpServlet {

  protected DatastoreService datastore;
  protected Gson gson;
  protected UserService userService;

  public ProductInfoServlet() {
    super();
    datastore = DatastoreServiceFactory.getDatastoreService();
    userService = UserServiceFactory.getUserService();
    gson = new Gson();
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String productId = request.getParameter("productId");

    ProductEntity product = ServletLibrary.retrieveProductInfo(datastore, productId);

    String json = null;
    if (product != null) {
      Business business = ServletLibrary.retrieveBusinessInfo(datastore, product.getBusinessId());
      // The false parameter indicates that we are searching the product set list with the id, not the name.
      ProductSetEntity productSet = ServletLibrary.retrieveProductSetInfo(datastore, product.getProductSetId(), false);
      if (business != null && productSet != null) {
        json = gson.toJson(new ProductInfo(product, productSet, business));
      }
    }

    // Send over the json response.
    response.setContentType("application/json;");
    response.getWriter().println(json);
  }
}