// package com.google.sps.test;

// import org.junit.Assert;
// import org.junit.Before;
// import org.junit.Test;
// import org.junit.runner.RunWith;
// import org.junit.runners.JUnit4;

// import com.google.api.gax.longrunning.OperationFuture;
// import com.google.cloud.vision.v1.*;

// import java.util.*;
// import com.google.sps.data.ServletsLibrary;
// import com.google.sps.data.ProductItem;
// import com.google.sps.data.ProductSetItem;

// import java.io.IOException;

// @RunWith(JUnit4.class)
// public final class ProductSearchApisTest{

//     private static final String projectId = "cloudberry-step-2020";
//     private static final String computeRegion = "us-east1";
//     private static final String PRODUCT_SET_ID_A = "00000";
//     private static final String PRODUCT_SET_ID_B = "00001";
//     private static final String PRODUCT_SET_ID_C = "00002";
//     private static final String PRODUCT_SET_DISPLAY_NAME_A = "Shoes";
//     private static final String PRODUCT_SET_DISPLAY_NAME_B = "Hats";
//     private static final String PRODUCT_SET_DISPLAY_NAME_C = "Food";


//     @Test
//     public void canCreateAProductSet() throws IOException{
//         ProductSetItem expected = new ProductSetItem(PRODUCT_SET_ID_A, PRODUCT_SET_DISPLAY_NAME_A);  
//         ProductSetItem results = ServletsLibrary.createProductSet(projectId, computeRegion, PRODUCT_SET_ID_A, PRODUCT_SET_DISPLAY_NAME_A);  

//         Assert.assertEquals(expected, results);
//     }
// }