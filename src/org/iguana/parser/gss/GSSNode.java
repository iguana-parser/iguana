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

import iguana.utils.collections.hash.MurmurHash3;
import iguana.utils.input.Input;
import org.iguana.datadependent.env.Environment;
import org.iguana.grammar.slot.BodyGrammarSlot;
import org.iguana.grammar.slot.EndGrammarSlot;
import org.iguana.grammar.slot.NonterminalGrammarSlot;
import org.iguana.parser.descriptor.Descriptor;
import org.iguana.sppf.NonPackedNode;
import org.iguana.sppf.NonterminalNode;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ali Afroozeh
 * @author Anastasia Izmaylova
 * 
 */
public class GSSNode {
	
	protected final NonterminalGrammarSlot slot;

	private final int inputIndex;
	
	private final PoppedElements poppedElements;
	
	private final List<GSSEdge> gssEdges;

	public GSSNode(NonterminalGrammarSlot slot, int inputIndex) {
		this.slot = slot;
		this.inputIndex = inputIndex;
		this.poppedElements = new PoppedElements(slot.getRuntime());
		this.gssEdges = new ArrayList<>();
	}
	
	public void createGSSEdge(Input input, BodyGrammarSlot returnSlot, GSSNode destination, NonPackedNode w) {
		NewGSSEdgeImpl edge = new NewGSSEdgeImpl(returnSlot, w, destination);
		slot.getRuntime().gssEdgeAdded(edge);
		
		gssEdges.add(edge);
		
		poppedElements.forEach(z -> {
			if (edge.getReturnSlot().testFollow(input.charAt(z.getRightExtent()))) {
				Descriptor descriptor = edge.addDescriptor(input, this, z);
				if (descriptor != null) {
					slot.getRuntime().scheduleDescriptor(descriptor);
				}
			}
		});
	}

    public void pop(Input input, EndGrammarSlot slot, NonPackedNode child) {
        slot.getRuntime().log("Pop %s, %d, %s", this, inputIndex, child);
        NonterminalNode node = poppedElements.add(input, slot, child);
        if (node == null) return; else iterateOverEdges(input, node);
    }

    public void pop(Input input, EndGrammarSlot slot, NonPackedNode child, Object value) {
        NonterminalNode node = poppedElements.add(input, slot, child, value);
        if (node == null) return; else iterateOverEdges(input, node);
    }

    private void iterateOverEdges(Input input, NonterminalNode node) {
        for(GSSEdge edge : getGSSEdges()) {

            if (!edge.getReturnSlot().testFollow(input.charAt(node.getRightExtent()))) continue;

            Descriptor descriptor = edge.addDescriptor(input, this, node);
            if (descriptor != null) {
                slot.getRuntime().scheduleDescriptor(descriptor);
            }
        }
    }

    public NonterminalNode getNonterminalNode(Input input, int j) {
		return poppedElements.getNonterminalNode(input, j);
	}

    public NonterminalNode getNonterminalNode(Input input) {
        return poppedElements.getNonterminalNode(input);
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
		return MurmurHash3.f2().apply(slot.hashCode(), inputIndex);
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
	
	public void createGSSEdge(Input input, BodyGrammarSlot returnSlot, GSSNode destination, NonPackedNode w, Environment env) {
		NewGSSEdgeImpl edge = new org.iguana.datadependent.gss.NewGSSEdgeImpl(returnSlot, w, destination, env);
		
		gssEdges.add(edge);
		returnSlot.getRuntime().gssEdgeAdded(edge);

		poppedElements.forEach(z -> {
			if (edge.getReturnSlot().testFollow(input.charAt(z.getRightExtent()))) {
				Descriptor descriptor = edge.addDescriptor(input, this, z);
				if (descriptor != null) {
                    returnSlot.getRuntime().scheduleDescriptor(descriptor);
				}
			}
		});
	}
	
}
