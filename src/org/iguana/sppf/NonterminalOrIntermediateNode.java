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

package org.iguana.sppf;

import java.util.ArrayList;
import java.util.List;

import org.iguana.grammar.slot.GrammarSlot;


public abstract class NonterminalOrIntermediateNode extends NonPackedNode {

	protected List<PackedNode> children;
	private PackedNodeSet set;
	
	public NonterminalOrIntermediateNode(GrammarSlot slot, int leftExtent, int rightExtent, PackedNodeSet set) {
		super(slot, leftExtent, rightExtent);
		this.set = set;
		children = new ArrayList<>();
	}

	public void addChild(PackedNode node) {
		//TODO: change it! PackedNodes cannot be added via this method at parse time.
		children.add(node);
	}
	
	public void removeChild(SPPFNode node) {
		children.remove(node);
	}
	
	public boolean addPackedNode(PackedNode packedNode, NonPackedNode leftChild, NonPackedNode rightChild) {
		if (set.addPackedNode(packedNode.getGrammarSlot(), packedNode.getPivot())) {
			children.add(packedNode);
			packedNode.addChild(leftChild);
			packedNode.addChild(rightChild);
			return true;
		}
		return false;
	}
	
	public boolean addPackedNode(PackedNode packedNode, NonPackedNode child) {
		if (set.addPackedNode(packedNode.getGrammarSlot(), packedNode.getPivot())) {
			children.add(packedNode);
			packedNode.addChild(child);
			return true;
		}
		return false;	
	}
	
	@Override
	public PackedNode getChildAt(int index) {
		return index < children.size() ? children.get(index) : null;
	}
	
	@Override
	public List<PackedNode> getChildren() {
		return children;
	}
	
	@Override
	public int childrenCount() {
		return children.size();
	}
	
	public boolean isAmbiguous() {
		return children.size() > 1;
	}
	
	public int getCountPackedNodes() {
		return children.size();
	}
}
