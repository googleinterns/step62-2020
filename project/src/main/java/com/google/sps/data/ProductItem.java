package com.google.sps.data;



public class ProductItem{
    String productId;
    String productName;
    String productCategory;
    String productSetId;


    public ProductItem(String productId, String ProductName, String ProductCategory, String productSetId){
        this.productId = productId;
        this.productName = productName;
        this.productCategory = productCategory;
        this.productSetId = productSetId;
    }
}