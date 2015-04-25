package org.iguana.traversal;

import java.net.URI;


/**
 * 
 * @author Ali Afroozeh
 *
 */
public class PositionInfo {
	
	private final int offset;
	private final int length;
	private final int startLineNumber;
	private final int startColumnNumber;
	private final int endLineNumber;
	private final int endColumnNumber;
	private final URI uri;
	
	public PositionInfo(int offset, int length, int lineNumber, int columnNumber, int endLineNumber, int endColumnNumber, URI uri) {
		this.offset = offset;
		this.length = length;
		this.startLineNumber = lineNumber;
		this.startColumnNumber = columnNumber;
		this.endLineNumber = endLineNumber;
		this.endColumnNumber = endColumnNumber;
		this.uri = uri;
	}
	
	public int getOffset() {
		return offset;
	}
	
	public int getLength() {
		return length;
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
	
	public URI getURI() {
		return uri;
	}
	
}
