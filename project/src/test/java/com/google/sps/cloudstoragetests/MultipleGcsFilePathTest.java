package com.google.sps;

import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.ArrayList;
import java.util.HashMap;
import com.google.appengine.api.blobstore.FileInfo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import javax.servlet.http.HttpServletRequest;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.sps.data.CloudStorageLibrary;
import static org.mockito.Mockito.*;

@RunWith(JUnit4.class)
public final class MultipleGcsFilePathTest {
    private static BlobstoreService MOCK_BLOBSTORE = mock(BlobstoreService.class);
    private static HttpServletRequest MOCK_REQUEST = mock(HttpServletRequest.class);
    
    private static HttpServletRequest MOCK_REQUEST_NO_FILES = mock(HttpServletRequest.class);

    //Empty Map of files
    private static final Map<String, List<FileInfo>> NO_FILES = Collections.emptyMap();

    //Defining sample files for testing
    private static final Date date = new Date();
    private static final Long size = 27262L;
    private static final FileInfo file_1 = new FileInfo("contentTypeA", date, "file1", size,
                                                        "abcd1234", "/gs/myBucket/file1");
    private static final FileInfo file_2 = new FileInfo("contentTypeB", date, "file2", size,
                                                        "abce1235", "/gs/myBucket/file2");
    private static final FileInfo file_3 = new FileInfo("contentTypeC", date, "file3", size,
                                                        "abcf1236", "/gs/myBucket/file3");

    private static final List<FileInfo> FILE_LIST = new ArrayList<FileInfo>();

    private static final Map<String, List<FileInfo>> FILES = new HashMap<>();

    private static final ArrayList<String> GCSURIS = new ArrayList<String>();

    //Adding the sample files to Lists and then the Lists to the Map
    @Before
    public void setUp() {
        FILE_LIST.add(file_1);
        FILE_LIST.add(file_2);
        FILE_LIST.add(file_3);

        FILES.put("List", FILE_LIST);

        GCSURIS.add("/gs/myBucket/file1");
        GCSURIS.add("/gs/myBucket/file2");
        GCSURIS.add("/gs/myBucket/file3");

        when(MOCK_BLOBSTORE.getFileInfos(MOCK_REQUEST)).thenReturn(FILES);
        when(MOCK_BLOBSTORE.getFileInfos(MOCK_REQUEST_NO_FILES)).thenReturn(NO_FILES);
    }

    //Test what happens when you pass in a null request
    @Test
    public void multGetGcsFilePathNullRequest() {
        ArrayList<String> actual = CloudStorageLibrary.getMultipleGcsFilePath(null, MOCK_BLOBSTORE);
        ArrayList<String> expected = null;

        Assert.assertEquals(expected, actual);
    }

    //Test what happens when you pass in a null blobstore
    @Test
    public void multGetGcsFilePathNullBlobstore() {
        ArrayList<String> actual = CloudStorageLibrary.getMultipleGcsFilePath(MOCK_REQUEST, null);
        ArrayList<String> expected = null;

        Assert.assertEquals(expected, actual);
    }
    
    //Test what happens when you pass in an empty Map
    @Test
    public void multEmptyMapForGetGcsFilePath() {
        ArrayList<String> actual = CloudStorageLibrary.getMultipleGcsFilePath(MOCK_REQUEST_NO_FILES, MOCK_BLOBSTORE);
        ArrayList<String> expected = null;

        Assert.assertEquals(expected, actual);
    }

    //Test what happens when you pass in a map that contains files
    @Test
    public void multGetGcsFilePathIsSet() {
        ArrayList<String> actual = CloudStorageLibrary.getMultipleGcsFilePath(MOCK_REQUEST, MOCK_BLOBSTORE);
        ArrayList<String> expected = GCSURIS;

        Assert.assertEquals(expected, actual);
    }
}