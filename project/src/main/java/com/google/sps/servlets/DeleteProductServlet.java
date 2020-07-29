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
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.Filter;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import com.google.sps.data.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@WebServlet("/deleteProduct")
public class DeleteProductServlet extends HttpServlet {

  protected DatastoreService datastore;
  protected UserService userService;

  public DeleteProductServlet() {
    super();
    datastore = DatastoreServiceFactory.getDatastoreService();
    userService = UserServiceFactory.getUserService();
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String productId = request.getParameter("productId");
    if (productId == null) {
      System.err.println("ProductId was not given!");
      response.sendRedirect("viewProducts.html");
      return;
    }

    // Delete the product from the relevant database tables. 
    ProductEntity product = ServletLibrary.retrieveProductInfo(datastore, productId);
    ServletLibrary.deleteProductFromBusiness(datastore, productId, userService.getCurrentUser().getUserId());
    ServletLibrary.deleteProductFromLabels(datastore, productId, product.getLabels());
    ServletLibrary.deleteProductFromProductSet(datastore, productId, product.getProductSetId());
    ServletLibrary.deleteProductFromProductCategory(datastore, productId, product.getProductCategory());
    Filter filter = new FilterPredicate("productId", FilterOperator.EQUAL, productId);
    PreparedQuery pq = datastore.prepare(new Query("Product").setFilter(filter));
    Entity entity = pq.asSingleEntity();
    datastore.delete(entity.getKey()); // Delete from the product table

    //Delete from product search Api
    // ProductSearchLibrary.deleteProduct(productId);
    
    response.sendRedirect("viewProducts.html");
  }

}