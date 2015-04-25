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

package org.iguana.traversal;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import org.iguana.sppf.IntermediateNode;
import org.iguana.sppf.NonPackedNode;
import org.iguana.sppf.NonterminalNode;
import org.iguana.sppf.PackedNode;
import org.iguana.sppf.SPPFNode;
import org.iguana.sppf.TerminalNode;

public class NonterminalNodeVisitor implements SPPFVisitor {

	private Set<SPPFNode> visitedNodes;
	private final Consumer<NonterminalNode> c;

	public static NonterminalNodeVisitor create(Consumer<NonterminalNode> c) {
		return new NonterminalNodeVisitor(c);
	}
	
	private NonterminalNodeVisitor(Consumer<NonterminalNode> c) {
		this.c = c;
		visitedNodes = new HashSet<>();
	}

	@Override
	public void visit(PackedNode node) {
		if (!isVisited(node)) {
			visitedNodes.add(node);
			for(SPPFNode child : node.getChildren()) {
				child.accept(this);
			}
		}
	}

	@Override
	public void visit(IntermediateNode node) {
		visitChildren(node);
	}

	@Override
	public void visit(NonterminalNode node) {
		c.accept(node);
		visitChildren(node);
	}

	@Override
	public void visit(TerminalNode node) {
	}
	
	private boolean isVisited(SPPFNode node) {
		return visitedNodes.contains(node);
	}	
	
	private void visitChildren(NonPackedNode node) {
		if (!isVisited(node)) {
			visitedNodes.add(node);
			node.getChildren().forEach(c -> c.accept(this));
		}
	}
	
}
