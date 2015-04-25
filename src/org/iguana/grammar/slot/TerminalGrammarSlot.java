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

package org.iguana.grammar.slot;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.iguana.regex.RegularExpression;
import org.iguana.regex.matcher.Matcher;
import org.iguana.regex.matcher.MatcherFactory;
import org.iguana.sppf.TerminalNode;
import org.iguana.util.Input;
import org.iguana.util.collections.Key;


public class TerminalGrammarSlot extends AbstractGrammarSlot {
	
	private RegularExpression regex;
	private Matcher matcher;
	private Map<Key, TerminalNode> terminalNodes;

	public TerminalGrammarSlot(int id, RegularExpression regex) {
		super(id, Collections.emptyList());
		this.regex = regex;
		this.matcher = MatcherFactory.getMatcher(regex);
		this.terminalNodes = new HashMap<>();
	}

	@Override
	public String getConstructorCode() {
		return null;
	}

	public RegularExpression getRegularExpression() {
		return regex;
	}
	
	public int match(Input input, int i) {
		return matcher.match(input, i);
	}
	
	@Override
	public Set<Transition> getTransitions() {
		return Collections.emptySet();
	}

	@Override
	public boolean addTransition(Transition transition) {
		return false;
	}

	@Override
	public String toString() {
		return regex.toString();
	}
	
	public TerminalNode getTerminalNode(Key key, Supplier<TerminalNode> s, Consumer<TerminalNode> c) {
		return terminalNodes.computeIfAbsent(key, k -> { TerminalNode val = s.get();
														 c.accept(val);
														 return val; 
													   });
	}
	
	public TerminalNode findTerminalNode(Key key) {
		return terminalNodes.get(key);
	}

	@Override
	public void reset(Input input) {
		terminalNodes = new HashMap<>();
	}

}
