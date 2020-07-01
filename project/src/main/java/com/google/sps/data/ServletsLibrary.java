package com.google.sps.data;

import com.google.api.gax.longrunning.OperationFuture;
import com.google.cloud.vision.v1.*;

import java.util.*;
 
import java.io.IOException;

public class ServletsLibrary{



    public static ArrayList<Productset> listProductSets(String projectId, String computeRegion) throws IOException {

    ArrayList<Productset> productsets = new ArrayList<>();

    try (ProductSearchClient client = ProductSearchClient.create()) {
        // A resource that represents Google Cloud Platform location.
        String formattedParent = ProductSearchClient.formatLocationName(projectId, computeRegion);
        // List all the product sets available in the region.
        for (ProductSet productSet : client.listProductSets(formattedParent).iterateAll()) {
        // Display the product set information
        System.out.println(String.format("Product set name: %s", productSet.getName()));
        System.out.println(
            String.format(
                "Product set id: %s",
                productSet.getName().substring(productSet.getName().lastIndexOf('/') + 1)));
        System.out.println(
            String.format("Product set display name: %s", productSet.getDisplayName()));
        System.out.println("Product set index time:");
        System.out.println(String.format("\tseconds: %s", productSet.getIndexTime().getSeconds()));
        System.out.println(String.format("\tnanos: %s", productSet.getIndexTime().getNanos()));

        Productset productset = new Productset(productSet.getName().substring(productSet.getName().lastIndexOf('/') + 1),
                                    productSet.getDisplayName());
        // The set id of a product set is the last directory of a file path and to get this file path
        // the index of the last backslash symbol is determined and a substring from the index to the end of the file path is made
        // to get the set id of a product set.
        productsets.add(productset);
        }
    }

    return productsets;
  }

  public static void createProductSet(
  String projectId, String computeRegion, String productSetId, String productSetDisplayName)
  throws IOException {
    try (ProductSearchClient client = ProductSearchClient.create()) {
 
      // A resource that represents Google Cloud Platform location.
      String formattedParent = ProductSearchClient.formatLocationName(projectId, computeRegion);
 
      // Create a product set with the product set specification in the region.
      ProductSet myProductSet =
        ProductSet.newBuilder().setDisplayName(productSetDisplayName).build();
      CreateProductSetRequest request =
        CreateProductSetRequest.newBuilder()
            .setParent(formattedParent)
            .setProductSet(myProductSet)
            .setProductSetId(productSetId)
            .build();
      ProductSet productSet = client.createProductSet(request);
      // Display the product set information
      System.out.println(String.format("Product set name: %s", productSet.getName()));
    }
  }

  public static void createReferenceImage(
    String projectId,
    String computeRegion,
    String productId,
    String referenceImageId,
    String gcsUri)
    throws IOException {
    try (ProductSearchClient client = ProductSearchClient.create()) {

        // Get the full path of the product.
        String formattedParent =
            ProductSearchClient.formatProductName(projectId, computeRegion, productId);
        // Create a reference image.
        ReferenceImage referenceImage = ReferenceImage.newBuilder().setUri(gcsUri).build();

        ReferenceImage image =
            client.createReferenceImage(formattedParent, referenceImage, referenceImageId);
        // Display the reference image information.
        System.out.println(String.format("Reference image name: %s", image.getName()));
        System.out.println(String.format("Reference image uri: %s", image.getUri()));
    }
  } 

  public static void addProductToProductSet(
    String projectId, String computeRegion, String productId, String productSetId)
    throws IOException {
    try (ProductSearchClient client = ProductSearchClient.create()) {

        // Get the full path of the product set.
        String formattedName =
            ProductSearchClient.formatProductSetName(projectId, computeRegion, productSetId);

        // Get the full path of the product.
        String productPath = ProductName.of(projectId, computeRegion, productId).toString();

        // Add the product to the product set.
        client.addProductToProductSet(formattedName, productPath);

        System.out.println(String.format("Product added to product set."));
    }
  }

  public static void createProduct(
    String projectId,
    String computeRegion,
    String productId,
    String productDisplayName,
    String productCategory)
    throws IOException {
    try (ProductSearchClient client = ProductSearchClient.create()) {

        // A resource that represents Google Cloud Platform location.
        String formattedParent = ProductSearchClient.formatLocationName(projectId, computeRegion);
        // Create a product with the product specification in the region.
        // Multiple labels are also supported.
        Product myProduct =
            Product.newBuilder()
                .setName(productId)
                .setDisplayName(productDisplayName)
                .setProductCategory(productCategory)
                .build();
        Product product = client.createProduct(formattedParent, myProduct, productId);
        // Display the product information
        System.out.println(String.format("Product name: %s", product.getName()));
    }
  }

  public static ArrayList<ProductItem> listProductsInProductSet(
    String projectId, String computeRegion, String productSetId) throws IOException {
        ArrayList<ProductItem> productItems = new ArrayList<>();
  try (ProductSearchClient client = ProductSearchClient.create()) {

    // Get the full path of the product set.
    String formattedName =
        ProductSearchClient.formatProductSetName(projectId, computeRegion, productSetId);
    // List all the products available in the product set.
    for (Product product : client.listProductsInProductSet(formattedName).iterateAll()) {
      // Display the product information
      System.out.println(String.format("Product name: %s", product.getName()));
      System.out.println(
          String.format(
              "Product id: %s",
              product.getName().substring(product.getName().lastIndexOf('/') + 1)));
      System.out.println(String.format("Product display name: %s", product.getDisplayName()));
      System.out.println(String.format("Product description: %s", product.getDescription()));
      System.out.println(String.format("Product category: %s", product.getProductCategory()));
      System.out.println("Product labels: ");
      ProductItem productItem = new ProductItem(product.getName().substring(product.getName().lastIndexOf('/') + 1), product.getDisplayName(),
                                product.getProductCategory());
      productItems.add(productItem);
      for (Product.KeyValue element : product.getProductLabelsList()) {
        System.out.println(String.format("%s: %s", element.getKey(), element.getValue()));
      }
    }
  }
  return productItems;
}

}