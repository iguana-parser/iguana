/*
 * Copyright (c) 2015, Ali Afroozeh and Anastasia Izmaylova, Centrum Wiskunde & Informatica (CWI)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this
 *    list of conditions and the following disclaimer in the documentation and/or
 *    other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 *
 */

package iguana.utils.input;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static java.lang.Character.isHighSurrogate;


/**
 * Is backed by an integer array to support UTF-32.
 *
 * @author Ali Afroozeh
 */
public interface Input {

    int EOF = -1;

    static Input fromString(String s) {
        return createInput(s, null);
    }

    static Input fromFile(File file) throws IOException {
        return fromFile(file, StandardCharsets.UTF_8);
    }

    static Input fromFile(File file, Charset charset) throws IOException {
        return createInput(new String(Files.readAllBytes(Paths.get(file.toURI())), charset), file.toURI());
    }

    static Input empty() {
        return fromString("");
    }

    static Input createInput(String s, URI uri) {
        boolean hasSurrogatePair = false;

        int lineCount = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (isHighSurrogate(c)) {
                hasSurrogatePair = true;
            }
            if (c == '\n') {
                lineCount++;
            }
        }

        if (!hasSurrogatePair) {
            return new DefaultInput(s, lineCount + 1, uri);
        }

        int[] characters = new int[s.length() + 1];

        int i = 0;
        int j = 0;
        while (i < s.length()) {
            char c = s.charAt(i);
            if (!Character.isHighSurrogate(c)) {
                characters[j++] = c;

                i++;
            } else {
                if (i < s.length() - 1) {
                    characters[j++] = Character.toCodePoint(c, s.charAt(i + 1));
                } else {
                    throw new RuntimeException("Invalid input");
                }
                i += 2;
            }
        }

        characters[j] = EOF;

        return new UTF32Input(characters, j + 1, lineCount + 1, uri);
    }

    List<Integer> nextSymbols(int index);

//    default List<Integer> nextSymbolsIgnoreLayout(int index) {
//        return nextSymbols(index);
//		while (true) {
//			List<Integer> c = getTagsAt(index++);
//			if (c == EOF) return EOF;
//			if (!isWhitespace(c))
//				return c;
//		}
//    }

    default int[] calculateLineLengths(int lineCount) {
        return new int[]{0};
//        int[] lineStarts = new int[lineCount];
//        lineStarts[0] = 0;
//        int j = 0;
//
//        for (int i = 0; i < length(); i++) {
//            if (getTagsAt(i) == '\n') {
//                if (i + 1 < length())
//                    lineStarts[++j] = i + 1;
//            }
//        }
//        return lineStarts;
    }

    /**
     * The length is one more than the actual characters in the input as the last input character is considered EOF.
     */
    int length();

    default boolean isEmpty() {
        return length() == 1;
    }

    int getLineNumber(int inputIndex);

    int getColumnNumber(int inputIndex);

    String subString(int start, int end);

    int getLineCount();

    URI getURI();

    boolean isStartOfLine(int inputIndex);

    boolean isEndOfLine(int inputIndex);

    boolean isEndOfFile(int inputIndex);

    List<Integer> getStartVertices();

    List<Integer> getFinalVertices();

    boolean isFinal(int index);
}
