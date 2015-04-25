/*
 * Copyright (c) 2015, CWI
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

import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

import org.iguana.grammar.slot.GrammarSlot;
import org.iguana.parser.gss.GSSNode;
import org.iguana.util.Input;


/**
 * 
 * @author Ali Afroozeh
 *
 */
public class ParseError implements ParseResult {

	private final GSSNode currentNode;
	private final GrammarSlot slot;
	private final int inputIndex;
	private Input input;
	
	public ParseError(GrammarSlot slot, Input input, int inputIndex, GSSNode curerntNode) {
		this.slot = slot;
		this.input = input;
		this.inputIndex = inputIndex;
		this.currentNode = curerntNode;
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
	
	public void printGrammarTrace() {
		printGrammarTrace(System.out);
	}
	
	public void printGrammarTrace(PrintStream out) {
		out.println(toString());
		
//		indent(out, 1, new GSSNode(((NonterminalGrammarSlot) slot).getNonterminal(), inputIndex, input.length()));
		
		GSSNode gssNode = currentNode;
		
		while(gssNode != null) {
			indent(out, 1, gssNode);			
			gssNode = findMergePoint(gssNode, out, 1);
		}
	}

	private void indent(PrintStream out, int i, GSSNode node) {
		if(node == null) {
			return;
		}
		out.println(String.format("%" + i * 2 + "s, %d", node.getGrammarSlot(), node.getInputIndex()));
	}
	
	private GSSNode findMergePoint(GSSNode node, PrintStream out, int i) {
		
		if(node.sizeChildren() == 1) {
			return node.getChildren().iterator().next();
		}
		
		return reachableFrom(node, out, i);
	}
	
	/**
	 * Adds all the GSS nodes reachable from the given node to the
	 * provided set and removes the node from the set.
	 * 
	 * @throws IllegalArgumentException if the given set is null.
	 *  
	 */
	private GSSNode reachableFrom(GSSNode node, PrintStream out, int i) {
		
		Set<GSSNode> set = new HashSet<>();
		Deque<GSSNode> frontier = new ArrayDeque<>();
		
		for(GSSNode destination : node.getChildren()) {
			set.add(destination);
			frontier.add(destination);
			indent(out, i+1, destination);
		}
		
		i++;
		while(frontier.size() > 1) {
			GSSNode f = (GSSNode) frontier.poll();
			for(GSSNode destination : f.getChildren()) {
				if(!set.contains(destination)) {
					set.add(destination);
					frontier.add(destination);
					indent(out, i+1, destination);
				}
			}
		}
		
		return frontier.poll();
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
