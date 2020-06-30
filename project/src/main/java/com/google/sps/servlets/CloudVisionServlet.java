package com.google.sps.servlets;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Date;
import java.awt.Color;
import com.google.sps.data.*;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.FetchOptions;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import com.google.cloud.vision.v1.*;
import com.google.cloud.vision.v1.WebDetection.WebEntity;
import com.google.cloud.vision.v1.WebDetection.WebLabel;
import com.google.cloud.vision.v1.WebDetection.WebPage;
import com.google.protobuf.ByteString;
import java.io.ByteArrayOutputStream;

import java.util.Random;
import java.math.BigDecimal; 


@WebServlet("/cloudVision")
public class CloudVisionServlet extends HttpServlet {

  protected Gson gson;
  protected BlobstoreService blobstore;
  protected ImagesService imagesService;
  protected List<Feature> allFeatures;
  protected DatastoreService datastore;
  protected UserService userService;

  public CloudVisionServlet() {
    super();
    gson = new Gson();
    blobstore = BlobstoreServiceFactory.getBlobstoreService();
    imagesService = ImagesServiceFactory.getImagesService();
    datastore = DatastoreServiceFactory.getDatastoreService();
    userService = UserServiceFactory.getUserService();
    Feature labelDetection = Feature.newBuilder().setType(Feature.Type.LABEL_DETECTION).build();
    Feature logoDetection = Feature.newBuilder().setType(Feature.Type.LOGO_DETECTION).build();
    Feature textDetection = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
    Feature imageProperties = Feature.newBuilder().setType(Feature.Type.IMAGE_PROPERTIES).build();
    Feature objectLocalization = Feature.newBuilder().setType(Feature.Type.OBJECT_LOCALIZATION).build();
    Feature webDetection = Feature.newBuilder().setType(Feature.Type.WEB_DETECTION).build();
    allFeatures = Arrays.asList(labelDetection, logoDetection, textDetection, imageProperties, objectLocalization, webDetection);
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // TODO: Retrieve the temporary vision annotation from the business account.
    Business business = ServletLibrary.retrieveBusinessInfo(datastore, userService.getCurrentUser().getUserId());
    String json = business.getTempVisionAnnotation();
    // need to send labels as well.
    // Send the json of the cloud vision annotation over.
    response.setContentType("application/json;");
    response.getWriter().println(json);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get the URL of the image that the user uploaded.
    BlobKey blobKey = VisionLibrary.getBlobKey(blobstore, request, "imageURL");
    String imageURL = VisionLibrary.getUploadedFileUrl(imagesService, blobKey);

    // Use blobKey to send a request to the cloud vision api. We are guaranteed 
    // that the client uploaded an image.
    byte[] blobBytes = VisionLibrary.getBlobBytes(blobstore, blobKey);
    AnnotateImageResponse imageResponse = VisionLibrary.handleCloudVisionRequest(blobBytes, allFeatures);
    String tempVisionAnnotation = VisionLibrary.formatImageResponse(imagesService, imageResponse, blobKey);

    // Store the response in the business account.
    Entity business = new Entity("Business", userService.getCurrentUser().getUserId());
    business.setProperty("tempVisionAnnotation", tempVisionAnnotation);
    datastore.put(business);

    // Redirect to the create product form. 
    response.sendRedirect("/createProduct.html");
  }
}