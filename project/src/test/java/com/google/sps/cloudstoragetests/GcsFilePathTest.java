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
public final class GcsFilePathTest {
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
    private static final FileInfo file_4 = new FileInfo("contentTypeD", date, "file4", size,
                                                        "abcg1237", "/gs/myBucket/file4");
    private static final FileInfo file_5 = new FileInfo("contentTypeE", date, "file5", size,
                                                        "abch1238", "/gs/myBucket/file5");
    private static final FileInfo file_6 = new FileInfo("contentTypeF", date, "file6", size,
                                                        "abci1239", "/gs/myBucket/file6");

    private static final List<FileInfo> FILE_LIST1 = new ArrayList<FileInfo>();
    private static final List<FileInfo> FILE_LIST2 = new ArrayList<FileInfo>();

    private static final Map<String, List<FileInfo>> FILES = new HashMap<>();

    //Adding the sample files to Lists and then the Lists to the Map
    @Before
    public void setUp() {
        FILE_LIST1.add(file_1);
        FILE_LIST1.add(file_2);
        FILE_LIST1.add(file_3);

        FILE_LIST2.add(file_4);
        FILE_LIST2.add(file_5);
        FILE_LIST2.add(file_6);

        FILES.put("List1", FILE_LIST1);
        FILES.put("List2", FILE_LIST2);

        when(MOCK_BLOBSTORE.getFileInfos(MOCK_REQUEST)).thenReturn(FILES);
        when(MOCK_BLOBSTORE.getFileInfos(MOCK_REQUEST_NO_FILES)).thenReturn(NO_FILES);
    }

    //Test what happens when you pass in a null request
    @Test
    public void getGcsFilePathNullRequest() {
        String actual = CloudStorageLibrary.getGcsFilePath(null, MOCK_BLOBSTORE);
        String expected = "";

        Assert.assertEquals(expected, actual);
    }

    //Test what happens when you pass in a null blobstore
    @Test
    public void getGcsFilePathNullBlobstore() {
        String actual = CloudStorageLibrary.getGcsFilePath(MOCK_REQUEST, null);
        String expected = "";

        Assert.assertEquals(expected, actual);
    }
    
    //Test what happens when you pass in an empty Map
    @Test
    public void emptyMapForGetGcsFilePath() {
        String actual = CloudStorageLibrary.getGcsFilePath(MOCK_REQUEST_NO_FILES, MOCK_BLOBSTORE);
        String expected = "";

        Assert.assertEquals(expected, actual);
    }

    //Test what happens when you pass in a map that contains files
    @Test
    public void getGcsFilePathIsSet() {
        String actual = CloudStorageLibrary.getGcsFilePath(MOCK_REQUEST, MOCK_BLOBSTORE);
        String expected = "/gs/myBucket/file1";

        Assert.assertEquals(expected, actual);
    }
}