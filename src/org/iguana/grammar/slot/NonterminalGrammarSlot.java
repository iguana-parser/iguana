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

package org.iguana.grammar.slot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.iguana.grammar.slot.lookahead.FollowTest;
import org.iguana.grammar.slot.lookahead.LookAheadTest;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.parser.gss.GSSNode;
import org.iguana.parser.gss.GSSNodeData;
import org.iguana.parser.gss.lookup.GSSNodeLookup;
import org.iguana.sppf.NonterminalNode;
import org.iguana.util.Input;
import org.iguana.util.collections.Key;


/**
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class NonterminalGrammarSlot extends AbstractGrammarSlot {
	
	private final Nonterminal nonterminal;
	
	private final List<BodyGrammarSlot> firstSlots;
	
	private final GSSNodeLookup nodeLookup;

	private Map<Key, NonterminalNode> nonterminalNodes;

	private LookAheadTest lookAheadTest;
	private FollowTest followTest;

	public NonterminalGrammarSlot(int id, Nonterminal nonterminal, GSSNodeLookup nodeLookup) {
		super(id);
		this.nonterminal = nonterminal;
		this.nodeLookup = nodeLookup;
		this.firstSlots = new ArrayList<>();
		this.nonterminalNodes = new HashMap<>();
	}
	
	public void addFirstSlot(BodyGrammarSlot slot) {
		firstSlots.add(slot);
	}
	
	public List<BodyGrammarSlot> getFirstSlots() {
		return firstSlots;
	}
	
	public List<BodyGrammarSlot> getFirstSlots(int v) {
		List<BodyGrammarSlot> lookaheadSlots = lookAheadTest.get(v);
		return lookaheadSlots == null ? firstSlots : lookaheadSlots;
	}
	
	public void setLookAheadTest(LookAheadTest lookAheadTest) {
		this.lookAheadTest = lookAheadTest;
	}

	public void setFollowTest(FollowTest followTest) {
		this.followTest = followTest;
	}
	
	public boolean testPredict(int v)  {
		return lookAheadTest.test(v);
	}
	
	public boolean testFollow(int v) {
		return followTest.test(v);
	}
	
	public Nonterminal getNonterminal() {
		return nonterminal;
	}
	
	public String[] getParameters() {
		return nonterminal.getParameters();
	}
	
	@Override
	public String toString() {
		return nonterminal.toString();
	}
	
	@Override
	public String getConstructorCode() {
		return new StringBuilder()
		           .append("new NonterminalGrammarSlot(")
		           .append(nonterminal.getConstructorCode())
		           .append(")").toString();
	}
	
	@Override
	public GSSNode getGSSNode(int inputIndex) {
		return nodeLookup.getOrElseCreate(this, inputIndex);
	}
	
	@Override
	public GSSNode hasGSSNode(int inputIndex) { 
		if (nodeLookup.isInitialized()) {
			return nodeLookup.get(inputIndex);
		} else {
			nodeLookup.init();
			return null;
		}
	}

	@Override
	public boolean isFirst() {
		return true;
	}
	
	public NonterminalNode getNonterminalNode(Key key, Supplier<NonterminalNode> s, Consumer<NonterminalNode> c) {
		return nonterminalNodes.computeIfAbsent(key, k -> { NonterminalNode val = s.get();
															c.accept(val);
															return val; 
														  });
	}
	
	public NonterminalNode findNonterminalNode(Key key) {
		return nonterminalNodes.get(key);
	}
	
	public Iterable<GSSNode> getGSSNodes() {
		return nodeLookup.getNodes();
	}

	@Override
	public void reset(Input input) {
		nodeLookup.reset(input);
		nonterminalNodes = new HashMap<>();
	}
	
	public void initGSSLookup() {
		nodeLookup.init();
	}

	/**
	 * 
	 * Data-dependent GLL parsing
	 * 
	 */
	@Override
	public <T> GSSNode getGSSNode(int inputIndex, GSSNodeData<T> data) {
		return nodeLookup.getOrElseCreate(this, inputIndex, data);
	}
	
	@Override
	public <T> GSSNode hasGSSNode(int inputIndex, GSSNodeData<T> data) {
		if (nodeLookup.isInitialized()) {
			return nodeLookup.get(inputIndex, data);
		} else {
			nodeLookup.init();
			return null;
		}
	}
}
