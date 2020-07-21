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

import com.google.sps.data.ServletLibrary;
import com.google.sps.data.ProductEntity;
import com.google.sps.data.ProductSearchLibrary;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import com.google.sps.data.ProductItem;

@WebServlet("/view-reference-image")
public class GetReferenceImagesServlet extends HttpServlet {

  DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  Gson gson = new Gson();
  UserService userService = UserServiceFactory.getUserService();
  ArrayList<String> referenceImages = new ArrayList<>();

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        ArrayList<ProductItem> products = ProductSearchLibrary.listProducts();

        for(ProductItem product : products){
            referenceImages.addAll(ProductSearchLibrary.listReferenceImagesOfProduct(product.getProductId()));
        }

        String json = gson.toJson(referenceImages);


        response.setContentType("application/json;");
        response.getWriter().println(json);
   }
}