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
 
@WebServlet("/delete-product-set")
public class DeleteProductSetServlet extends HttpServlet {
 
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
    String projectId = "cloudberry-step-2";
    String computeRegion = "us-east1";
    String productSetId = request.getParameter("productSet");
    
    ServletsLibrary.deleteProductSet(projectId, computeRegion, productSetId);

    response.sendRedirect("index.html");

  } 
}