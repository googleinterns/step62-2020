package com.google.sps.data;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.blobstore.BlobKey;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsInputChannel;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;
import com.google.appengine.api.blobstore.UploadOptions;
import com.google.appengine.api.blobstore.FileInfo;

import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageClass;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.storage.Blob;

public class CloudStorageLibrary {
    private static final String BUCKET_NAME = "cloudberry-step-2020-test-bucket";
    // private static final String BUCKET_NAME = "neelgandhi-step-2020-test-bucket";

    //Function to determine if the gcs file path is valid
    public static Boolean doesGcsuriExist(Storage storage, String gcsFilePath) {
        String fileName = gcsFilePath.replaceFirst(("/gs/" + BUCKET_NAME + "/"), "");
        Blob blob = storage.get(BUCKET_NAME, fileName);

        return (blob != null);
    }

    /*Function to get the gcsuri from an uploaded file.
      getFileInfos() returns a map of the files that have been uploaded.*/
    public static String getGcsFilePath(HttpServletRequest request, BlobstoreService blobstore) {
        if (request == null || blobstore == null) {
            return "";
        }

        Map<String, List<FileInfo>> files = blobstore.getFileInfos(request);

        //We only need the first element of the map because we upload one image at a time
        //TODO(mrjwash): When we switch to multiple I have to parse for the newest upload using getCreation();
        for (Map.Entry<String, List<FileInfo>> fileMap : files.entrySet()) { 
            try {
                //getGsObjectName() actually returns the gcsuri for a file
                return fileMap.getValue().get(0).getGsObjectName();
            } catch (Exception e) {
                System.out.println(e);
                return "";
            }
        }
        return "";
    }

    //Gets a url to directly serve the image
    public static String getServingFileUrl(Storage storage, String gcsFilePath) {
        if (storage == null || gcsFilePath == null) {
            return "";
        } else if (gcsFilePath.isEmpty()) {
            return "";
        } else {
            if (!(doesGcsuriExist(storage, gcsFilePath))) {
                return "";
            }

            String tempFilePath = gcsFilePath.replaceFirst("/gs", "");

            return "https://storage.googleapis.com" + tempFilePath;
        }
    }

    //Function that gets the upload Url for a given gcsuri
    public static String getUploadedFileUrl(BlobstoreService blobstore, Storage storage, String gcsFilePath) {
        if (blobstore == null || storage == null || gcsFilePath == null) {
            return "";
        } else if (gcsFilePath.isEmpty()) {
            return "";
        } else {
            if (!(doesGcsuriExist(storage, gcsFilePath))) {
                return "";
            }
            
            BlobKey blobKey = blobstore.createGsBlobKey(gcsFilePath);
        
            return "/serveBlobstoreImage?blobKey=" + blobKey.getKeyString();
        }
    }

    //Creates a bucket with the given project and bucket name
    public static void CreateBucket(String projectId, String bucketName) {
        Storage storage = StorageOptions.newBuilder().setProjectId(projectId)
                          .build().getService();
                          
        StorageClass storageClass = StorageClass.STANDARD;

        String location = "us-east1";

        Bucket bucket = storage.create(BucketInfo.newBuilder(bucketName)
                        .setStorageClass(storageClass).setLocation(location)
                        .build());

        System.out.println("Created bucket " + bucket.getName()
                           + " in " + bucket.getLocation()
                           + " with storage class "
                           + bucket.getStorageClass());
    }

}