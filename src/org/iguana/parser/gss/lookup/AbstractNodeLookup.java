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

package org.iguana.parser.gss.lookup;

import iguana.utils.input.Input;
import org.iguana.grammar.slot.NonterminalGrammarSlot;
import org.iguana.parser.descriptor.ResultOps;
import org.iguana.parser.gss.GSSNode;
import org.iguana.parser.gss.GSSNodeData;
import org.iguana.util.Tuple;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Anastasia Izmaylova
 *
 */

public abstract class AbstractNodeLookup<T> implements GSSNodeLookup<T> {
	
	/**
	 * 
	 * Data-dependent GLL parsing
	 * 
	 */
	protected Map<Tuple<Integer, GSSNodeData<?>>, GSSNode<T>> map = new HashMap<>();

	protected ResultOps<T> ops;

	public AbstractNodeLookup(ResultOps<T> ops) {
		this.ops = ops;
	}
	
	@Override
	public <V> void get(int i, GSSNodeData<V> data, GSSNodeCreator<T> creator) {
		map.compute(new Tuple<>(i, data), (k, v) -> creator.create(v));
	}
	
	@Override
	public void reset(Input input) {
		map = new HashMap<>();
	}
	
	@Override
	public <V> GSSNode<T> get(NonterminalGrammarSlot<T> slot, int i, GSSNodeData<V> data) {
		GSSNode<T> gssNode = map.get(new Tuple<>(i, data));
		if (gssNode == null)
			return new org.iguana.datadependent.gss.GSSNode<>(slot, i, data, ops);
		return gssNode;
	}
	
}
