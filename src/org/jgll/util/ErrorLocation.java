package org.jgll.util;

public class ErrorLocation {

	private int lineNumber;
	private int columnNumber;
	
	public ErrorLocation(int lineNumber, int columnNumber) {
		this.lineNumber = lineNumber;
		this.columnNumber = columnNumber;
	}
	
	public int getLineNumber() {
		return lineNumber;
	}
	
	public int getColumnNumber() {
		return columnNumber;
	}
}
