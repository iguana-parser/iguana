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

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import iguana.utils.input.Input;
import org.iguana.util.generator.ConstructorCode;

/**
 * A GrammarSlot is a position immediately before or after
 * a symbol in the body of a production rule. 
 * Grammar slots, similar to LR items, are represented by  
 * For example, in the rule X ::= alpha . beta, the grammar 
 * slot, denoted by ., is after
 * alpha and before beta, where alpha are a list of grammar symbols.
 * 
 *
 * @author Ali Afroozeh
 *
 */
public interface GrammarSlot extends ConstructorCode {
	
	default Set<GrammarSlot> getReachableSlots() {
		return StreamSupport.stream(getTransitions().spliterator(), false).map(t -> t.destination()).collect(Collectors.toSet());
	}
	
	/**
	 * Corresponds to a grammar position A ::= B . \alpha 
	 */
	default boolean isFirst() { return false; }
	
	/**
	 * Corresponds to a grammar position after the last terminal or nonterminal symbol 
	 */
	default boolean isLast() { return false; }
	
	default boolean isEnd() { return false; }

	public void reset(Input input);
	
	public boolean addTransition(Transition transition);
	
	public Iterable<Transition> getTransitions();
	
	default GrammarSlot withId(int id) { return this; }
	
	public int getId();
	
}
