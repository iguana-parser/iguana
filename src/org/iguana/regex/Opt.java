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

package org.iguana.regex;

import java.util.Collections;
import java.util.Set;

import org.iguana.grammar.symbol.AbstractRegularExpression;
import org.iguana.grammar.symbol.CharacterRange;
import org.iguana.grammar.symbol.Symbol;
import org.iguana.grammar.symbol.SymbolBuilder;
import org.iguana.regex.automaton.Automaton;
import org.iguana.regex.automaton.State;
import org.iguana.regex.automaton.StateType;
import org.iguana.traversal.ISymbolVisitor;

public class Opt extends AbstractRegularExpression {

	private static final long serialVersionUID = 1L;

	private final Symbol s;
	
	private Opt(Builder builder) {
		super(builder);
		this.s = builder.s;
	}

	public static Opt from(Symbol s) {
		return builder(s).build();
	}
	
	public Symbol getSymbol() {
		return s;
	}

	private static String getName(Symbol s) {
		return s.getName() + "?";
	}
	
	@Override
	public int length() {
		return ((RegularExpression) s).length();
	}
	
	@Override
	public Automaton createAutomaton() {
		State startState = new State();
		
		State finalState = new State(StateType.FINAL);

		Automaton automaton = ((RegularExpression) s).getAutomaton().copy();
		startState.addEpsilonTransition(automaton.getStartState());
		
		Set<State> finalStates = automaton.getFinalStates();
		for(State s : finalStates) {
			s.setStateType(StateType.NORMAL);
			s.addEpsilonTransition(finalState);			
		}
		
		startState.addEpsilonTransition(finalState);
		
		return Automaton.builder(startState).build();
	}

	@Override
	public boolean isNullable() {
		return true;
	}
	
	@Override
	public Set<CharacterRange> getFirstSet() {
		return ((RegularExpression) s).getFirstSet();
	}
	
	@Override
	public Set<CharacterRange> getNotFollowSet() {
		return Collections.emptySet();
	}

	@Override
	public String getConstructorCode() {
		return Opt.class.getSimpleName() + ".builder(" + s.getConstructorCode() + ")" + super.getConstructorCode() + ".build()";
	}
	
	@Override
	public SymbolBuilder<? extends Symbol> copyBuilder() {
		return new Builder(s);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		
		if (!(obj instanceof Opt))
			return false;
		
		Opt other = (Opt) obj;
		return s.equals(other.s);
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
	public static Builder builder(Symbol s) {
		return new Builder(s);
	}
	
	public static class Builder extends SymbolBuilder<Opt> {

		private Symbol s;

		public Builder(Symbol s) {
			super(getName(s));
			this.s = s;
		}
		
		public Builder(Opt opt) {
			super(opt);
			this.s = opt.s;
		}
		
		@Override
		public Opt build() {
			return new Opt(this);
		}
	}

	@Override
	public <T> T accept(ISymbolVisitor<T> visitor) {
		return visitor.visit(this);
	}
	
}
