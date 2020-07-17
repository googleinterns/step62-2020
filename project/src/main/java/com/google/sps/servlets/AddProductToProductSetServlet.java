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
 
@WebServlet("/add-product")
public class AddProductToProductSetServlet extends HttpServlet {
 
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    try{
        String projectId = "cloudberry-step-2";
        String computeRegion = "us-east1";
        String productId = request.getParameter("product-id");
        String productSetId = request.getParameter("product-set-id");
    
        ServletsLibrary.addProductToProductSet(projectId, computeRegion, productId, productSetId);
    } catch(Exception e){
        response.getWriter().println("Cannot retrieve input");
    }

    
    response.sendRedirect("create-product.html");

  } 
}
