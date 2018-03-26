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

import java.util.List;
import java.util.Objects;

import static iguana.utils.collections.CollectionsUtil.concat;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

public class ArrayNodeLookup<T> extends AbstractNodeLookup<T> {

	private GSSNode[] gssNodes;
	private int size;
	
	public ArrayNodeLookup(Input input, ResultOps<T> ops) {
		super(ops);
		gssNodes = new GSSNode[input.length()];
	}
	
	@Override
	public void reset(Input input) {
		super.reset(input);
		gssNodes = new GSSNode[input.length()];
		size = 0;
	}
	
	@Override
	public Iterable<GSSNode<T>> getNodes() {
		@SuppressWarnings("unchecked")
		List<GSSNode<T>> list = asList(this.gssNodes);
		return concat(list.stream().filter(Objects::nonNull).collect(toList()), super.map.values());
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public void put(int i, GSSNode<T> gssNode) {
		gssNodes[i] = gssNode;
		size++;
	}

	@Override
	public void get(int i, GSSNodeCreator<T> creator) {
		gssNodes[i] = creator.create(gssNodes[i]);
	}

	@Override
	public GSSNode<T> get(int i) {
		return gssNodes[i];
	}

	@Override
	public GSSNode<T> get(NonterminalGrammarSlot<T> slot, int i) {
		GSSNode<T> node = gssNodes[i];
		if (node == null) {
			node = new GSSNode<>(slot, i, ops);
			gssNodes[i] = node;
			return node;
		} 
		return node;
	}
}
