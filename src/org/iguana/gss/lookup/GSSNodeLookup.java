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

package org.iguana.gss.lookup;

import iguana.utils.input.Input;
import org.iguana.grammar.slot.NonterminalGrammarSlot;
import org.iguana.gss.GSSNode;
import org.iguana.gss.GSSNodeData;
import org.iguana.result.Result;
import org.iguana.util.Configuration;

public interface GSSNodeLookup<T extends Result> {
	
	void get(int i, GSSNodeCreator<T> creator);

	GSSNode<T> get(int i);
	
	GSSNode<T> get(NonterminalGrammarSlot slot, int i);
	
	void reset(Input input);

	Iterable<GSSNode<T>> getNodes();

	int size();
	
	void get(int i, GSSNodeData<Object> data, GSSNodeCreator<T> creator);

	GSSNode<T> get(NonterminalGrammarSlot slot, int i, GSSNodeData<Object> data);

	void put(int i, GSSNode<T> gssNode);

	static <T extends Result> GSSNodeLookup<T> getNodeLookup() {
		Configuration config = Configuration.load();
		if (config.getGSSLookupImpl() == Configuration.LookupImpl.HASH_MAP) {
			if (config.getHashmapImpl() == Configuration.HashMapImpl.JAVA)
				return new JavaHashMapNodeLookup<>();
			else if (config.getHashmapImpl() == Configuration.HashMapImpl.INT_OPEN_ADDRESSING)
				return new IntOpenAddressingMap<>();
		}
		throw new RuntimeException("Cannot create GSS node lookup");
	}

	@FunctionalInterface
	interface GSSNodeCreator<T extends Result> {
		GSSNode<T> create(GSSNode<T> node);
	}
}
