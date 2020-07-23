package com.google.sps;

import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import com.google.sps.data.TextSearchLibrary;
import com.google.sps.data.TextSearchLibrary.Match;
import com.google.sps.data.ProductLabel;
import static org.mockito.Mockito.*;

@RunWith(JUnit4.class)
public final class TextSearchTest {
    

    // Setup
    @Before
    public void setUp() {
       return;
    }

    // Keyword is exactly equal to label
    @Test
    public void bothAreEqual() {
      Match actual = TextSearchLibrary.compareLabels("hello", "hello");
      Match expected = Match.EXACT;

      Assert.assertEquals(expected, actual);
    }

    // Keyword and label are both empty strings.
    @Test
    public void bothAreEqualAndEmpty() {
      Match actual = TextSearchLibrary.compareLabels("", "");
      Match expected = Match.EXACT;

      Assert.assertEquals(expected, actual);
    }

    // Keyword is a substring of the label.
    @Test
    public void labelContainsKeyword() {
      Match actual = TextSearchLibrary.compareLabels("hello", "hellothere!");
      Match expected = Match.PARTIAL;

      Assert.assertEquals(expected, actual);
    }

    // Keyword is a equals a word in a label.
    @Test
    public void labelPartEqualsKeyword() {
      Match actual = TextSearchLibrary.compareLabels("hello", "hello there!");
      Match expected = Match.CLOSE;

      Assert.assertEquals(expected, actual);
    }

    // Keyword is a substring of a word in a label.
    @Test
    public void labelPartContainsKeyword() {
      Match actual = TextSearchLibrary.compareLabels("hello", "hello, there!");
      Match expected = Match.PARTIAL;

      Assert.assertEquals(expected, actual);
    }

    // A word in the key string equals the label.
    @Test
    public void keywordPartEqualsLabel() {
      Match actual = TextSearchLibrary.compareLabels("hello there!", "hello");
      Match expected = Match.CLOSE;

      Assert.assertEquals(expected, actual);
    }

    // A word in the key string is a substring of the label.
    @Test
    public void keywordPartContainsLabel() {
      Match actual = TextSearchLibrary.compareLabels("hello there!", "hello!");
      Match expected = Match.PARTIAL;

      Assert.assertEquals(expected, actual);
    }
    
    // No match
    @Test
    public void noMatch() {
      Match actual = TextSearchLibrary.compareLabels("hello there!", "fds");
      Match expected = Match.NONE;

      Assert.assertEquals(expected, actual);
    }

    // No match, even if label is a substring of the search
    @Test
    public void noMatchLabelIsSubstring() {
      Match actual = TextSearchLibrary.compareLabels("hellothere!", "hello");
      Match expected = Match.NONE;

      Assert.assertEquals(expected, actual);
    }

    // None of the words match
    @Test
    public void noMatchManyWords() {
      Match actual = TextSearchLibrary.compareLabels("hello there!", "my name");
      Match expected = Match.NONE;

      Assert.assertEquals(expected, actual);
    }

    // Some of the words match.
    @Test
    public void wordsAreSimilar() {
      Match actual = TextSearchLibrary.compareLabels("hello there", "hello man");
      Match expected = Match.CLOSE;

      Assert.assertEquals(expected, actual);
    }
}