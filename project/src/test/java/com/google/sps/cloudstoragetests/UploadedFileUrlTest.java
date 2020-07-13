package com.google.sps;

import java.util.Date;
import com.google.appengine.api.blobstore.FileInfo;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.sps.data.CloudStorageLibrary;
import static org.mockito.Mockito.*;

@RunWith(JUnit4.class)
public final class UploadedFileUrlTest {
    private static BlobstoreService MOCK_BLOBSTORE = mock(BlobstoreService.class);
    private static BlobKey MOCK_BLOBKEY = mock(BlobKey.class);

    private final String GCS_FILE_NAME = "/gs/myBucket/myFile";
    private String BLOBKEY = "";

    @Before
    public void setUp() {
        when(MOCK_BLOBSTORE.createGsBlobKey(GCS_FILE_NAME)).thenReturn(MOCK_BLOBKEY);
        when(MOCK_BLOBKEY.getKeyString()).thenReturn("123456789");

        BLOBKEY = MOCK_BLOBSTORE.createGsBlobKey(GCS_FILE_NAME).getKeyString();
    }
    
    //Test to show what happens when you pass in a null value
    @Test
    public void uploadedNullGcsFileName() {
        String actual = CloudStorageLibrary.getUploadedFileUrl(MOCK_BLOBSTORE, null);
        String expected = "";

        Assert.assertEquals(expected, actual);
    }

    //Test to show what happens when you pass in an empty string
    @Test
    public void uploadedEmptyGcsFileName() {
        String actual = CloudStorageLibrary.getUploadedFileUrl(MOCK_BLOBSTORE, "");
        String expected = "";

        Assert.assertEquals(expected, actual);
    }

    //Test to show that the function works properly
    @Test
    public void uploadedGcsFileNameisSet() {
        String actual = CloudStorageLibrary.getUploadedFileUrl(MOCK_BLOBSTORE, GCS_FILE_NAME);
        String expected = "/getBlobstoreUrl?blobKey=" + BLOBKEY;

        Assert.assertEquals(expected, actual);
    }
}