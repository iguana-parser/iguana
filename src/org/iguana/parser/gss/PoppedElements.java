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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

import iguana.parsetrees.sppf.*;
import iguana.utils.collections.Keys;
import iguana.utils.input.Input;
import org.iguana.grammar.slot.EndGrammarSlot;
import org.iguana.parser.GLLParser;
import org.iguana.util.Holder;
import iguana.utils.collections.key.Key;

import static iguana.parsetrees.sppf.SPPFNodeFactory.*;

public class PoppedElements {

	private NonterminalNode firstNode;

    private Input firstInput;
	
	private Map<Object, NonterminalNode> poppedElements;
	
	public NonterminalNode add(GLLParser parser, Input input, EndGrammarSlot slot, NonPackedNode child) {

		// No node added yet
		if (firstNode == null) {
			firstNode = createNonterminalNode(slot.getNonterminal(), slot, child, input);
            firstInput = input;
            parser.packedNodeAdded(slot, child.getRightExtent());
			parser.nonterminalNodeAdded(firstNode);
			return firstNode;
		// Only one node is added and there is an ambiguity
		} else if (poppedElements == null && firstNode.getRightExtent() == child.getRightExtent() && firstInput.equals(input)) {
            boolean ambiguous = firstNode.addPackedNode(slot, child);
            parser.packedNodeAdded(slot, child.getRightExtent());
            if (ambiguous) parser.ambiguousNodeAdded(firstNode);
			return null;
        // Only one node is added and the node is not in the map
		} else {
			// Initialize the map and put the firstNode element there
			if (poppedElements == null) {
				poppedElements = new LinkedHashMap<>();
				poppedElements.put(Keys.from(firstNode.getRightExtent(), firstInput), firstNode);
			}
			
			Holder<NonterminalNode> holder = new Holder<>();
			poppedElements.compute(Keys.from(child.getRightExtent(), input), (k, v) -> {
                if (v == null) {
                    NonterminalNode node = createNonterminalNode(slot.getNonterminal(), slot, child, input);
                    parser.nonterminalNodeAdded(node);
                    parser.packedNodeAdded(slot, child.getRightExtent());
                    holder.set(node);
                    return node;
                } else {
                    boolean ambiguous = v.addPackedNode(slot, child);
                    parser.packedNodeAdded(slot, child.getRightExtent());
                    if (ambiguous) parser.ambiguousNodeAdded(v);
                    return v;
                }
            });

	 		return holder.get();
		}
	}
	
	public NonterminalNode add(GLLParser parser, Input input, EndGrammarSlot slot, NonPackedNode child, Object value) {
		// No node added yet
		if (firstNode == null) {
            firstNode = createNonterminalNode(slot.getNonterminal(), slot, child, value, input);
            parser.nonterminalNodeAdded(firstNode);
            parser.packedNodeAdded(slot, child.getRightExtent());
            return firstNode;
        } else {
            Key key = Keys.from(child.getRightExtent(), value);
            // Only one node is added and there is an ambiguity
            if (poppedElements == null && Keys.from(firstNode.getRightExtent(), firstNode.getValue()).equals(key)) {
            boolean ambiguous = firstNode.addPackedNode(slot, child);
            parser.packedNodeAdded(slot, child.getRightExtent());
            if (ambiguous) parser.ambiguousNodeAdded(firstNode);
            return null;
        } else {
            // Initialize the map and put the firstNode element there
            if (poppedElements == null) {
                poppedElements = new HashMap<>();
                poppedElements.put(Keys.from(firstNode.getRightExtent(), firstNode.getValue()), firstNode);
            }

            Holder<NonterminalNode> holder = new Holder<>();
            poppedElements.compute(key, (k, v) -> {
                if (v == null) {
                    NonterminalNode node = createNonterminalNode(slot.getNonterminal(), slot, child, value, input);
                    parser.nonterminalNodeAdded(node);
                    parser.packedNodeAdded(slot, child.getLeftExtent());
                    holder.set(node);
                    return node;
                }
                else {
                    boolean ambiguous = v.addPackedNode(slot, child);
                    parser.packedNodeAdded(slot, child.getRightExtent());
                    if (ambiguous) parser.ambiguousNodeAdded(v);
                    return v;
                }
            });
            return holder.get();
            }
        }
	}
	
	public void forEach(Consumer<NonterminalNode> c) {
		if (poppedElements == null) {
			if (firstNode != null) c.accept(firstNode);
		} else {
			poppedElements.values().forEach(c);
		}
	}
	
	public NonterminalNode getNonterminalNode(Input input, int j) {
		if (poppedElements == null) {
			if (firstNode != null && firstNode.getRightExtent() == j && firstInput == input)
				return firstNode;
			else 
				return null;
		}
		return poppedElements.get(Keys.from(j, input));
	}
	
	public int size() {
		if (poppedElements == null) {
			return firstNode != null ? 1 : 0;
		} else {
			return poppedElements.size();
		}
	}
}
