package com.google.sps.data;

import java.util.stream.Collectors;  
import com.google.appengine.api.datastore.GeoPt;
import com.google.sps.data.ProductWithAddress;

import java.util.*; 
import java.lang.*; 

public class MapsLibrary {
    private static Double oLat;
    private static Double oLng;
    private static Double dLat;
    private static Double dLng;

    public static Double distance(GeoPt origin, GeoPt destination) {
        //Convert to radians
        oLat = Math.toRadians(Double.parseDouble(new Float(origin.getLatitude()).toString()));
        oLng = Math.toRadians(Double.parseDouble(new Float(origin.getLongitude()).toString()));
        dLat = Math.toRadians(Double.parseDouble(new Float(destination.getLatitude()).toString()));
        dLng = Math.toRadians(Double.parseDouble(new Float(destination.getLongitude()).toString()));

        // Haversine formula
        Double latDiff = dLat - oLat;
        Double lngDiff = dLng - oLng;
        Double ans = Math.pow(Math.sin(latDiff / 2), 2) 
                  + Math.cos(oLat) * Math.cos(dLat) 
                  * Math.pow(Math.sin(lngDiff / 2),2);
        
        ans = 2 * Math.asin(Math.sqrt(ans));

        //Radius of Earth in miles
        int radius = 3956;

        ans = ans * radius;

        return ans;
    }
    
    public static List<ProductEntity> sortByLocation(List<ProductWithAddress> productList,
      Account account) {
          productList.sort((product1, product2) -> {
              Double dist1 = distance(account.getLatLng(), product1.getAddress().getLatLng());
              Double dist2 = distance(account.getLatLng(), product2.getAddress().getLatLng());

              if (dist1 < dist2) {
                  return -1;
              }
              
              if (dist1 > dist2) {
                  return 1;
              }

              return 0;
          });

          return productList.stream().map(product -> product.getProduct()).
            collect(Collectors.toList());
    }
}