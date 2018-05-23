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

import iguana.utils.collections.tuple.IntTuple;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * 
 * Is backed by an integer array to support UTF-32. 
 * 
 * @author Ali Afroozeh
 *
 */
public class Input {

    private static final int EOF = -1;

	private final int[] characters;

	private int[] lineStarts;

	private final int lineCount;

	private final int length;
	
	private final URI uri;

	private final int hash;

	public static Input fromString(String s) {
		return new Input(s, null);
	}

	public static Input fromPath(String path) throws IOException {
		return fromPath(path, StandardCharsets.UTF_8);
	}

	public static Input fromPath(String path, Charset charset) throws IOException {
		return new Input(new String(Files.readAllBytes(Paths.get(path)), charset), URI.create(path));
	}

	public static Input fromFile(File file) throws IOException {
		return fromFile(file, StandardCharsets.UTF_8);
	}

	public static Input fromFile(File file, Charset charset) throws IOException {
		return new Input(new String(Files.readAllBytes(Paths.get(file.toURI())), charset), file.toURI());
	}

	public static Input empty() {
		return fromString("");
	}

	private Input(String s, URI uri) {
		this.uri = uri;

		this.characters = new int[s.length() + 1];

		IntTuple result = convertStringToIntArray(s, characters);

		this.length = result.first();
		this.lineCount = result.second();

		this.hash = Arrays.hashCode(characters);
	}

	private static IntTuple convertStringToIntArray(String s, int[] characters) {
		int lineCount = 0;

		int i = 0;
		int j = 0;
		while (i < s.length()) {
			char c = s.charAt(i);
			if (!Character.isHighSurrogate(c)) {
				characters[j++] = c;
				if (c == '\n') {
					lineCount++;
				}
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

		characters[j++] = EOF;

		return IntTuple.of(j, lineCount + 1);
	}

	private static int[] calculateLineLengths(int lineCount, int[] characters) {
		int[] lineStarts = new int[lineCount];
		lineStarts[0] = 0;
		int j = 0;

		for (int i = 0; i < characters.length; i++) {
			if (characters[i] == '\n') {
				if (i + 1 < characters.length)
					lineStarts[++j] = i + 1;
			}
		}
		return lineStarts;
	}
	
	public int charAt(int index) {
		return characters[index];
	}

	public int charAtIgnoreLayout(int index) {
		while (true) {
			int c = characters[index++];
			if (c == EOF) return EOF;
			if (!Character.isWhitespace(c))
				return c;
		}
	}

    /**
     * The length is one more than the actual characters in the input as the last input character is considered EOF.
     */
	public int length() {
		return length;
	}
	
	public boolean match(int start, int end, String target) {
		return match(start, end, toIntArray(target));
	}
	
	public boolean match(int start, int end, int[] target) {
		if(target.length != end - start) {
			return false;
		}
	 	
		int i = 0;
		while(i < target.length) {
			if(target[i] != characters[start + i]) {
				return false;
			}
			i++;
		}
		
		return true;
	}

	public boolean match(int from, String target) {
		return match(from, toIntArray(target));
	}
	
	public boolean matchBackward(int start, String target) {
		return matchBackward(start, toIntArray(target));
	}
	
	public boolean matchBackward(int start, int[] target) {
		if(start - target.length < 0) {
			return false;
		}
		
		int i = target.length - 1;
		int j = start - 1;
		while(i >= 0) {
			if(target[i] != characters[j]) {
				return false;
			}
			i--;
			j--;
		}
		
		return true;
	}
	
	public boolean match(int from, int[] target) {
		if (target.length > length() - from) {
			return false;
		}
		
		int i = 0;
		while (i < target.length) {
			if(target[i] != characters[from + i]) {
				return false;
			}
			i++;
		}
		
		return true;
	}

	public int getLineNumber(int inputIndex) {
        checkBounds(inputIndex, length());
		checkLineStartsInitialized();

        int lineStart = Arrays.binarySearch(lineStarts, inputIndex);
        if (lineStart >= 0) return lineStart + 1;
        return -lineStart - 1;
	}

	public int getColumnNumber(int inputIndex) {
        checkBounds(inputIndex, length());
		checkLineStartsInitialized();

        int lineStart = Arrays.binarySearch(lineStarts, inputIndex);
        if (lineStart >= 0) return 1;
        return inputIndex - lineStarts[-lineStart - 1 - 1] + 1;
	}

	private void checkLineStartsInitialized() {
		if (lineStarts == null) {
			lineStarts = calculateLineLengths(getLineCount(), characters);
		}
	}

	@Override
    public int hashCode() {
        return hash;
    }

    @Override
	public boolean equals(Object obj) {
		if (this == obj) return true;

		if (!(obj instanceof Input)) return false;

		Input other = (Input) obj;

		return this.length() == other.length() &&
               this.hash == other.hash &&
               Arrays.equals(characters, other.characters);
	}

	/**
	 * Returns a string representation of this input instance from the
	 * given start (including) and end (excluding) indices.
	 *  
	 */
	public String subString(int start, int end) {
		List<Character> charList = new ArrayList<>();
		
		for(int i = start; i < end; i++) {
			if (characters[i] == -1) continue;
			char[] chars = Character.toChars(characters[i]);
			for(char c : chars) {
				charList.add(c);
			}			
		}
		
		StringBuilder sb = new StringBuilder();
		for(char c : charList) {
			sb.append(c);
		}
		
		return sb.toString();
	}

	@Override
	public String toString() {
		return subString(0, characters.length - 1);
	}
	
	public int getLineCount() {
		return this.lineCount;
	}
	
	public boolean isEmpty() {
		return length() == 1;
	}
	
	public URI getURI() {
		return uri;
	}

    public boolean isStartOfLine(int inputIndex) {
        checkBounds(inputIndex, length());
        checkLineStartsInitialized();

        return Arrays.binarySearch(lineStarts, inputIndex) >= 0;
    }

	public boolean isEndOfLine(int inputIndex) {
        checkBounds(inputIndex, length());

        return charAt(inputIndex) == '\n' || charAt(inputIndex) == EOF;
	}
	
	public boolean isEndOfFile(int inputIndex) {
        checkBounds(inputIndex, length());

		return characters[inputIndex] == -1;
	}

    private static void checkBounds(int index, int length) {
        if (index < 0 || index >= length) {
            throw new IndexOutOfBoundsException("index must be greater than or equal to 0 and smaller than the input length (" + length + ")");
        }
    }

    private static int[] toIntArray(String s) {
        int[] array = new int[s.codePointCount(0, s.length())];
        for(int i = 0; i < array.length; i++) {
            array[i] = s.codePointAt(i);
        }
        return array;
    }

}
