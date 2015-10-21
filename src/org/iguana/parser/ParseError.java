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

package org.iguana.parser;

import iguana.utils.input.Input;
import org.iguana.grammar.slot.GrammarSlot;
import org.iguana.parser.gss.GSSNode;


/**
 * 
 * @author Ali Afroozeh
 *
 */
public class ParseError extends AbstractParseResult {

	private final GrammarSlot slot;
	private final int inputIndex;
    private final GSSNode gssNode;

    public ParseError(GrammarSlot slot, Input input, int inputIndex, GSSNode gssNode) {
		super(input);
		this.slot = slot;
		this.inputIndex = inputIndex;
        this.gssNode = gssNode;
    }
	
	public int getInputIndex() {
		return inputIndex;
	}

	public GrammarSlot getSlot() {
		return slot;
	}
	
	public static String getMessage(Input input, int inputIndex) {		
		int lineNumber = input.getLineNumber(inputIndex);
		int columnNumber = input.getColumnNumber(inputIndex);
		
		return String.format("Parse error at input index: %d, line: %d, column: %d", inputIndex, lineNumber, columnNumber);
	}

	@Override
	public String toString() {
		return String.format("Parse error at %d, line: %d, column: %d", inputIndex, 
							 input.getLineNumber(inputIndex), 
							 input.getColumnNumber(inputIndex));
	}

	@Override
	public boolean isParseError() {
		return true;
	}

	@Override
	public boolean isParseSuccess() {
		return false;
	}

	@Override
	public ParseError asParseError() {
		return this;
	}

	@Override
	public ParseSuccess asParseSuccess() {
		throw new RuntimeException("Cannot call getParseSuccess on ParseError.");
	}
 	
}
