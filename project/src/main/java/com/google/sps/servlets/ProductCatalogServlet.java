package com.google.sps.servlets;
 
import com.google.api.gax.longrunning.OperationFuture;
import com.google.cloud.vision.v1.*;
 
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
 
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
 
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
 
import com.google.appengine.api.datastore.Entity;
 
 
 
@WebServlet("/product-catalog")
public class ProductCatalogServlet extends HttpServlet {
 
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String projectId = request.getParameter("project-id");
    String computeRegion = request.getParameter("compute-region");
    String productSetId = request.getParameter("product-set-id");
    String productSetDisplay = request.getParameter("product-set-display");
 
    createProductSet(projectId, computeRegion, productSetId, productSetDisplay);
    
    response.sendRedirect("/create-product-set.html");
  }
 
  public static void createProductSet(
  String projectId, String computeRegion, String productSetId, String productSetDisplayName)
  throws IOException {
    try (ProductSearchClient client = ProductSearchClient.create()) {
 
      // A resource that represents Google Cloud Platform location.
      String formattedParent = ProductSearchClient.formatLocationName(projectId, computeRegion);
 
      // Create a product set with the product set specification in the region.
      ProductSet myProductSet =
        ProductSet.newBuilder().setDisplayName(productSetDisplayName).build();
      CreateProductSetRequest request =
        CreateProductSetRequest.newBuilder()
            .setParent(formattedParent)
            .setProductSet(myProductSet)
            .setProductSetId(productSetId)
            .build();
      ProductSet productSet = client.createProductSet(request);
      // Display the product set information
      System.out.println(String.format("Product set name: %s", productSet.getName()));
    }
  } 
}
