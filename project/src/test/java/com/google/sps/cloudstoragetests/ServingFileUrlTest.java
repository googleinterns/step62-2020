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
import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import static org.mockito.Mockito.*;

@RunWith(JUnit4.class)
public final class ServingFileUrlTest {
    private static BlobstoreService MOCK_BLOBSTORE = mock(BlobstoreService.class);
    private static BlobKey MOCK_BLOBKEY = mock(BlobKey.class);
    private static BlobInfoFactory MOCK_BLOBINFO_FACTORY = mock(BlobInfoFactory.class);
    private static BlobInfo MOCK_BLOBINFO = mock(BlobInfo.class);

    private static BlobKey INVALID_MOCK_BLOBKEY = mock(BlobKey.class);
    private static BlobInfo INVALID_MOCK_BLOBINFO = mock(BlobInfo.class);

    private static final String GCS_FILE_NAME = "/gs/myBucket/myFile";

    private static final String INVALID_GCS_FILE_NAME = "/gs/notValid/aFile";

    @Before
    public void setUp() {
        when(MOCK_BLOBSTORE.createGsBlobKey(GCS_FILE_NAME)).thenReturn(MOCK_BLOBKEY);
        when(MOCK_BLOBINFO_FACTORY.loadBlobInfo(MOCK_BLOBKEY)).thenReturn(MOCK_BLOBINFO);

        when(MOCK_BLOBSTORE.createGsBlobKey(INVALID_GCS_FILE_NAME)).thenReturn(INVALID_MOCK_BLOBKEY);
        when(MOCK_BLOBINFO_FACTORY.loadBlobInfo(INVALID_MOCK_BLOBKEY)).thenReturn(null);
    }

    //Test to show what happens when you pass in a null blobstore
    @Test
    public void servingNullBlobstore() {
        String actual = CloudStorageLibrary.getServingFileUrl(null, MOCK_BLOBINFO_FACTORY, GCS_FILE_NAME);
        String expected = "";

        Assert.assertEquals(expected, actual);
    }

    //Test to show what happens when you pass in a null blobInfoFactory
    @Test
    public void servingNullBlobInfoFactory() {
        String actual = CloudStorageLibrary.getServingFileUrl(MOCK_BLOBSTORE, null, GCS_FILE_NAME);
        String expected = "";

        Assert.assertEquals(expected, actual);
    }

    //Test to show what happens when you pass in a null gcsuri
    @Test
    public void servingNullGcsFileName() {
        String actual = CloudStorageLibrary.getServingFileUrl(MOCK_BLOBSTORE, MOCK_BLOBINFO_FACTORY, null);
        String expected = "";

        Assert.assertEquals(expected, actual);
    }

    //Function that test what happens if you pass in a empty string
    @Test
    public void servingEmptyGcsFileName() {
        String actual = CloudStorageLibrary.getServingFileUrl(MOCK_BLOBSTORE, MOCK_BLOBINFO_FACTORY, "");
        String expected = "";

        Assert.assertEquals(expected, actual);
    }

    //Function to test what happens when you pass in a string that doesn't exist
    @Test
    public void servingNonExistentGcsFileName() {
        String actual = CloudStorageLibrary.getServingFileUrl(MOCK_BLOBSTORE, MOCK_BLOBINFO_FACTORY, INVALID_GCS_FILE_NAME);
        String expected = "";

        Assert.assertEquals(expected, actual);
    }

    //Test to make sure the function works properly if you pass in a file name
    @Test
    public void servingGcsFileNameisSet() {
        String actual = CloudStorageLibrary.getServingFileUrl(MOCK_BLOBSTORE, MOCK_BLOBINFO_FACTORY, GCS_FILE_NAME);
        String expected = "https://storage.googleapis.com/myBucket/myFile";

        Assert.assertEquals(expected, actual);
    }
} 