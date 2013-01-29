package org.jgll.exception;

import org.jgll.util.ErrorLocation;
import org.jgll.util.InputUtil;


@SuppressWarnings("serial")
public class ParsingFailedException extends RuntimeException {
	
	private final int inputIndex;
	private final int position;
	private final String[] expectedTokens;
	private final String[] alternate;
	private final String head;
	
	private String input;
	
	public ParsingFailedException(String head, int inputIndex, String input) {
		this(head, null, inputIndex, 0, null, input);
	}
	
	public ParsingFailedException(String head, String[] alternate, int inputIndex, int position, String[] expectedTokens, String input) {
		this.head = head;
		this.alternate = alternate;
		this.inputIndex = inputIndex;
		this.position = position;
		this.expectedTokens = expectedTokens;
		this.input = input;
	}
	
	public int getInputIndex() {
		return inputIndex;
	}
		
	public int getPosition() {
		return position;
	}
	
	@Override
	public String toString() {
		
		ErrorLocation loc = new InputUtil(input).getLineNumber(inputIndex);
		int lineNumber = loc.getLineNumber();
		int columnNumber = loc.getColumnNumber();
		
		// TODO: make the error reporting better.
		StringBuilder sb = new StringBuilder();
		sb.append("Parse error in recognizing a " + head  + " at line:" + lineNumber + " column:" + columnNumber).append("\n");
		sb.append(inputIndex + "\n");
		
		return sb.toString();
	}
	
}
