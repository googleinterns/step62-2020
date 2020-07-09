package com.google.sps.test;

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
import com.google.sps.data.CloudStorageLibrary;

@RunWith(JUnit4.class)
public final class GcsFilePathTest {
    private static final Map<String, List<FileInfo>> NO_FILES = Collections.emptyMap();

    private static final Date date = new Date();
    private static final Long size = 2726297.6;
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
    }
    
    @Test
    public void EmptyMapForGcsFilePath() {
        String actual = getGcsFilePath(NO_FILES);
        String expected = "";

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void GcsFilePathIsSet() {
        String actual = getGcsFilePath(FILES);
        String expected = "/gs/myBucket/file1";

        Assert.assertEquals(expected, actual);
    }
}