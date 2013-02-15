package org.jgll.util;

public class LineColumn {

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
