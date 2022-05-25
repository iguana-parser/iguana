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

package org.iguana.grammar.runtime;

import org.iguana.grammar.symbol.Associativity;

import java.util.HashMap;
import java.util.Map;

import static org.iguana.utils.string.StringUtil.listToString;


public class AssociativityGroup {
	
	private final Associativity associativity;
	
	private final PrecedenceLevel precedenceLevel;
	
	private final Map<Integer, Associativity> map;
	
	private int precedence = -1;
	
	private int lhs = -1;
	
	private int rhs = -1;

	public AssociativityGroup(Associativity associativity, PrecedenceLevel precedenceLevel) {
		this.associativity = associativity;
		this.precedenceLevel = precedenceLevel;
		this.map = new HashMap<>();
		this.lhs = precedenceLevel.getLhs();
	}
	
	public AssociativityGroup(Associativity associativity, PrecedenceLevel precedenceLevel, int lhs, int rhs, int precedence) {
		this(associativity, precedenceLevel);
		this.precedence = precedence;
		this.lhs = lhs;
		this.rhs = rhs;
	}
	
	public Associativity getAssociativity() {
		return associativity;
	}
	
	public int getLhs() {
		return lhs;
	}
	
	public int getRhs() {
		return rhs;
	}
	
	public int getPrecedence() {
		return precedence;
	}
	
	public Map<Integer, Associativity> getAssocMap() {
		return map;
	}
	
	public AssociativityGroup add(int precedence, Associativity associativity) {
		
		if (precedence == this.precedence) return this;
		
		map.put(precedence, associativity);
		return this;
	}
		
	public int getPrecedence(RuntimeRule rule) {
		if (!(rule.isLeftOrRightRecursive() 
				|| rule.isILeftOrRightRecursive()))
			return -1;
		
		if (rule.getAssociativity() == associativity) {
			if (precedence != -1)
				return precedence;
			else {
				precedence = precedenceLevel.getPrecedence(rule);
				return precedence;
			}
		} 
		
		if (rule.getAssociativity() == Associativity.UNDEFINED) {
			if (precedence != -1) {
				
				if (rule.isUnary() && rule.isRightRecursive()) 
					precedenceLevel.setHasPrefixUnaryFromAssociativityGroup();
				if (rule.isUnary() && rule.isLeftRecursive()) 
					precedenceLevel.setHasPostfixUnaryFromAssociativityGroup();
				
				return precedence;
			} else {
				precedence = precedenceLevel.getPrecedenceFromAssociativityGroup(rule);
				return precedence;
			}
		}
		
		int precedence = precedenceLevel.getPrecedence(rule);
		map.put(precedence, rule.getAssociativity());
		return precedence;
	}
	
	public void done() {
		rhs = precedenceLevel.getCurrent();
	}
	
	@Override
	public String toString() {
		return associativity.name() + "(" 
					+ lhs + ","
					+ rhs + ","
					+ (precedence != -1? precedence + (map.keySet().isEmpty()? "" : ",") : "") 
					+ listToString(map.keySet(), ",") + ")";
	}

}
