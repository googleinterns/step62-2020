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

    //Radius of Earth in miles
    private static int EARTH_RADIUS = 3956

    public static Double distance(GeoPt origin, GeoPt destination) {
        //Convert coordinates to radians instead of degrees
        oLat = new Float(origin.getLatitude()).doubleValue();
        oLng = new Float(origin.getLongitude()).doubleValue();
        dLat = new Float(destination.getLatitude()).doubleValue();
        dLng = new Float(destination.getLongitude()).doubleValue();

        // Haversine formula
        Double latDiff = dLat - oLat;
        Double lngDiff = dLng - oLng;
        Double ans = Math.pow(Math.sin(latDiff / 2), 2) 
                  + Math.cos(oLat) * Math.cos(dLat) 
                  * Math.pow(Math.sin(lngDiff / 2),2);
        
        ans = 2 * Math.asin(Math.sqrt(ans));

        return ans * EARTH_RADIUS;
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