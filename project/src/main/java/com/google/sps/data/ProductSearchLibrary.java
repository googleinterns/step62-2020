package com.google.sps.data;

import com.google.api.gax.longrunning.OperationFuture;
import com.google.cloud.vision.v1.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;


 
import java.io.IOException;

public class ProductSearchLibrary{

    private static final String projectId = "cloudberry-step-2020";
    private static final String computeRegion = "us-east1";
    
    public static ArrayList<ProductSetItem> listProductSets(String projectId,
     String computeRegion) throws IOException {

      ArrayList<ProductSetItem> productSets = new ArrayList<>();

      try (ProductSearchClient client = ProductSearchClient.create()) {
        // A resource that represents Google Cloud Platform location.
        String formattedParent = ProductSearchClient.formatLocationName(projectId, computeRegion);
        // List all the product sets available in the region.
        for (ProductSet productSet : client.listProductSets(formattedParent).iterateAll()) {

            // The set id of a product set is the last directory of a file path and to get this file path
            // the index of the last backslash symbol is determined and a substring from the index to the end of the file path is made
            // to get the set id of a product set.
            createProductSetItemAndAddToList(productSet.getName().substring(productSet.getName().lastIndexOf('/') + 1),
                                            productSet.getDisplayName(), productSets);
            
            
        }
      } 

    return productSets;
  }

  public static ProductSetItem createProductSet( 
    String productSetId, 
    String productSetDisplayName) throws IOException {
    
    ProductSetItem newProductSet = new ProductSetItem(productSetId, productSetDisplayName);

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
    }
    return newProductSet;
  }

  public static void createReferenceImage(
    String productId,
    String referenceImageId,
    String gcsUri) throws IOException {
    
        ProductSearchClient client = ProductSearchClient.create();
        // Get the full path of the product.
        String formattedParent =
            ProductSearchClient.formatProductName(projectId, computeRegion, productId);
        // Create a reference image.
        ReferenceImage referenceImage = ReferenceImage.newBuilder().setUri(gcsUri).build();

        ReferenceImage image =
            client.createReferenceImage(formattedParent, referenceImage, referenceImageId);

  } 

  public static void addProductToProductSet(
    String productId, 
    String productSetId) throws IOException {
    try (ProductSearchClient client = ProductSearchClient.create()) {

        // Get the full path of the product set.
        String formattedName =
            ProductSearchClient.formatProductSetName(projectId, computeRegion, productSetId);

        // Get the full path of the product.
        String productPath = ProductName.of(projectId, computeRegion, productId).toString();

        // Add the product to the product set.
        client.addProductToProductSet(formattedName, productPath);
    }
  }

  public static void createProduct(
    String productId,
    String productDisplayName,
    String productCategory) throws IOException {
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
    }
  }

  public static void deleteProductSet(
    String projectId, 
    String computeRegion, 
    String productSetId)
    throws IOException {
    try (ProductSearchClient client = ProductSearchClient.create()) {

        // Get the full path of the product set.
        String formattedName =
            ProductSearchClient.formatProductSetName(projectId, computeRegion, productSetId);
        // Delete the product set.
        client.deleteProductSet(formattedName);
    }
  }

  public static void removeProductFromProductSet(
    String projectId, 
    String computeRegion, 
    String productId, 
    String productSetId)
    throws IOException {
    try (ProductSearchClient client = ProductSearchClient.create()) {

        // Get the full path of the product set.
        String formattedParent =
            ProductSearchClient.formatProductSetName(projectId, computeRegion, productSetId);

        // Get the full path of the product.
        String formattedName =
            ProductSearchClient.formatProductName(projectId, computeRegion, productId);

        // Remove the product from the product set.
        client.removeProductFromProductSet(formattedParent, formattedName);
    } 
  }

  public static ArrayList<ProductItem> listProducts() throws IOException {
    ArrayList<ProductItem> products = new ArrayList<>();
    try (ProductSearchClient client = ProductSearchClient.create()) {

        // A resource that represents Google Cloud Platform location.
        String formattedParent = ProductSearchClient.formatLocationName(projectId, computeRegion);

        // List all the products available in the region.
        for (Product product : client.listProducts(formattedParent).iterateAll()) {

            // The set id of a product set is the last directory of a file path and to get this file path
            // the index of the last backslash symbol is determined and a substring from the index to the end of the file path is made
            // to get the set id of a product set.
            createProductItemAndAddToList(product.getName().substring(product.getName().lastIndexOf('/') + 1), product.getDisplayName(),
                                        product.getProductCategory(), products);
        }
    }
    return products;
  }

  public static ArrayList<ProductItem> listProductsInProductSet(
    String projectId, 
    String computeRegion, 
    String productSetId) throws IOException {
    ArrayList<ProductItem> productItems = new ArrayList<>();
    try (ProductSearchClient client = ProductSearchClient.create()) {

        // Get the full path of the product set.
        String formattedName =
            ProductSearchClient.formatProductSetName(projectId, computeRegion, productSetId);
        // List all the products available in the product set.
        for (Product product : client.listProductsInProductSet(formattedName).iterateAll()) {

            createProductItemAndAddToList(product.getName().substring(product.getName().lastIndexOf('/') + 1), product.getDisplayName(),
                                        product.getProductCategory(), productItems);
        }
    }
    return productItems;
  }
   
   public static ArrayList<String> listReferenceImagesOfProduct(
    String productId) throws IOException {
    ArrayList<String> referenceImages = new ArrayList<>();
    try (ProductSearchClient client = ProductSearchClient.create()) {
        // Get the full path of the product.
        String formattedParent =
            ProductSearchClient.formatProductName(projectId, computeRegion, productId);
        for (ReferenceImage image : client.listReferenceImages(formattedParent).iterateAll()) {
            referenceImages.add(image.getUri());
        }
    }
    return referenceImages;
  }

  public static ArrayList<String> getSimilarProductsGcs(
    String productSetId,
    String productCategory,
    String gcsUri,
    String filter)
    throws IOException {
    
    ArrayList<String> productIds = new ArrayList<>();
    try (ImageAnnotatorClient queryImageClient = ImageAnnotatorClient.create()) {
        

        // Get the full path of the product set.
        String productSetPath = ProductSetName.of(projectId, computeRegion, productSetId).toString();

        // Get the image from Google Cloud Storage
        ImageSource source = ImageSource.newBuilder().setGcsImageUri(gcsUri).build();

        // Create annotate image request along with product search feature.
        Feature featuresElement = Feature.newBuilder().setType(Feature.Type.PRODUCT_SEARCH).build();
        Image image = Image.newBuilder().setSource(source).build();
        ImageContext imageContext =
            ImageContext.newBuilder()
                .setProductSearchParams(
                    ProductSearchParams.newBuilder()
                        .setProductSet(productSetPath)
                        .addProductCategories(productCategory)
                        .setFilter(filter))
                .build();

        AnnotateImageRequest annotateImageRequest =
            AnnotateImageRequest.newBuilder()
                .addFeatures(featuresElement)
                .setImage(image)
                .setImageContext(imageContext)
                .build();
        List<AnnotateImageRequest> requests = Arrays.asList(annotateImageRequest);

        // Search products similar to the image.
        BatchAnnotateImagesResponse response = queryImageClient.batchAnnotateImages(requests);


        // The first index of the responses is called to determine 
        // the products that are similar to the uploaded image
        List<ProductSearchResults.Result> similarProducts =
            response.getResponses(0).getProductSearchResults().getResultsList();
            
            
        for (ProductSearchResults.Result product : similarProducts) {
            
            String id = getProductId(product.getProduct().getName());
            productIds.add(id);
        }
    }
    return productIds;
  }

  private static void createProductItemAndAddToList(String productId, String productDisplayName, String productCategory, ArrayList<ProductItem> productList){
      ProductItem product = new ProductItem(productId, productDisplayName, productCategory);

      productList.add(product);
    }

  private static void createProductSetItemAndAddToList(String productSetId, String productSetDisplayName, ArrayList<ProductSetItem> productSetList){
      ProductSetItem productSet = new ProductSetItem(productSetId, productSetDisplayName);

      productSetList.add(productSet);
    }

    public static void deleteProduct(String productId)
    throws IOException {
        try (ProductSearchClient client = ProductSearchClient.create()) {

            // Get the full path of the product.
            String formattedName =
                ProductSearchClient.formatProductName(projectId, computeRegion, productId);

            // Delete a product.
            client.deleteProduct(formattedName);
            System.out.println("Product deleted.");
        }
    }

    public static String getProductId(String productName){

        String[] productNameSplit = productName.split("/");

        int productNameArrayLength = productNameSplit.length;

        return productNameSplit[productNameArrayLength - 1];

    }
}
