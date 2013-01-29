package org.jgll.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputUtil {

	private String input;

	/**
	 * The {@code lines} list keeps the size of each line, in number of
	 * characters.
	 */
	private List<Integer> lines = new ArrayList<Integer>();

	public InputUtil(String input) {
		this.input = input;
		calculateLineLengths();
	}

	public static String readTextFromFile(String parentDir, String fileName) throws IOException {
		return readTextFromFile(new File(parentDir, fileName));
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
		while((c = in.read()) != -1) {
			sb.append((char) c);
		}
		in.close();
		return sb.toString();
	}

	public ErrorLocation getLineNumber(int index) {

		if (input.isEmpty()) {
			return new ErrorLocation(1, 1);
		}

		int lineNumber;
		int columnNumber;

		// Error happens at the EOF character
		if (index == input.length()) {
			lineNumber = lines.size();
			columnNumber = lines.get(lines.size() - 1) + 1;
		} else {
			int acc = 0;
			lineNumber = 1;
			columnNumber = 0;
			for (int size : lines) {
				acc += size;
				if (acc > index) {
					columnNumber = index - (acc - size) + 1;
					break;
				}
				lineNumber++;
			}
		}

		ErrorLocation errorLocation = new ErrorLocation(lineNumber, columnNumber);
		return errorLocation;
	}

	/**
	 * Populates the {@code lines} list, which holds the length of each line.
	 */
	private void calculateLineLengths() {

		Pattern pattern = Pattern.compile("\\r?\\n");
		Matcher m = pattern.matcher(input);

		// keeps the lines and delimiters
		List<String> ret = new ArrayList<String>();
		int start = 0;
		while (m.find()) {
			ret.add(input.substring(start, m.start()));
			ret.add(m.group());
			start = m.end();
		}

		// Collect the last chunk of text after the last newline character.
		if (start < input.length()) {
			ret.add(input.substring(start, input.length()));
		}

		int i = 0;
		while (i < ret.size()) {
			// The end of the line character is counted as the last character of
			// the first line.
			// ret.get(i + 1) determines the delimiter
			if (i + 1 < ret.size()) {
				lines.add(ret.get(i).length() + ret.get(i + 1).length());
			} else {
				lines.add(ret.get(i).length());
			}

			i += 2;
		}
	}
}
