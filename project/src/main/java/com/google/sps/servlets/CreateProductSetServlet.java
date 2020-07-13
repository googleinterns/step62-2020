package com.google.sps.servlets;
 
import com.google.api.gax.longrunning.OperationFuture;
import com.google.cloud.vision.v1.*;
 
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

import com.google.sps.data.ServletsLibrary;
import com.google.sps.data.ProductSetItem;
 
 
 
@WebServlet("/product-set")
public class CreateProductSetServlet extends HttpServlet {
 
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    String projectId = "cloudberry-step-2020";
    String computeRegion = "us-east1";
    String productSetId = request.getParameter("product-set-id");
    String productSetDisplay = request.getParameter("product-set-display");
    
    ProductSetItem productSetItem = ServletsLibrary.createProductSet(projectId, computeRegion, productSetId, productSetDisplay);
 
    response.sendRedirect("/index.html");
  }
}
