package com.google.sps.data;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Date;
import java.awt.Color;

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

import com.google.cloud.vision.v1.*;
import com.google.cloud.vision.v1.WebDetection.WebEntity;
import com.google.cloud.vision.v1.WebDetection.WebLabel;
import com.google.cloud.vision.v1.WebDetection.WebPage;
import com.google.protobuf.ByteString;
import java.io.ByteArrayOutputStream;

import java.util.Random;
import java.math.BigDecimal; 


public class VisionLibrary {
  public static AnnotateImageResponse handleCloudVisionRequest(byte[] imageBytes, List<Feature> allFeatures) throws IOException {
    // Convert image bytes into proper image format.
    ByteString byteString = ByteString.copyFrom(imageBytes);
    Image image = Image.newBuilder().setContent(byteString).build();

    // Create a request with all the features desired.
    AnnotateImageRequest request = AnnotateImageRequest.newBuilder().addAllFeatures(allFeatures).setImage(image).build();
    List<AnnotateImageRequest> requests = new ArrayList<>();
    requests.add(request);

    // Connect to cloud vision client and send request.
    ImageAnnotatorClient client = ImageAnnotatorClient.create();
    BatchAnnotateImagesResponse batchResponse = client.batchAnnotateImages(requests);
    client.close();

    // Receive the reponse and handle potential errors.
    List<AnnotateImageResponse> imageResponses = batchResponse.getResponsesList();
    AnnotateImageResponse imageResponse = imageResponses.get(0);
    if (imageResponse.hasError()) {
      System.err.println("Error getting image labels: " + imageResponse.getError().getMessage());
      return null;
    }

    return imageResponse;
  }

  public static String formatImageResponse(AnnotateImageResponse imageResponse, BlobKey blobKey) {
    // These are labels for the overall image based on what the vision api thinks.
    List<EntityAnnotation> labelAnnotations = imageResponse.getLabelAnnotationsList();

    // These are labels based on images that are similar from the web.
    WebDetection webDetection = imageResponse.getWebDetection();
    List<WebEntity> webLabelAnnotations = webDetection.getWebEntitiesList();
    List<WebLabel> webBestLabelAnnotations = webDetection.getBestGuessLabelsList();
    List<WebPage> webPageAnnotations = webDetection.getPagesWithMatchingImagesList();

    // Any text found within an image is contained here.
    List<EntityAnnotation> textAnnotations = imageResponse.getTextAnnotationsList();

    // Gets a list of dominant colors in the image.
    DominantColorsAnnotation colors = imageResponse.getImagePropertiesAnnotation().getDominantColors();
    List<ColorInfo> colorAnnotations = colors.getColorsList();

    // Gets a list of all objects found in the image.
    List<LocalizedObjectAnnotation> objectAnnotations = imageResponse.getLocalizedObjectAnnotationsList();

    // Finds any logos that are in the image.
    List<EntityAnnotation> logoAnnotations = imageResponse.getLogoAnnotationsList();

    List<ImageLabel> genericLabels = new ArrayList<>();
    for (EntityAnnotation label : labelAnnotations) {
      genericLabels.add(new ImageLabel(label.getDescription(), label.getScore()));
    }

    List<ImageLabel> webLabels = new ArrayList<>();
    for (WebEntity label : webLabelAnnotations) {
      webLabels.add(new ImageLabel(label.getDescription(), label.getScore()));
    }

    List<String> webBestLabels = new ArrayList<>();
    for (WebLabel label : webBestLabelAnnotations) {
      webBestLabels.add(label.getLabel());
    }

    List<String> webUrls = new ArrayList<>();
    for (WebPage page : webPageAnnotations) {
      webUrls.add(page.getUrl());
    }

    List<String> textInImage = new ArrayList<>();
    for (EntityAnnotation textBlock : textAnnotations) {
      textInImage.add(textBlock.getDescription());
    }

    List<ImageLabel> dominantColors = new ArrayList<>();
    for (ColorInfo color : colorAnnotations) {
      String nearestColor = getNearestColor(convertToJavaColor(color.getColor()));
      dominantColors.add(new ImageLabel(nearestColor, color.getScore()));
    }

    List<ImageLabel> objectsInImage = new ArrayList<>();
    for (LocalizedObjectAnnotation object : objectAnnotations) {
      objectsInImage.add(new ImageLabel(object.getName(), object.getScore()));
    }

    List<ImageLabel> logosInImage = new ArrayList<>();
    for (EntityAnnotation logo : logoAnnotations) {
      logosInImage.add(new ImageLabel(logo.getDescription(), logo.getScore()));
    }

    String imageURL = getUploadedFileUrl(blobKey);
    gson = new Gson();
    return gson.toJson(new CloudVisionAnnotation(imageURL,
                                                 cleanUpLabels(genericLabels),
                                                 cleanUpLabels(webLabels),
                                                 webBestLabels,
                                                 webUrls,
                                                 textInImage,
                                                 cleanUpLabels(dominantColors),
                                                 cleanUpLabels(objectsInImage), 
                                                 cleanUpLabels(logosInImage)));
  }

  // This cleans up a imageLabel list by removing duplicates and blank labels.
  public static List<ImageLabel> cleanUpLabels(List<ImageLabel> labels) {
    List<ImageLabel> cleanedList = new ArrayList<>();
    Set<String> seen = new HashSet<>();
    for (ImageLabel label : labels) {
      String description = label.getDescription();
      if (seen.contains(description)) continue;
      if (description.isEmpty()) continue;
      cleanedList.add(label);
      seen.add(description);
    }
    return cleanedList;
  }

  /**
   * Returns the BlobKey that points to the file uploaded by the user, or null if the user didn't
   * upload a file.
   */
  public static BlobKey getBlobKey(BlobstoreService blobstoreService, HttpServletRequest request, String formInputElementName) {
    Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
    List<BlobKey> blobKeys = blobs.get(formInputElementName);

    // User submitted form without selecting a file, so we can't get a BlobKey. (dev server)
    if (blobKeys == null || blobKeys.isEmpty()) {
      return null;
    }

    // Our form only contains a single file input, so get the first index.
    BlobKey blobKey = blobKeys.get(0);

    // User submitted form without selecting a file, so the BlobKey is empty. (live server)
    BlobInfo blobInfo = new BlobInfoFactory().loadBlobInfo(blobKey);
    if (blobInfo.getSize() == 0) {
      blobstoreService.delete(blobKey);
      return null;
    }

    return blobKey;
  }

  /** Returns a URL that points to the uploaded file. */
  public static String getUploadedFileUrl(ImagesService imagesService, BlobKey blobKey) {
    // In the case that the user did not upload any image, return null.
    if (blobKey == null) return null;
    // Attempt to use imagesService API, otherwise serve blob directly through
    // a servlet.
    try {
      ServingUrlOptions options = ServingUrlOptions.Builder.withBlobKey(blobKey);
      // To support running in Google Cloud Shell with AppEngine's devserver, we must use the relative
      // path to the image, rather than the path returned by imagesService which contains a host.
      try {
        URL url = new URL(imagesService.getServingUrl(options));
        return url.getPath();
      } catch (MalformedURLException e) {
        return imagesService.getServingUrl(options);
      }
    } catch (Exception e) {
      return "/serveBlobstoreImage?blobKey=" + blobKey.getKeyString();
    }
  }

  /**
   * Blobstore stores files as binary data. This function retrieves the binary data stored at the
   * BlobKey parameter.
   */
  public static byte[] getBlobBytes(BlobstoreService blobstoreService, BlobKey blobKey) throws IOException {
    ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();

    int fetchSize = BlobstoreService.MAX_BLOB_FETCH_SIZE;
    long currentByteIndex = 0;
    boolean continueReading = true;
    while (continueReading) {
      // end index is inclusive, so we have to subtract 1 to get fetchSize bytes
      byte[] b =
          blobstoreService.fetchData(blobKey, currentByteIndex, currentByteIndex + fetchSize - 1);
      outputBytes.write(b);

      // if we read fewer bytes than we requested, then we reached the end
      if (b.length < fetchSize) {
        continueReading = false;
      }

      currentByteIndex += fetchSize;
    }

    return outputBytes.toByteArray();
  }
  
  public static String getNearestColor(Color color) {
    Color[] constantColors = new Color[] { Color.black, Color.blue, Color.cyan, Color.gray, Color.green, Color.magenta, Color.orange, Color.pink, Color.red, Color.white, Color.yellow };
    Color nearestColor = null;
    Integer nearestDistance = new Integer(Integer.MAX_VALUE);

    for (Color constantColor : constantColors) {
      int newDistance = colorDistance(constantColor, color);
      if (newDistance < nearestDistance) {
        nearestColor = constantColor;
        nearestDistance = newDistance;
      }
    }

    return getNearestColorString(nearestColor);
    // return nearestColor.toString();
  }

  // https://stackoverflow.com/questions/6334311/whats-the-best-way-to-round-a-color-object-to-the-nearest-color-constant
  // This link explaines the reasoning behind the magic numbers present in this function.
  // the main idea is that the closest color visually is not necessarily the closest
  // color in terms of a strict euclidean distance.
  public static int colorDistance(Color c1, Color c2) {
    // int red1 = c1.getRed();
    // int red2 = c2.getRed();
    // int rmean = (red1 + red2) >> 1;
    // int r = red1 - red2;
    // int g = c1.getGreen() - c2.getGreen();
    // int b = c1.getBlue() - c2.getBlue();
    // return (((512+rmean)*r*r)>>8) + 4*g*g + (((767-rmean)*b*b)>>8);
    int r = c1.getRed() - c2.getRed();
    int g = c1.getGreen() - c2.getGreen();
    int b = c1.getBlue() - c2.getBlue();
    return r*r + g*g + b*b;
  }  

  public static Color convertToJavaColor(com.google.type.Color color) {
    return new Color(Math.round(color.getRed()), Math.round(color.getGreen()), Math.round(color.getBlue()));
  }

  public static String getNearestColorString(Color color) {
    if (color.equals(Color.black)) return "black";
    else if (color.equals(Color.blue)) return "blue";
    else if (color.equals(Color.cyan)) return "cyan";
    else if (color.equals(Color.gray)) return "gray";
    else if (color.equals(Color.green)) return "green";
    else if (color.equals(Color.magenta)) return "magenta";
    else if (color.equals(Color.orange)) return "orange";
    else if (color.equals(Color.pink)) return "pink";
    else if (color.equals(Color.red)) return "red";
    else if (color.equals(Color.white)) return "white";
    else if (color.equals(Color.yellow)) return "yellow";
    else return "";
  }

}