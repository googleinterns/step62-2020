package com.google.sps.servlets;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsInputChannel;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;
import com.google.appengine.api.blobstore.UploadOptions;
import com.google.appengine.api.blobstore.FileInfo;


@WebServlet("/getBlobstoreUrl")
public class BlobstoreUrlServlet extends HttpServlet {

  protected BlobstoreService blobstoreService;

  public BlobstoreUrlServlet() {
    super();
    blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
  }

  //Creates an upload url for an image using a cloud storage bucket
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // TODO: make sure to set it to the cloudberry bucket when moving to cloudberry.
    
    // String bucketName = "cloudberry-step-2020-test-bucket"; // Use twhen deploying to cloudberry.
    String bucketName = "neelgandhi-step-2020-test-bucket"; // Use when deploying to neelgandhi.
    UploadOptions bucket = UploadOptions.Builder.withGoogleStorageBucketName(bucketName);

    String uploadUrl = blobstoreService.createUploadUrl("/cloudVision", bucket);

    response.setContentType("text/html");
    response.getWriter().println(uploadUrl);
  }
}


