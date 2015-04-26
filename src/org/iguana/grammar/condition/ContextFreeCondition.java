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

package org.iguana.grammar.condition;

import static org.iguana.util.generator.GeneratorUtil.*;

import java.util.Arrays;
import java.util.List;

import org.iguana.grammar.symbol.Symbol;
import org.iguana.regex.Sequence;
import org.iguana.traversal.IConditionVisitor;

public class ContextFreeCondition extends Condition {
	
	private static final long serialVersionUID = 1L;
	
	private List<? extends Symbol> symbols;
	
	public ContextFreeCondition(ConditionType type, Sequence<?> group) {
		super(type);
		this.symbols = group.getSymbols();
	}
	
	public ContextFreeCondition(ConditionType type, List<? extends Symbol> symbols) {
		super(type);
		this.symbols = symbols;
	}
	
	@SafeVarargs
	public static <T extends Symbol> ContextFreeCondition notMatch(T...symbols) {
		return new ContextFreeCondition(ConditionType.NOT_MATCH, Arrays.asList(symbols));
	}
	
	public static <T extends Symbol> ContextFreeCondition notMatch(List<T> symbols) {
		return new ContextFreeCondition(ConditionType.NOT_MATCH, symbols);
	}
	
	@SafeVarargs
	public static <T extends Symbol> ContextFreeCondition match(T...symbols) {
		return new ContextFreeCondition(ConditionType.MATCH, Arrays.asList(symbols));
	}
	
	public static <T extends Symbol> ContextFreeCondition match(List<T> symbols) {
		return new ContextFreeCondition(ConditionType.MATCH, symbols);
	}
	
	@SafeVarargs
	public static <T extends Symbol> ContextFreeCondition notFollow(T...symbols) {
		return new ContextFreeCondition(ConditionType.NOT_FOLLOW, Arrays.asList(symbols));
	}
	
	public static <T extends Symbol> ContextFreeCondition notFollow(List<T> symbols) {
		return new ContextFreeCondition(ConditionType.NOT_FOLLOW, symbols);
	}
	
	@SafeVarargs
	public static <T extends Symbol> ContextFreeCondition follow(T...symbols) {
		return new ContextFreeCondition(ConditionType.FOLLOW, Arrays.asList(symbols));
	}
	
	public static <T extends Symbol> ContextFreeCondition follow(List<T> symbols) {
		return new ContextFreeCondition(ConditionType.FOLLOW, symbols);
	}

	@SafeVarargs
	public static <T extends Symbol> ContextFreeCondition notPrecede(T...symbols) {
		return new ContextFreeCondition(ConditionType.NOT_PRECEDE, Arrays.asList(symbols));
	}
	
	public static <T extends Symbol> ContextFreeCondition notPrecede(List<T> symbols) {
		return new ContextFreeCondition(ConditionType.NOT_PRECEDE, symbols);
	}
	
	@SafeVarargs
	public static <T extends Symbol> ContextFreeCondition precede(T...symbols) {
		return new ContextFreeCondition(ConditionType.PRECEDE, Arrays.asList(symbols));
	}
	
	public static <T extends Symbol> ContextFreeCondition precede(List<T> symbols) {
		return new ContextFreeCondition(ConditionType.PRECEDE, symbols);
	}
	
	public List<? extends Symbol> getSymbols() {
		return symbols;
	}
	
	@Override
	public String toString() {
		return type.toString() + " " + listToString(symbols);
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(this == obj) {
			return true;
		}
		
		if(!(obj instanceof ContextFreeCondition)) {
			return false;
		}
		
		ContextFreeCondition other = (ContextFreeCondition) obj;
		
		return type == other.type && symbols.equals(other.symbols);
	}

	@Override
	public String getConstructorCode() {
		throw new UnsupportedOperationException();
	}

	@Override
	public SlotAction getSlotAction() {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T accept(IConditionVisitor<T> visitor) {
		return visitor.visit(this);
	}
}
