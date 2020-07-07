package com.google.sps.data;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
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

public class CloudStorageLibrary {

    //Generates GCS file path. Still working on this. Explain logic and explain what the file path looks like
    public static String getFileName(HttpServletRequest request, BlobstoreService blobstore) {
        Map<String, List<FileInfo>> files = blobstore.getFileInfos(request);
        Set< Map.Entry<String, List<FileInfo>> > fileSet = files.entrySet();

        for (Map.Entry<String, List<FileInfo>> fileMap : fileSet) { 
            try {
                return fileMap.getValue().get(0).getGsObjectName();
            } catch (Exception e) {
                return "";
            }
        }
        return "";
    }

    //Check file name here
    public static String getUploadedFileUrl(HttpServletRequest request, BlobstoreService blobstore) {
        BlobKey blobKey = blobstore.createGsBlobKey(getFileName(request, blobstore));
        
        return "/image-serve?blobKey=" + blobKey.getKeyString();
    }

    //Creates a bucket with the given project and bucket name
    public static void CreateBucket(String projectId, String bucketName) {
        Storage storage = StorageOptions.newBuilder().setProjectId(projectId).
                          build().getService();
                          
        StorageClass storageClass = StorageClass.STANDARD;

        String location = "us-east1";

        Bucket bucket = storage.create(BucketInfo.newBuilder(bucketName).
                        setStorageClass(storageClass).setLocation(location).
                        build());

        System.out.println("Created bucket " + bucket.getName()
                           + " in " + bucket.getLocation()
                           + " with storage class "
                           + bucket.getStorageClass());
    }
}