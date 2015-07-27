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
import java.util.List;
import java.util.Map;

import org.iguana.datadependent.env.Environment;
import org.iguana.grammar.slot.BodyGrammarSlot;
import org.iguana.grammar.slot.EndGrammarSlot;
import org.iguana.grammar.slot.NonterminalGrammarSlot;
import org.iguana.parser.GLLParser;
import org.iguana.parser.HashFunctions;
import org.iguana.parser.descriptor.Descriptor;
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
	
	private final NonterminalGrammarSlot slot;

	private final int inputIndex;
	
	private Map<Object, NonterminalNode> poppedElements;
	
	private final List<GSSEdge> gssEdges;

	public GSSNode(NonterminalGrammarSlot slot, int inputIndex) {
		this.slot = slot;
		this.inputIndex = inputIndex;
		this.poppedElements = new HashMap<>();
		this.gssEdges = new ArrayList<>();
	}
	
	public NonterminalNode addToPoppedElements(GLLParser parser, int j, EndGrammarSlot slot, NonPackedNode child) {
		Holder<NonterminalNode> holder = new Holder<>();
		poppedElements.compute(j, (k, v) -> { 
			if (v == null) {
				NonterminalNode node = new NonterminalNode(slot.getNonterminal(), inputIndex, j);
				node.addPackedNode(parser, new PackedNode(slot, j, node), child);
				
				parser.nonterminalNodeAdded(node);
				
				holder.set(node);
				return node;
			}
			else {
				v.addPackedNode(parser, new PackedNode(slot, j, v), child);
				return v;
			}
		});
		
 		return holder.get();
	}
	
	public void createGSSEdge(GLLParser parser, BodyGrammarSlot returnSlot, GSSNode destination, NonPackedNode w) {
		NewGSSEdgeImpl edge = new NewGSSEdgeImpl(returnSlot, w, destination);
		parser.gssEdgeAdded(edge);
		
		gssEdges.add(edge);
		
		for (NonPackedNode z : getPoppedElements()) {	
			
			if (!edge.getReturnSlot().testFollow(parser.getInput().charAt(z.getRightExtent()))) continue;
			
			Descriptor descriptor = edge.addDescriptor(parser, this, z.getRightExtent(), z);
			if (descriptor != null) {
				parser.scheduleDescriptor(descriptor);
			}
		}
	}
	
	public NonterminalNode getNonterminalNode(int j) {
		return poppedElements.get(j);
	}
	
	public Iterable<NonterminalNode> getPoppedElements() {
		return poppedElements.values();
	}
	
	public NonterminalGrammarSlot getGrammarSlot() {
		return slot;
	}

	public int getInputIndex() {
		return inputIndex;
	}
	
	public int countGSSEdges() {
		return gssEdges.size();
	}
	
	public int countPoppedElements() {
		return poppedElements.size();
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
	
	/**
	 * 
	 * Data-dependent GLL parsing
	 * 
	 */
	
	public NonterminalNode addToPoppedElements(GLLParser parser, Key key, EndGrammarSlot slot, NonPackedNode child, Object value) {	
		Holder<NonterminalNode> holder = new Holder<>();
		poppedElements.compute(key, (k, v) -> { 
			if (v == null) {
				NonterminalNode node = new NonterminalNode(slot.getNonterminal(), inputIndex, child.getRightExtent(), value);
				node.addPackedNode(parser, new PackedNode(slot, child.getRightExtent(), node), child);
				holder.set(node);
				return node;
			}
			else {
				v.addPackedNode(parser, new PackedNode(slot, child.getRightExtent(), v), child);
				return v;
			}
		});
		
 		return holder.get();
	}
	
	public void createGSSEdge(GLLParser parser, BodyGrammarSlot returnSlot, GSSNode destination, NonPackedNode w, Environment env) {
		NewGSSEdgeImpl edge = new org.iguana.datadependent.gss.NewGSSEdgeImpl(returnSlot, w, destination, env);
		
		gssEdges.add(edge);
		parser.gssEdgeAdded(edge);

		for (NonPackedNode z : getPoppedElements()) {
			
			if (!edge.getReturnSlot().testFollow(parser.getInput().charAt(z.getRightExtent()))) continue;
			
			Descriptor descriptor = edge.addDescriptor(parser, this, z.getRightExtent(), z);
			if (descriptor != null) {
				parser.scheduleDescriptor(descriptor);
			}
		}
			
	}
	
}
