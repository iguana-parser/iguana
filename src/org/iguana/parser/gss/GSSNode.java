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

package org.iguana.parser.gss;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.iguana.grammar.slot.EndGrammarSlot;
import org.iguana.grammar.slot.GrammarSlot;
import org.iguana.parser.HashFunctions;
import org.iguana.sppf.NonPackedNode;
import org.iguana.sppf.NonterminalNode;
import org.iguana.sppf.PackedNode;
import org.iguana.util.Holder;
import org.iguana.util.collections.Key;

/**
 *
 * @author Ali Afroozeh
 * @author Anastasia Izmaylova
 * 
 */
public class GSSNode {
	
	private final GrammarSlot slot;

	private final int inputIndex;
	
	private Map<Integer, NonterminalNode> poppedElements;
	
	private Map<Key, NonterminalNode> poppedElementsWithValues;
	
	private final List<GSSEdge> gssEdges;

	private Set<Key> descriptors;
	
	public GSSNode(GrammarSlot slot, int inputIndex) {
		this.slot = slot;
		this.inputIndex = inputIndex;
		this.poppedElements = new HashMap<>();
		this.poppedElementsWithValues = new HashMap<>();
		this.gssEdges = new ArrayList<>();
		this.descriptors = null;
	}
	
	public NonterminalNode addToPoppedElements(int j, EndGrammarSlot slot, NonPackedNode child) {
		Holder<NonterminalNode> holder = new Holder<>();
		poppedElements.compute(j, (k, v) -> { 
			if (v == null) {
				NonterminalNode node = new NonterminalNode(slot.getNonterminal(), inputIndex, j);
				node.addPackedNode(new PackedNode(slot, j, node), child);
				holder.set(node);
				return node;
			}
			else {
				v.addPackedNode(new PackedNode(slot, j, v), child);
				return v;
			}
		});
		
 		return holder.get();
	}
	
	public NonterminalNode getNonterminalNode(int j) {
		return poppedElements.get(j);
	}
	
	public Iterable<NonterminalNode> getPoppedElements() {
		return poppedElements.values();
	}
	
	public GrammarSlot getGrammarSlot() {
		return slot;
	}

	public int getInputIndex() {
		return inputIndex;
	}
	
	public boolean getGSSEdge(GSSEdge edge) {
		return gssEdges.add(edge);
	}
	
	public int countGSSEdges() {
		return gssEdges.size();
	}
	
	public int countPoppedElements() {
		return poppedElements.size();
	}
	
	public int countDescriptors() {
		return descriptors == null ? 0 : descriptors.size();
	}
	
	public Iterable<GSSEdge> getGSSEdges() {
		return gssEdges;
	}
	
	public boolean equals(Object obj) {
		
		if(this == obj)
			return true;

		if (!(obj instanceof GSSNode))
			return false;
		
		GSSNode other = (GSSNode) obj;

		return  slot == other.getGrammarSlot() &&
				inputIndex == other.getInputIndex();
	}

	public int hashCode() {
		return HashFunctions.defaulFunction.hash(slot.getId(), inputIndex);
	}
	
	public String toString() {
		return String.format("(%s, %d)", slot, inputIndex);
	}

	public int getCountGSSEdges() {
		return gssEdges.size();
	}

	public boolean hasDescriptor(Key key) {
		if (descriptors == null) descriptors = new HashSet<>();
		return !descriptors.add(key);
	}

	public void clearDescriptors() {
		poppedElements.clear();
		gssEdges.clear();
		if (descriptors != null) descriptors.clear();
	}
	
	/**
	 * 
	 * Data-dependent GLL parsing
	 * 
	 */
	
	public NonterminalNode addToPoppedElements(Key key, EndGrammarSlot slot, NonPackedNode child, Object value) {	
		Holder<NonterminalNode> holder = new Holder<>();
		poppedElementsWithValues.compute(key, (k, v) -> { 
			if (v == null) {
				NonterminalNode node = new NonterminalNode(slot.getNonterminal(), inputIndex, child.getRightExtent(), value);
				node.addPackedNode(new PackedNode(slot, child.getRightExtent(), node), child);
				holder.set(node);
				return node;
			}
			else {
				v.addPackedNode(new PackedNode(slot, child.getRightExtent(), v), child);
				return v;
			}
		});
		
 		return holder.get();
	}
	
}
