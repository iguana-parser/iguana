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

package org.iguana.parser.lookup;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

import org.iguana.datadependent.env.Environment;
import org.iguana.datadependent.util.collections.IntKey4PlusObject;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.slot.GrammarSlot;
import org.iguana.parser.descriptor.Descriptor;
import org.iguana.parser.gss.GSSNode;
import org.iguana.sppf.NonPackedNode;
import org.iguana.util.Input;
import org.iguana.util.collections.IntKey4;
import org.iguana.util.collections.Key;
import org.iguana.util.hashing.hashfunction.IntHash4;
import org.iguana.util.hashing.hashfunction.IntHash5;


/**
 * 
 * @author Ali Afroozeh
 *
 */
public class GlobalDescriptorLookupImpl implements DescriptorLookup {

	private final Set<Key> set;
	private final Deque<Descriptor> descriptorsStack;
	
	private final IntHash4 f;
	
	private final IntHash5 f5;
	
	public GlobalDescriptorLookupImpl(Input input, Grammar grammar) {
		int inputSize = input.length() + 1; 
		int grammarSize = grammar.size() + 1;
		this.f = (x, y, z, w) -> x * inputSize * grammarSize * inputSize +
								 y * grammarSize * inputSize +
								 z * inputSize +
								 w;
		
		this.f5 = (x, y, z, w, v) -> x * grammarSize * inputSize * grammarSize * inputSize +
		                             y * inputSize * grammarSize * inputSize +
									 z * grammarSize * inputSize +
									 w * inputSize +
									 v;
		
		set = new HashSet<>();
		descriptorsStack = new ArrayDeque<>();
	}
	
	@Override
	public boolean hasNextDescriptor() {
		return !descriptorsStack.isEmpty();
	}

	@Override
	public Descriptor nextDescriptor() {
		return descriptorsStack.pop();
	}
	
	@Override
	public boolean addDescriptor(GrammarSlot slot, GSSNode gssNode, int inputIndex, NonPackedNode sppfNode) {
		return !set.add(IntKey4.from(slot.getId(), inputIndex, gssNode.getGrammarSlot().getId(), gssNode.getInputIndex(), f));
	}

	@Override
	public void scheduleDescriptor(Descriptor descriptor) {
		descriptorsStack.push(descriptor);
	}

	@Override
	public boolean addDescriptor(GrammarSlot slot, GSSNode gssNode, int inputIndex, NonPackedNode sppfNode, Environment env) {
		return !set.add(IntKey4PlusObject.from(env, slot.getId(), inputIndex, gssNode.getGrammarSlot().getId(), gssNode.getInputIndex(), f5));
	}
	
}
