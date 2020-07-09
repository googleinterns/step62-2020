package com.google.sps.test;

import java.util.Date;
import com.google.appengine.api.blobstore.FileInfo;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.sps.data.CloudStorageLibrary;

@RunWith(JUnit4.class)
public class UploadedFileUrlTest {
    private static final BlobstoreService BLOBSTORE = BlobstoreServiceFactory.getBlobstoreService();
    private static final String GCS_FILE_NAME = "/gs/myBucket/myFile";
    private static final String BLOBKEY = BLOBSTORE.createGsBlobKey(GCS_FILE_NAME).getKeyString();

    @Test
    public void nullGcsFileName() {
        String actual = getUploadedFileUrl(BLOBSTORE, null);
        String expected = "";

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void emptyGcsFileName() {
        String actual = getUploadedFileUrl(BLOBSTORE, "");
        String expected = "";

        Assert.assertEquals(expected, actual);
    }


    @Test
    public void GcsFileNameisSet() {
        String actual = getServingFileUrl(BLOBSTORE, GCS_FILE_NAME);
        String expected = "/getBlobstoreUrl?blobKey=" + BLOBKEY;

        Assert.assertEquals(expected, actual);
    }
}