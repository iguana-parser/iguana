package org.jgll.traversal;


/**
 * 
 * @author Ali Afroozeh
 *
 */
public class PositionInfo {
	
	private final int start;
	private final int offset;
	private final int startLineNumber;
	private final int startColumnNumber;
	private final int endLineNumber;
	private final int endColumnNumber;

	public PositionInfo(int start, int offset, int lineNumber, int columnNumber, int endLineNumber, int endColumnNumber) {
		this.start = start;
		this.offset = offset;
		this.startLineNumber = lineNumber;
		this.startColumnNumber = columnNumber;
		this.endLineNumber = endLineNumber;
		this.endColumnNumber = endColumnNumber;
	}
	
	public int getStart() {
		return start;
	}
	
	public int getOffset() {
		return offset;
	}

	public int getLineNumber() {
		return startLineNumber;
	}
	
	public int getColumn() {
		return startColumnNumber;
	}

	public int getEndLineNumber() {
		return endLineNumber;
	}
	
	public int getEndColumnNumber() {
		return endColumnNumber;
	}
	
}
