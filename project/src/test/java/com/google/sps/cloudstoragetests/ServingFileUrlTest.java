package com.google.sps;

import java.util.Date;
import com.google.appengine.api.blobstore.FileInfo;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import com.google.sps.data.CloudStorageLibrary;

@RunWith(JUnit4.class)
public final class ServingFileUrlTest {
    private static final String GCS_FILE_NAME = "/gs/myBucket/myFile";
    
    //Test to show what happens when you pass in a null value
    @Test
    public void servingNullGcsFileName() {
        String actual = CloudStorageLibrary.getServingFileUrl(null);
        String expected = "";

        Assert.assertEquals(expected, actual);
    }

    //Function that test what happens if you pass in a empty string
    @Test
    public void servingEmptyGcsFileName() {
        String actual = CloudStorageLibrary.getServingFileUrl("");
        String expected = "";

        Assert.assertEquals(expected, actual);
    }

    //Test to make sure the function works properly if you pass in a file name
    @Test
    public void servingGcsFileNameisSet() {
        String actual = CloudStorageLibrary.getServingFileUrl(GCS_FILE_NAME);
        String expected = "https://storage.googleapis.com/myBucket/myFile";

        Assert.assertEquals(expected, actual);
    }
}