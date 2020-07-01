package com.google.sps.servlets;
 

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.io.IOException;


import com.google.gson.Gson;
import com.google.sps.data.Productset;

import com.google.sps.data.ServletsLibrary;
 
 
 
@WebServlet("/product-list")
public class GetProductSetsServlet extends HttpServlet {
  Gson gson = new Gson();
  
 
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
    String projectId = "cloudberry-step-2020";
    String computeRegion = "us-east1";
    
    ArrayList<Productset> productSets = ServletsLibrary.listProductSets(projectId, computeRegion);
    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(productSets));
  }
  
}
