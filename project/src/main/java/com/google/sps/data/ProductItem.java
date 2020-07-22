package com.google.sps.data;



public class ProductItem{
    String productId;
    String productName;
    String productCategory;

    public ProductItem(String productId, String productName, String productCategory){
        this.productId = productId;
        this.productName = productName;
        this.productCategory = productCategory;
    }

    public String getProductId(){
        return productId;
    }

    public String getProductName(){
        return productName;
    }

    public String getProductCategory(){
        return productCategory;
    }
}