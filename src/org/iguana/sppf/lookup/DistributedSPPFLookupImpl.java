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

package org.iguana.sppf.lookup;

import org.iguana.datadependent.env.Environment;
import org.iguana.datadependent.util.collections.IntKey2PlusObject;
import org.iguana.grammar.slot.BodyGrammarSlot;
import org.iguana.grammar.slot.NonterminalGrammarSlot;
import org.iguana.parser.gss.GSSNodeData;
import org.iguana.sppf.IntermediateNode;
import org.iguana.sppf.NonterminalNode;
import org.iguana.util.Input;
import org.iguana.util.Tuple;
import org.iguana.util.collections.IntKey2;
import org.iguana.util.hashing.hashfunction.IntHash2;
import org.iguana.util.hashing.hashfunction.IntHash3;

public class DistributedSPPFLookupImpl extends AbstractSPPFLookup {
	
	private final IntHash2 f;
	private final IntHash3 f3;

	public DistributedSPPFLookupImpl(Input input) {
		super(input);
		int inputSize = input.length() + 1;
		this.f = (x, y) -> x * inputSize + y;
		this.f3 = (x, y, z) -> x * inputSize * inputSize + y * inputSize + z;
	}
		
	@Override
	public NonterminalNode hasNonterminalNode(NonterminalGrammarSlot slot, int leftExtent, int rightExtent) {
		return slot.findNonterminalNode(IntKey2.from(leftExtent, rightExtent, f));
	}

	@Override
	public NonterminalNode getNonterminalNode(NonterminalGrammarSlot slot, int leftExtent, int rightExtent) {
		return slot.getNonterminalNode(IntKey2.from(leftExtent, rightExtent, f), 
									   () -> createNonterminalNode(slot, leftExtent, rightExtent),
									   this::nonterminalNodeAdded);
	}
	
	@Override
	public IntermediateNode hasIntermediateNode(BodyGrammarSlot slot, int leftExtent, int rightExtent) {
		return slot.findIntermediateNode(IntKey2.from(leftExtent, rightExtent, f));
	}

	@Override
	public IntermediateNode getIntermediateNode(BodyGrammarSlot slot, int leftExtent, int rightExtent, NodeCreator<IntermediateNode> creator) {
		return slot.getIntermediateNode(IntKey2.from(leftExtent, rightExtent, f), creator);
	}

	@Override
	public NonterminalNode getStartSymbol(NonterminalGrammarSlot startSymbol, int inputSize) {
		return startSymbol.findNonterminalNode(IntKey2.from(0, inputSize - 1, f));
	}
	
	@Override
	public <T> NonterminalNode hasNonterminalNode(NonterminalGrammarSlot slot, int leftExtent, int rightExtent, GSSNodeData<T> data, Object value) {
		return slot.findNonterminalNode(IntKey2PlusObject.from(Tuple.of(data, value), leftExtent, rightExtent, f3));
	}

	@Override
	public <T> NonterminalNode getNonterminalNode(NonterminalGrammarSlot slot, int leftExtent, int rightExtent, GSSNodeData<T> data, Object value) {
		return slot.getNonterminalNode(IntKey2PlusObject.from(Tuple.of(data, value), leftExtent, rightExtent, f3), 
									   () -> createNonterminalNode(slot, leftExtent, rightExtent, value), 
									   this::nonterminalNodeAdded);
	}

	@Override
	public IntermediateNode hasIntermediateNode(BodyGrammarSlot slot, int leftExtent, int rightExtent, Environment env) {
		return slot.findIntermediateNode(IntKey2PlusObject.from(env, leftExtent, rightExtent, f3));
	}
	
	@Override
	public IntermediateNode getIntermediateNode(BodyGrammarSlot slot, int leftExtent, int rightExtent, Environment env, NodeCreator<IntermediateNode> creator) {
		return slot.getIntermediateNode(IntKey2PlusObject.from(env, leftExtent, rightExtent, f3), creator);
	}

}
