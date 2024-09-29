package org.cis1200;

import org.junit.jupiter.api.Test;
import java.io.StringReader;
import java.io.BufferedReader;
import java.util.NoSuchElementException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for LineIterator */
public class LineIteratorTest {

    /*
     * Here's a test to help you out, but you still need to write your own.
     */

    @Test
    public void testHasNextAndNext() {

        // Note we don't need to create a new file here in order to test out our
        // LineIterator if we do not want to. We can just create a
        // StringReader to make testing easy!
        String words = "0, The end should come here.\n"
                + "1, This comes from data with no duplicate words!";
        StringReader sr = new StringReader(words);
        BufferedReader br = new BufferedReader(sr);
        LineIterator li = new LineIterator(br);
        assertTrue(li.hasNext());
        assertEquals("0, The end should come here.", li.next());
        assertTrue(li.hasNext());
        assertEquals("1, This comes from data with no duplicate words!", li.next());
        assertFalse(li.hasNext());
    }

    /* **** ****** **** WRITE YOUR TESTS BELOW THIS LINE **** ****** **** */

    @Test
    public void testEmptyFile() {
        StringReader sr = new StringReader("");
        BufferedReader br = new BufferedReader(sr);
        LineIterator li = new LineIterator(br);
        assertFalse(li.hasNext());
        assertThrows(NoSuchElementException.class, li::next);
    }

    @Test
    public void testReadingPastEndOfFile() {
        String words = "Only one line here";
        StringReader sr = new StringReader(words);
        BufferedReader br = new BufferedReader(sr);
        LineIterator li = new LineIterator(br);
        assertTrue(li.hasNext());
        assertEquals("Only one line here", li.next());
        assertFalse(li.hasNext());
        assertThrows(NoSuchElementException.class, li::next);
    }

    @Test
    public void testHandlingIOException() {
        BufferedReader br = new BufferedReader(new StringReader("First line\nSecond line")) {
            @Override
            public String readLine() throws IOException {
                throw new IOException("Forced IOException for testing purposes.");
            }
        };
        LineIterator li = new LineIterator(br);
        assertFalse(li.hasNext());
        assertThrows(NoSuchElementException.class, li::next);
    }

    @Test
    public void testMultipleLines() {
        String lines = "First line\nSecond line\nThird line";
        BufferedReader br = new BufferedReader(new StringReader(lines));
        LineIterator li = new LineIterator(br);

        assertTrue(li.hasNext());
        assertEquals("First line", li.next());
        assertTrue(li.hasNext());
        assertEquals("Second line", li.next());
        assertTrue(li.hasNext());
        assertEquals("Third line", li.next());
        assertFalse(li.hasNext());
    }

    @Test
    public void testReaderClosureAtEnd() {
        String lines = "First line\nSecond line";
        BufferedReader br = new BufferedReader(new StringReader(lines));
        LineIterator li = new LineIterator(br);

        while (li.hasNext()) {
            li.next();
        }

        Exception exception = null;
        try {
            br.readLine();
        } catch (IOException e) {
            exception = e;
        }

        assertNotNull(
                exception,
                "BufferedReader should be closed and " +
                        "throw IOException when readLine is called."
        );
    }

}