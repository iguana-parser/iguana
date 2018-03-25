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
import org.iguana.parser.descriptor.ResultOps;
import org.iguana.util.ParserLogger;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ali Afroozeh
 * @author Anastasia Izmaylova
 * 
 */
public class GSSNode<T> {
	
	protected final NonterminalGrammarSlot<T> slot;

	private final int inputIndex;
	
	private final PoppedElements<T> poppedElements;
	
	private final List<GSSEdge<T>> gssEdges;

	public GSSNode(NonterminalGrammarSlot<T> slot, int inputIndex, ResultOps<T> ops) {
		this.slot = slot;
		this.inputIndex = inputIndex;
		this.poppedElements = new PoppedElements<>(ops);
		this.gssEdges = new ArrayList<>();
	}
	
	public void createGSSEdge(Input input, BodyGrammarSlot<T> returnSlot, GSSNode<T> destination, T w, ResultOps<T> ops) {
		GSSEdge<T> edge = new NewGSSEdgeImpl<>(returnSlot, w, destination);
		ParserLogger.getInstance().gssEdgeAdded(edge);
		
		gssEdges.add(edge);
		
		poppedElements.forEach(z -> {
			if (edge.getReturnSlot().testFollow(input.charAt(ops.getRightIndex(z)))) {
				Descriptor<T> descriptor = edge.addDescriptor(input, this, z, ops);
				if (descriptor != null) {
					slot.getRuntime().scheduleDescriptor(descriptor);
				}
			}
		});
	}

    public void pop(Input input, EndGrammarSlot slot, T child, ResultOps<T> ops) {
		ParserLogger.getInstance().log("Pop %s, %d, %s", this, inputIndex, child);
        T node = poppedElements.add(slot, child);
        if (node != null) iterateOverEdges(input, node, ops);
    }

    public void pop(Input input, EndGrammarSlot slot, T child, Object value, ResultOps<T> ops) {
        T node = poppedElements.add(slot, child, value);
        if (node != null) iterateOverEdges(input, node, ops);
    }

    private void iterateOverEdges(Input input, T node, ResultOps<T> ops) {
        for(GSSEdge<T> edge : getGSSEdges()) {

            if (!edge.getReturnSlot().testFollow(input.charAt(ops.getRightIndex(node)))) continue;

            Descriptor<T> descriptor = edge.addDescriptor(input, this, node, ops);
            if (descriptor != null) {
                slot.getRuntime().scheduleDescriptor(descriptor);
            }
        }
    }

    public T getResult(int j) {
		return poppedElements.getResult(j);
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
		
	public Iterable<GSSEdge<T>> getGSSEdges() {
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
	
	/*
	 * 
	 * Data-dependent GLL parsing
	 * 
	 */
	
	public void createGSSEdge(Input input, BodyGrammarSlot<T> returnSlot, GSSNode<T> destination, T w, Environment env, ResultOps<T> ops) {
		GSSEdge<T> edge = new org.iguana.datadependent.gss.NewGSSEdgeImpl<>(returnSlot, w, destination, env);
		
		gssEdges.add(edge);
		ParserLogger.getInstance().gssEdgeAdded(edge);

		poppedElements.forEach(z -> {
			if (edge.getReturnSlot().testFollow(input.charAt(ops.getRightIndex(z)))) {
				Descriptor<T> descriptor = edge.addDescriptor(input, this, z, ops);
				if (descriptor != null) {
                    returnSlot.getRuntime().scheduleDescriptor(descriptor);
				}
			}
		});
	}
	
}
