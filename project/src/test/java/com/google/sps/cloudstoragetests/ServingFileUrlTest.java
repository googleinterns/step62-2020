package com.google.sps;

import java.util.Date;
import com.google.appengine.api.blobstore.FileInfo;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import com.google.sps.data.CloudStorageLibrary;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.storage.Blob;
import static org.mockito.Mockito.*;

@RunWith(JUnit4.class)
public final class ServingFileUrlTest {
    private static Storage MOCK_STORAGE = mock(Storage.class);
    private static Blob MOCK_BLOB = mock(Blob.class);

    private static final String GCSCURI = "/gs/cloudberry-step-2020-test-bucket/myFile";
    private static final String FILE_NAME = "myFile";
    private static final String BUCKET_NAME = "cloudberry-step-2020-test-bucket";

    private static final String INVALID_GCSURI = "/gs/cloudberry-step-2020-test-bucket/invalidFile";
    private static final String INVALID_FILE_NAME = "invalidFile";

    @Before
    public void setUp() {
        when(MOCK_STORAGE.get(BUCKET_NAME, FILE_NAME)).thenReturn(MOCK_BLOB);
        when(MOCK_STORAGE.get(BUCKET_NAME, INVALID_FILE_NAME)).thenReturn(null);
    }

    //Test to show what happens when you pass in a null storage
    @Test
    public void servingNullStorage() {
        String actual = CloudStorageLibrary.getServingFileUrl(null, GCSCURI);
        String expected = "";

        Assert.assertEquals(expected, actual);
    }

    //Test to show what happens when you pass in a null gcsuri
    @Test
    public void servingNullGcsFileName() {
        String actual = CloudStorageLibrary.getServingFileUrl(MOCK_STORAGE, null);
        String expected = "";

        Assert.assertEquals(expected, actual);
    }

    //Function that test what happens if you pass in a empty string
    @Test
    public void servingEmptyGcsFileName() {
        String actual = CloudStorageLibrary.getServingFileUrl(MOCK_STORAGE, "");
        String expected = "";

        Assert.assertEquals(expected, actual);
    }

    //Function to test what happens when you pass in a string that doesn't exist
    @Test
    public void servingNonExistentGcsFileName() {
        String actual = CloudStorageLibrary.getServingFileUrl(MOCK_STORAGE, INVALID_GCSURI);
        String expected = "";

        Assert.assertEquals(expected, actual);
    }

    //Test to make sure the function works properly if you pass in a file name
    @Test
    public void servingGcsFileNameisSet() {
        String actual = CloudStorageLibrary.getServingFileUrl(MOCK_STORAGE, GCSCURI);
        String expected = "https://storage.googleapis.com/cloudberry-step-2020-test-bucket/myFile";

        Assert.assertEquals(expected, actual);
    }
} 