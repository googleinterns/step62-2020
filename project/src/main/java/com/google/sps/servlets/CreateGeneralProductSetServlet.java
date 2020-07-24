package com.google.sps.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.sps.data.ProductSearchLibrary;


@WebServlet("/createProductSearchSet")
public class CreateGeneralProductSetServlet extends HttpServlet {


    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // Servlet that creates a product set that takes in all products to facilitate searching by image
        String productSetId = "cloudberryAllProducts";
        String productSetDisplayName = "allProducts";
        ProductSearchLibrary.createProductSet(productSetId, productSetDisplayName);

        response.sendRedirect("/index.html");
    }

    
}