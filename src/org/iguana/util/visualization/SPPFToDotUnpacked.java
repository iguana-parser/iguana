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

import static org.iguana.util.visualization.GraphVizUtil.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.iguana.sppf.IntermediateNode;
import org.iguana.sppf.NonterminalNode;
import org.iguana.sppf.PackedNode;
import org.iguana.sppf.SPPFNode;
import org.iguana.sppf.TerminalNode;
import org.iguana.traversal.SPPFVisitor;
import org.iguana.traversal.SPPFVisitorUtil;
import org.iguana.util.Input;

/**
 * Creates a Graphviz's dot format representation of an SPPF node in form of
 * its parse trees separately.
 * 
 * The unpacking of an SPPF is in general exponential, so this class should not
 * be used for large inputs.
 * 
 * 
 * @author Ali Afroozeh
 * 
 * @see SPPFVisitor
 */
public class SPPFToDotUnpacked extends ToDot {
	
	private final boolean showPackedNodeLabel;
	
	protected Set<String> parseTrees;
	
	private Map<SPPFNode, Integer> map;
	
	SPPFNode[] permutation;

	private Input input;

	public SPPFToDotUnpacked(Input input) {
		this(input, false);
	}
	
	public SPPFToDotUnpacked(Input input, boolean showPackedNodeLabel) {
		this.input = input;
		this.showPackedNodeLabel = showPackedNodeLabel;
		this.parseTrees = new HashSet<>();
	}
	
	public void visit(SPPFNode node) {

		createPermutationMaps(node);
		
		int j = 0;
		for(SPPFNode sppfNode : map.keySet()) {
			permutation[j++] = sppfNode;
		}
		
		StringBuilder sb = new StringBuilder();
		visit(node, sb);
		parseTrees.add(sb.toString());
		
		int i = 0;
		
		while(true) {
			
			boolean newPermutation = add(i, node);
			
			if(!newPermutation) {
				break;
			}			
		}
	}
	
	private boolean add(int i, SPPFNode node) {
		
		if(i == permutation.length) {
			return false;
		}
		
		if(i == permutation.length - 1 && map.get(permutation[i]) == permutation[i].childrenCount()) {
			return false;
		}
		
		if(map.get(permutation[i]) < permutation[i].childrenCount()) {
			map.put(permutation[i], map.get(permutation[i]) + 1);
			
			if(map.get(permutation[i]) == permutation[i].childrenCount()) {
				for(int k = 0; k <= i; k++) {
					map.put(permutation[k], 0);
				}
				return add(i + 1, node);
			}
			
			StringBuilder sb = new StringBuilder();
			visit(node, sb);
			 			
			parseTrees.add(sb.toString());
			return true;
		}
		
		
		return false;
	}
	
	private void visit(SPPFNode node, StringBuilder sb) {

		if(node instanceof NonterminalNode) {
			visit((NonterminalNode) node, sb);
		} 
		else if(node instanceof PackedNode) {
			visit((PackedNode)node, sb);
		} 
		else if(node instanceof IntermediateNode) {
			visit((IntermediateNode)node, sb); 
		}
	}

	public void visit(NonterminalNode node, StringBuilder sb) {
		
		String label = node.getGrammarSlot().toString();
		
		sb.append("\"" + getId(node) + "\"" + String.format(SYMBOL_NODE, replaceWhiteSpace(label)) + "\n");
		
		if(node.isAmbiguous()) {
			
			int i = 0;
			while(i < node.childrenCount()) {
				SPPFVisitorUtil.removeIntermediateNode((PackedNode) node.getChildAt(i));
				i++;
			}
			
			Integer index = map.get(node);
			SPPFNode child = node.getChildAt(index);
			
			addEdgesToChildren(node, child.getChildren(), sb);
			
			for(SPPFNode childOfPackedNode : child.getChildren()) {
				visit(childOfPackedNode, sb);					
			}
		}
		else {
			SPPFVisitorUtil.removeIntermediateNode(node);
			addEdgesToChildren(node, node.getChildren(), sb);
			
			for(SPPFNode child : node.getChildren()) {
				visit(child, sb);
			}
		}
	}

	public void visit(IntermediateNode node, StringBuilder sb) {
		sb.append("\"" + getId(node) + "\"" + String.format(INTERMEDIATE_NODE, replaceWhiteSpace(node.toString())) + "\n");
		addEdgesToChildren(node, node.getChildren(), sb);

		for(SPPFNode child : node.getChildren()) {
			visit(child, sb);
		}
	}

	public void visit(PackedNode node, StringBuilder sb) {

		if(showPackedNodeLabel) {
			sb.append("\"" + getId(node) + "\"" + String.format(PACKED_NODE, replaceWhiteSpace(node.toString())) + "\n");
		} else {
			sb.append("\"" + getId(node) + "\"" + String.format(PACKED_NODE, "") + "\n");
		}
		addEdgesToChildren(node, node.getChildren(), sb);
		
		for(SPPFNode child : node.getChildren()) {
			visit(child, sb);
		}
	}
	
	public Iterable<String> getResult() {
		return parseTrees;
	}
	
	protected void addEdgesToChildren(SPPFNode node, Iterable<? extends SPPFNode> children, StringBuilder sb) {
		for (SPPFNode child : children) {
			addEdgeToChild(node, child, sb);
		}
	}
	
	protected void addEdgesToChildrenWithNode(SPPFNode node, Iterable<SPPFNode> children, StringBuilder sb) {
		for (SPPFNode child : children) {
			addEdgeToChild(node, child, sb);
			
		}
	}
	
	protected void addEdgeToChild(SPPFNode parentNode, SPPFNode childNode, StringBuilder sb) {
		sb.append(EDGE + "\"" + getId(parentNode) + "\"" + "->" + "{\"" + getId(childNode) + "\"}" + "\n");
	}
	
	protected String replaceWhiteSpace(String s) {
		return s.replace("\\", "\\\\").replace("\t", "\\\\t").replace("\n", "\\\\n").replace("\r", "\\\\r").replace("\"", "\\\"");
	}

	
	private void createPermutationMaps(SPPFNode node) {
		map = new HashMap<>();
		SPPFVisitor sppfVisitor = new SPPFVisitor() {
			
			@Override
			public void visit(PackedNode node) {
				SPPFVisitorUtil.visitChildren(node, this);
			}
			
			@Override
			public void visit(IntermediateNode node) {
				SPPFVisitorUtil.visitChildren(node, this);
			}
			
			@Override
			public void visit(NonterminalNode node) {
				if(node.isAmbiguous()) {
					map.put(node, 0);
				}
				SPPFVisitorUtil.visitChildren(node, this);
			}
			
			@Override
			public void visit(TerminalNode node) {}
		};
		
		node.accept(sppfVisitor);
		
		permutation = new SPPFNode[map.size()];		
	}
	
}
