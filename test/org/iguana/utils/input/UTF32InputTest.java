package org.iguana.utils.input;

import java.net.URI;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UTF32InputTest {

    // Test for character access and length
    @Test
    void testCharAtAndLength() {
        int[] chars = { 65, 66, 67, -1 };
        UTF32Input input = new UTF32Input(chars, chars.length, 1, URI.create("dummy:uri"));

        // Verify the length of the input
        assertEquals(4, input.length(), "Length should include EOF");

        // Verify the first character
        assertEquals(65, input.charAt(0), "First character should be A");

        // Verify the last character (EOF)
        assertEquals(-1, input.charAt(3), "Fourth character should be EOF");
    }

    // Test for string conversion, specifically handling characters outside the BMP
    @Test
    void testToString() {
        int[] chars = { 0x1F600, -1 };
        UTF32Input input = new UTF32Input(chars, chars.length, 1, URI.create("dummy:uri"));

        // Verify the conversion to string
        assertEquals("😀", input.toString(), "toString should convert UTF-32 code points to a Java String");
    }

    // Test for equality, hashCode, and non-equality
    @Test
    void testEqualsAndHashCode() {
        int[] chars1 = { 65, 66, 67, -1 };
        UTF32Input input1 = new UTF32Input(chars1, chars1.length, 1, URI.create("dummy:uri"));
        int[] chars2 = { 65, 66, 67, -1 };
        UTF32Input input2 = new UTF32Input(chars2, chars2.length, 1, URI.create("dummy:uri"));
        int[] chars3 = { 68, 69, 70, -1 };
        UTF32Input input3 = new UTF32Input(chars3, chars3.length, 1, URI.create("dummy:uri"));

        // Verify equality between input1 and input2
        assertEquals(input1, input2, "Same characters should be equal");

        // Verify the same hashCode for input1 and input2
        assertEquals(input1.hashCode(), input2.hashCode(), "hashCode should be the same for equal characters");

        // Verify non-equality between input1 and input3
        assertNotEquals(input1, input3, "Different characters should not be equal");
    }
}
