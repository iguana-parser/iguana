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

import org.iguana.sppf.NonterminalNode;
import org.iguana.sppf.PackedNode;
import org.iguana.traversal.SPPFVisitor;
import org.iguana.traversal.SPPFVisitorUtil;
import org.iguana.util.Input;

/**
 * Creates a Graphviz's dot format representation of an SPPF node.
 * 
 * @author Ali Afroozeh
 * 
 * @see SPPFVisitor
 */
public class ToDotWithoutIntermediateNodes extends SPPFToDot {
	
	public ToDotWithoutIntermediateNodes(Input input) {
		super(input);
	}
	
	@Override
	public void visit(NonterminalNode node) {

		if(!visited.contains(node)) {
			visited.add(node);
			
			if(node.isAmbiguous()) {
				int i = 0;
				while(i < node.childrenCount()) {
					SPPFVisitorUtil.removeIntermediateNode((PackedNode) node.getChildAt(i));
					i++;
				}
			} else {
				SPPFVisitorUtil.removeIntermediateNode(node);
			}
	
			String label = String.format("(%s, %d, %d)", node.getGrammarSlot().toString(), 
					 node.getLeftExtent(), node.getRightExtent());

			sb.append("\"" + getId(node) + "\"" + String.format(SYMBOL_NODE, replaceWhiteSpace(label)) + "\n");
			addEdgesToChildren(node);
			
			SPPFVisitorUtil.visitChildren(node, this);
		}		
	}
	
	@Override
	public void visit(PackedNode node) {
		if(node.isAmbiguous()) {
			int i = 0;
			while(i < node.childrenCount()) {
				SPPFVisitorUtil.removeIntermediateNode((PackedNode) node.getChildAt(i));
				i++;
			}
		} else {
			SPPFVisitorUtil.removeIntermediateNode(node);
		}

		if(showPackedNodeLabel) {
			sb.append("\"" + getId(node) + "\"" + String.format(PACKED_NODE, replaceWhiteSpace(node.toString())) + "\n");
		} else {
			sb.append("\"" + getId(node) + "\"" + String.format(PACKED_NODE, "") + "\n");
		}
		addEdgesToChildren(node);
		
		SPPFVisitorUtil.visitChildren(node, this);
	}
}
