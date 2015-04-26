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
