package com.google.sps.servlets;
 

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.io.IOException;


import com.google.gson.Gson;
import com.google.sps.data.ProductSetItem;
import com.google.sps.data.ProductItem;

import com.google.sps.data.ServletsLibrary;
 
 
 
@WebServlet("/product-list")
public class GetProductsInProductSetServlet extends HttpServlet {
  Gson gson = new Gson();
  
 
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    String projectId = "cloudberry-step-2020";
    String computeRegion = "us-east1";
    // String productSetId = request.getParameter("productSetId");
    ArrayList<ProductItem> products = ServletsLibrary.listProducts(projectId, computeRegion);

    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(products));
    
    
    
  }
  
}
