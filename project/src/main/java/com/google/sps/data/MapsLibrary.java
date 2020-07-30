package com.google.sps.data;

import java.util.stream.Collectors;  
import com.google.appengine.api.datastore.GeoPt;
import com.google.sps.data.ProductWithAddress;

import java.util.*; 
import java.lang.*; 

public class MapsLibrary {
    private float oLat;
    private float oLng;
    private float dLat;
    private float dLng;

    public static float distance(GeoPt origin, GeoPt destination) {
        //Convert lat and long to radians
        oLat = Math.toRadians(origin.getLatitude());
        oLng = Math.toRadians(origin.getLongitude());
        dLat = Math.toRadians(destination.getLatitude());
        dLng = Math.toRadians(destination.getLongitude());

        // Haversine formula
        float latDiff = dLat - oLat;
        float lngDiff = dLng - dLng;
        float ans = Math.pow(Math.sin(dlat / 2), 2) 
                  + Math.cos(lat1) * Math.cos(lat2) 
                  * Math.pow(Math.sin(dlon / 2),2);
        
        ans = 2 * Math.asin(Math.sqrt(ans));

        //Radius of Earth in miles
        float radius = 3956;

        ans = ans * radius;

        return ans;
    }
    
    public static List<ProductEntity> sortByLocation(List<ProductWithAddress> productList,
      Account account) {
          List<ProductWithAddress> sortedProducts =  productList.sort((product1, product2) -> {
              float dist1 = distance(account.getLatLng(), product1.getAddress().getLatLng);
              float dist2 = distance(account.getLatLng(), product2.getAddress().getLatLng);

              if (dist1 < dist2) {
                  return -1;
              }
              
              if (dist1 > dist2) {
                  return 1;
              }

              return 0;
          });

          return sortedProducts.stream().map(product -> product.getProduct()).
            collect(Collectors.toList());
    }
}