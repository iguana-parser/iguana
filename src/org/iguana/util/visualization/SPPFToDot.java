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

package org.iguana.util.visualization;

import iguana.utils.input.Input;
import org.iguana.sppf.*;
import org.iguana.traversal.SPPFVisitor;

import java.util.*;

import static iguana.utils.string.StringUtil.listToString;
import static iguana.utils.visualization.GraphVizUtil.*;

/**
 * Creates a Graphviz's dot format representation of an SPPF node.
 * 
 * 
 * @author Ali Afroozeh
 * 
 * @see SPPFVisitor
 * 
 */
public class SPPFToDot implements SPPFVisitor<Void>  {
	
	protected final boolean showPackedNodeLabel;
	
	protected StringBuilder sb;

	protected Input input;
	
	protected Set<NonPackedNode> visited = new HashSet<>();

	public SPPFToDot(Input input) {
		this(input, false);
	}
	
	public SPPFToDot(Input input, boolean showPackedNodeLabel) {
		this.input = input;
		this.showPackedNodeLabel = showPackedNodeLabel;
		this.sb = new StringBuilder();
		ids.clear();
	}

	@Override
	public Void visit(TerminalNode node) {
		
		if(!visited.contains(node)) {
			visited.add(node);
			String matchedInput = input.subString(node.getLeftExtent(), node.getRightExtent());
			String color = "black";
			String label = String.format("(%s, %d, %d): \"%s\"", node.getGrammarSlot(), node.getLeftExtent(), node.getRightExtent(), matchedInput);
			sb.append("\"" + getId(node) + "\"" + String.format(ROUNDED_RECTANGLE, color, replaceWhiteSpace(label)) + "\n");
		}

		return null;
	}

	@Override
	public Void visit(NonterminalNode node) {
		if(!visited.contains(node)) {
			visited.add(node);
			
			String label;
			if (node.getValue() == null)
				label = String.format("(%s, %d, %d)", node.getGrammarSlot(), node.getLeftExtent(), node.getRightExtent());
			else {
				if (node.getValue() instanceof List<?>)
					label = String.format("(%s, %d, %d, %s)", node.getGrammarSlot(), node.getLeftExtent(), node.getRightExtent(), 
												"(" + listToString((List<?>) node.getValue(), ",") + ")");
				else
					label = String.format("(%s, %d, %d, %s)", node.getGrammarSlot(), node.getLeftExtent(), node.getRightExtent(), node.getValue());
			}

			String color = node.isAmbiguous() ? "red" : "black";
			sb.append("\"" + getId(node) + "\"" + String.format(ROUNDED_RECTANGLE, color, replaceWhiteSpace(label)) + "\n");
			addEdgesToChildren(node);
			
			visitChildren(node);
		}
		return null;
	}

	@Override
	public Void visit(IntermediateNode node) {
		if(!visited.contains(node)) {
			visited.add(node);
			
			String label = String.format("(%s, %d, %d)", node.getGrammarSlot(), node.getLeftExtent(), node.getRightExtent());

			String color = node.isAmbiguous() ? "red" : "black";
			sb.append("\"" + getId(node) + "\"" + String.format(RECTANGLE, color, replaceWhiteSpace(label)) + "\n");
			addEdgesToChildren(node);
	
			visitChildren(node);
		}
		return null;
	}

	@Override
	public Void visit(PackedNode node) {
		if(showPackedNodeLabel) {
			sb.append("\"" + getId(node) + "\"" + String.format(CIRCLE, "black", replaceWhiteSpace(node.toString())) + "\n");
		} else {
			sb.append("\"" + getId(node) + "\"" + String.format(CIRCLE, "black", "", "") + "\n");
		}
		addEdgesToChildren(node);
		
		visitChildren(node);
		return null;
	}
	
	protected void addEdgesToChildren(SPPFNode node) {
		for (SPPFNode child : node.getChildren()) {
			addEdgeToChild(node, child);
		}
	}
	
	protected void addEdgeToChild(SPPFNode parentNode, SPPFNode childNode) {
		sb.append(EDGE + "\"" + getId(parentNode) + "\"" + "->" + "{\"" + getId(childNode) + "\"}" + "\n");
	}
	
	protected String replaceWhiteSpace(String s) {
		return s.replace("\\", "\\\\").replace("\t", "\\\\t").replace("\n", "\\\\n").replace("\r", "\\\\r").replace("\"", "\\\"");
	}

	public String getString() {
		return sb.toString();
	}

	public void visitChildren(SPPFNode node) {
		for (SPPFNode child : node.getChildren()) {
			child.accept(this);
		}
	}

	private static Map<SPPFNode, Integer> ids = new HashMap<>();

	private static int getId(SPPFNode node) {
		return ids.computeIfAbsent(node, k -> ids.size() + 1);
	}

}
