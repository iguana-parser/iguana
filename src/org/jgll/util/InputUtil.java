package org.jgll.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class InputUtil {

	private int[] input;

	private LineColumn[] lineColumns;

	private static InputUtil instance;
	
	public synchronized static InputUtil getInstance() {
		if(instance == null) {
			instance = new InputUtil();
		}
		
		return instance;
	}

	private InputUtil() {};
	
	public void setInput(String input) {
		setInput(fromString(input));
	}

	public void setInput(int[] input) {
		this.input = input;
		lineColumns = new LineColumn[input.length - 1];
		calculateLineLengths();
	}
	
	public int charAt(int index) {
		return input[index];
	}

	public static String readTextFromFile(String parentDir, String fileName) throws IOException {
		return readTextFromFile(new File(parentDir, fileName));
	}

	public static int[] fromString(String s) {
		int[] input = new int[s.length() + 1];
		for (int i = 0; i < s.length(); i++) {
			input[i] = s.charAt(i);
		}
		input[s.length()] = -1;
		return input;
	}
	
	/**
	 * Returns the whole contents of a text file as a string.
	 * 
	 * @param path
	 *            the path to the text file
	 * @throws IOException
	 */
	public static String readTextFromFile(String path) throws IOException {
		return readTextFromFile(new File(path));
	}

	public static String readTextFromFile(File file) throws IOException {
		StringBuilder sb = new StringBuilder();

		InputStream in = new BufferedInputStream(new FileInputStream(file));
		int c = 0;
		while ((c = in.read()) != -1) {
			sb.append((char) c);
		}

		in.close();

		return sb.toString();
	}

	public static String read(InputStream is) throws IOException {
		StringBuilder sb = new StringBuilder();
		BufferedInputStream in = new BufferedInputStream(is);
		int c = 0;
		while ((c = in.read()) != -1) {
			sb.append((char) c);
		}
		in.close();
		return sb.toString();
	}

	public LineColumn getLineNumber(int index) {
		return lineColumns[index];
	}

	private void calculateLineLengths() {
		int lineNumber = 1;
		int columnNumber = 1;

		for (int i = 0; i < input.length - 1; i++) {
			lineColumns[i] = new LineColumn(lineNumber, columnNumber);
			if (input[i] == '\n') {
				lineNumber++;
				columnNumber = 1;
			} else if(input[i] == '\r' && i < input.length - 2 && input[i + 1] == '\n') {
				columnNumber = 1;
				lineNumber++;
				i++;
			} else if (input[i] == '\r') {
				lineNumber++;
				columnNumber = 1;
			} else {
				columnNumber++;
			}
		}
	}
}
