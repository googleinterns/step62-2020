package com.google.sps.test;

import java.util.Date;
import com.google.appengine.api.blobstore.FileInfo;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import com.google.sps.data.CloudStorageLibrary;

@RunWith(JUnit4.class)
public class ServingFileUrlTest {
    private static final String GCS_FILE_NAME = "/gs/myBucket/myFile";
    
    @Test
    public void nullGcsFileName() {
        String actual = getServingFileUrl(null);
        String expected = "";

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void emptyGcsFileName() {
        String actual = getServingFileUrl("");
        String expected = "";

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void GcsFileNameisSet() {
        String actual = getServingFileUrl(GCS_FILE_NAME);
        String expected = "https://storage.googleapis.com/myBucket/myFile";

        Assert.assertEquals(expected, actual);
    }
}