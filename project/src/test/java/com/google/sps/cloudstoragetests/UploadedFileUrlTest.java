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
    private static BlobInfoFactory MOCK_BLOBINFO_FACTORY = mock(BlobInfoFactory.class);
    private static BlobInfo MOCK_BLOBINFO = mock(BlobInfo.class);

    private static BlobKey INVALID_MOCK_BLOBKEY = mock(BlobKey.class);
    private static BlobInfo INVALID_MOCK_BLOBINFO = mock(BlobInfo.class);

    private final String GCS_FILE_NAME = "/gs/myBucket/myFile";

    private static final String INVALID_GCS_FILE_NAME = "/gs/notValid/aFile";

    private String BLOBKEY = "";

    @Before
    public void setUp() {
        when(MOCK_BLOBSTORE.createGsBlobKey(GCS_FILE_NAME)).thenReturn(MOCK_BLOBKEY);
        when(MOCK_BLOBKEY.getKeyString()).thenReturn("123456789");
        when(MOCK_BLOBINFO_FACTORY.loadBlobInfo(MOCK_BLOBKEY)).thenReturn(MOCK_BLOBINFO);

        when(MOCK_BLOBSTORE.createGsBlobKey(INVALID_GCS_FILE_NAME)).thenReturn(INVALID_MOCK_BLOBKEY);
        when(MOCK_BLOBINFO_FACTORY.loadBlobInfo(INVALID_MOCK_BLOBKEY)).thenReturn(null);

        BLOBKEY = MOCK_BLOBSTORE.createGsBlobKey(GCS_FILE_NAME).getKeyString();
    }
    
    //Test to show what happens when you pass in a null blobstore
    @Test
    public void uploadedNullBlobstore() {
        String actual = CloudStorageLibrary.getUploadedFileUrl(null, MOCK_BLOBINFO_FACTORY, GCS_FILE_NAME);
        String expected = "";

        Assert.assertEquals(expected, actual);
    }

    //Test to show what happens when you pass in a null blobInfoFactory
    @Test
    public void uploadedNullBlobInfoFactory() {
        String actual = CloudStorageLibrary.getUploadedFileUrl(MOCK_BLOBSTORE, null, GCS_FILE_NAME);
        String expected = "";

        Assert.assertEquals(expected, actual);
    }
    
    //Test to show what happens when you pass in a null gcsuri
    @Test
    public void uploadedNullGcsFileName() {
        String actual = CloudStorageLibrary.getUploadedFileUrl(MOCK_BLOBSTORE, MOCK_BLOBINFO_FACTORY, null);
        String expected = "";

        Assert.assertEquals(expected, actual);
    }

    //Test to show what happens when you pass in an empty string
    @Test
    public void uploadedEmptyGcsFileName() {
        String actual = CloudStorageLibrary.getUploadedFileUrl(MOCK_BLOBSTORE, MOCK_BLOBINFO_FACTORY, "");
        String expected = "";

        Assert.assertEquals(expected, actual);
    }

    //Function to test what happens when you pass in a string that doesn't exist
    @Test
    public void uploadingNonExistentGcsFileName() {
        String actual = CloudStorageLibrary.getUploadedFileUrl(MOCK_BLOBSTORE, MOCK_BLOBINFO_FACTORY, INVALID_GCS_FILE_NAME);
        String expected = "";

        Assert.assertEquals(expected, actual);
    }

    //Test to show that the function works properly
    @Test
    public void uploadedGcsFileNameisSet() {
        String actual = CloudStorageLibrary.getUploadedFileUrl(MOCK_BLOBSTORE, MOCK_BLOBINFO_FACTORY, GCS_FILE_NAME);
        String expected = "/serveBlobstoreImage?blobKey=" + BLOBKEY;

        Assert.assertEquals(expected, actual);
    }
}