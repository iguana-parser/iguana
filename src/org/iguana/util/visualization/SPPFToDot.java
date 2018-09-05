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
import iguana.utils.visualization.DotGraph;
import org.iguana.sppf.*;
import org.iguana.traversal.SPPFVisitor;

import java.util.*;

import static iguana.utils.string.StringUtil.listToString;
import static iguana.utils.visualization.DotGraph.newEdge;
import static iguana.utils.visualization.DotGraph.newNode;

public class SPPFToDot implements SPPFVisitor<Void>  {
	
	private final boolean showPackedNodeLabel;

	private Map<SPPFNode, Integer> ids = new HashMap<>();
	
	private DotGraph dotGraph;

	protected Input input;
	
	private Set<NonPackedNode> visited = new HashSet<>();

	public static DotGraph getDotGraph(SPPFNode root, Input input) {
		return getDotGraph(root, input, false);
	}

	public static DotGraph getDotGraph(SPPFNode root, Input input, boolean showPackedNodeLabel) {
		DotGraph dotGraph = new DotGraph();
		SPPFToDot sppfToDot = new SPPFToDot(input, dotGraph, showPackedNodeLabel);
		root.accept(sppfToDot);
		return dotGraph;
	}

	public SPPFToDot(Input input, DotGraph dotGraph, boolean showPackedNodeLabel) {
		this.input = input;
		this.showPackedNodeLabel = showPackedNodeLabel;
		this.dotGraph = dotGraph;
	}

	@Override
	public Void visit(TerminalNode node) {
		if(!visited.contains(node)) {
			visited.add(node);
			String matchedInput = input.subString(node.getLeftExtent(), node.getIndex());
			String label = String.format("(%s, %d, %d): \"%s\"", node.getGrammarSlot(), node.getLeftExtent(), node.getIndex(), matchedInput);
			dotGraph.addNode(newNode(getId(node), label));
		}

		return null;
	}

	@Override
	public Void visit(NonterminalNode node) {
		if(!visited.contains(node)) {
			visited.add(node);
			
			String label;
			if (node.getValue() == null)
				label = String.format("(%s, %d, %d)", node.getGrammarSlot(), node.getLeftExtent(), node.getIndex());
			else {
				if (node.getValue() instanceof List<?>)
					label = String.format("(%s, %d, %d, %s)", node.getGrammarSlot(), node.getLeftExtent(), node.getIndex(),
												"(" + listToString((List<?>) node.getValue(), ",") + ")");
				else
					label = String.format("(%s, %d, %d, %s)", node.getGrammarSlot(), node.getLeftExtent(), node.getIndex(), node.getValue());
			}

			DotGraph.Node dotNode = newNode(getId(node), label);
			if (node.isAmbiguous()) {
				dotNode.setColor(DotGraph.Color.RED);
			}
			dotGraph.addNode(dotNode);
			addEdgesToChildren(node);
			
			visitChildren(node);
		}
		return null;
	}

	@Override
	public Void visit(IntermediateNode node) {
		if(!visited.contains(node)) {
			visited.add(node);
			
			String label = String.format("(%s, %d, %d)", node.getGrammarSlot(), node.getLeftExtent(), node.getIndex());

			DotGraph.Node dotNode = newNode(getId(node), label).setShape(DotGraph.Shape.RECTANGLE);
			if (node.isAmbiguous()) {
				dotNode.setColor(DotGraph.Color.RED);
			}
			dotGraph.addNode(dotNode);
			addEdgesToChildren(node);
	
			visitChildren(node);
		}
		return null;
	}

	@Override
	public Void visit(PackedNode node) {
		DotGraph.Node dotNode = newNode(getId(node)).setShape(DotGraph.Shape.CIRCLE);
		if (showPackedNodeLabel) {
			dotNode.setLabel(node.toString());
		}
		dotGraph.addNode(dotNode);

		addEdgesToChildren(node);
		
		visitChildren(node);
		return null;
	}
	
	private void addEdgesToChildren(SPPFNode node) {
	    for (int i = 0; i < node.childrenCount(); i++) {
            addEdgeToChild(node, node.getChildAt(i));
        }
	}
	
	private void addEdgeToChild(SPPFNode parentNode, SPPFNode childNode) {
		dotGraph.addEdge(newEdge(getId(parentNode), getId(childNode)));
	}
	
	private void visitChildren(SPPFNode node) {
        for (int i = 0; i < node.childrenCount(); i++) {
            node.getChildAt(i).accept(this);
        }
	}

	private int getId(SPPFNode node) {
		return ids.computeIfAbsent(node, k -> ids.size() + 1);
	}

}
