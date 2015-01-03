package org.jgll.util;

import java.io.BufferedReader;
import java.io.CharArrayReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.io.input.ReaderInputStream;
import org.jgll.grammar.symbol.EOF;
import org.jgll.traversal.PositionInfo;

/**
 * 
 * Is backed by an integer array to support UTF-32. 
 * 
 * @author Ali Afroozeh
 *
 */
public class Input {

	private int[] characters;
	
	/**
	 * This array keeps the line and column information associated with each input index.
	 */
	private LineColumn[] lineColumns;
	
	/**
	 * Number of lines in the input.
	 */
	private int lineCount;
	
	private URI uri;
	
	public static Input fromString(String s, URI uri) {
		try {
			return new Input(fromStream(IOUtils.toInputStream(s, "UTF-8")), uri);
		} catch (IOException e) {
			assert false : "this should not happen from a string";
			e.printStackTrace();
		}
		throw new RuntimeException();
	}
	
	public static Input fromCharArray(char[] s, URI uri) {
		try {
			return new Input(fromStream(new ReaderInputStream(new CharArrayReader(s))), uri);
		} catch (IOException e) {
		    e.printStackTrace();
		}
		throw new RuntimeException("unexpected implementation exception");
		
	}
	
	public static Input empty() {
		return fromString("");
	}
	
	public static Input fromString(String s) {
		return fromString(s, URI.create("dummy:///"));
	}
	
	public static Input fromChar(char c) {
		int[] input = new int[2];
		input[0] = c;
		input[1] = EOF.VALUE;
		return new Input(input, URI.create("dummy:///"));
	}
	
	public static Input fromIntArray(int[] input) {
		return new Input(input, URI.create("dummy:///"));
	}

	private static int[] fromStream(InputStream in) throws IOException {
		BOMInputStream bomIn = new BOMInputStream(in, false);
		Reader reader = new BufferedReader(new InputStreamReader(bomIn));
		
		List<Integer> input = new ArrayList<>();

		int c = 0;
		while ((c = reader.read()) != -1) {
			if (!Character.isHighSurrogate((char) c)) {
				input.add(c);
			} else {
				int next = 0;
				if ((next = reader.read()) != -1) {
					input.add(Character.toCodePoint((char)c, (char)next));					
				}
			}
		}
		
		input.add(EOF.VALUE);

		reader.close();
		
		int[] intInput = new int[input.size()];
		int i = 0;
		for (Integer v : input) {
			intInput[i++] = v;
		}

		return intInput;
	}
	
	public static Input fromIntArray(int[] input, URI uri) {
		return new Input(input, uri);
	}
	
	public static Input fromPath(String path) throws IOException {
		return fromFile(new File(path));
	}
	
	public static Input fromFile(File file) throws IOException {
		return new Input(fromStream(new FileInputStream(file)), file.toURI());
	}

	private Input(int[] input, URI uri) {
		this.characters = input;
		this.uri = uri;
		lineColumns = new LineColumn[input.length];
		calculateLineLengths();
	}
	
	public int charAt(int index) {
		return characters[index];
	}

	public int length() {
		return characters.length;
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
		
		if(target.length > length() - from) {
			return false;
		}
		
		int i = 0;
		while(i < target.length) {
			if(target[i] != characters[from + i]) {
				return false;
			}
			i++;
		}
		
		return true;
	}
	
	public static int[] toIntArray(String s) {
		int[] array = new int[s.codePointCount(0, s.length())];
		for(int i = 0; i < array.length; i++) {
			array[i] = s.codePointAt(i);
		}
		return array;
	}
	
	public LineColumn getLineColumn(int index) {
		if(index < 0 || index >= lineColumns.length) {
			return new LineColumn(0, 0);
		}
		return lineColumns[index];
	}
 
	public int getLineNumber(int index) {
		if(index < 0 || index >= lineColumns.length) {
			return 0;
		}
		return lineColumns[index].getLineNumber();
	}
	
	public int getColumnNumber(int index) {
		if(index < 0 || index >= lineColumns.length) {
			return 0;
		}
		return lineColumns[index].getColumnNumber();
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(this == obj) {
			return true;
		}
		
		if(! (obj instanceof Input)) {
			return false;
		}
		
		Input other = (Input) obj;
		
		return Arrays.equals(characters, other.characters);
	}
	
	public PositionInfo getPositionInfo(int leftExtent, int rightExtent) {
		return new PositionInfo(leftExtent, 
								rightExtent - leftExtent, 
								getLineNumber(leftExtent), 
								getColumnNumber(leftExtent), 
								getLineNumber(rightExtent), 
								getColumnNumber(rightExtent),
								uri);
	}
	
	

	private void calculateLineLengths() {
		int lineNumber = 1;
		int columnNumber = 1;

		// Empty input: only the end of line symbol
		if(characters.length == 1) {
			lineColumns[0] = new LineColumn(lineNumber, columnNumber);
			return;
		}
		
		for (int i = 0; i < characters.length; i++) {
			lineColumns[i] = new LineColumn(lineNumber, columnNumber);
			if (characters[i] == '\n') {
				lineCount++;
				lineNumber++;
				columnNumber = 1;
			} else if (characters[i] == '\r') {
				columnNumber = 1;
			} else {
				columnNumber++;
			}
		}
		
		// The end of the line char column as the last character
//		lineColumns[input.length - 1] = new LineColumn(lineColumns[input.length - 2]);
	}
		
	private static class LineColumn {

		private int lineNumber;
		private int columnNumber;
		
		public LineColumn(int lineNumber, int columnNumber) {
			this.lineNumber = lineNumber;
			this.columnNumber = columnNumber;
		}
		
		public int getLineNumber() {
			return lineNumber;
		}
		
		public int getColumnNumber() {
			return columnNumber;
		}
		
		@Override
		public String toString() {
			return "(" + lineNumber + ":" + columnNumber + ")";
		}
		
		@Override
		public boolean equals(Object obj) {
			if(this == obj) {
				return true;
			}
			
			if(!(obj instanceof LineColumn)) {
				return false;
			}
			
			LineColumn other = (LineColumn) obj;
			return lineNumber == other.lineNumber && columnNumber == other.columnNumber;
		}
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
		return lineCount;
	}
	
	public boolean isEmpty() {
		return length() == 1;
	}
	
	
	public URI getURI() {
		return uri;
	}

	public boolean isEndOfLine(int currentInputIndex) {
		return characters[currentInputIndex] == 0 || lineColumns[currentInputIndex + 1].columnNumber == 1;
	}
	
	public int[] getCharacters() {
		return characters;
	}

	public boolean isStartOfLine(int currentInputIndex) {
		return currentInputIndex == 0 || lineColumns[currentInputIndex].columnNumber == 1;
	}
	
}
