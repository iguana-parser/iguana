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

package org.iguana.grammar.symbol;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public class Position {
	
	private final Rule rule;
	
	private final int posInRule;
	
	private final int posInSymbol;
	
	public Position(Rule rule, int posInRule) {
		this(rule, posInRule, -1);
	}
	
	public Position(Rule rule, int posInRule, int posInSymbol) {
		this.rule = rule;
		this.posInRule = posInRule;
		this.posInSymbol = posInSymbol;
	}
	
	public Rule getRule() {
		return rule;
	}
	
	public int getPosition() {
		return posInRule;
	}
	
	public boolean isFirst() {
		return posInRule == 1;
	}
	
	public boolean isLast() {
		return posInRule == rule.size();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		
		if (!(obj instanceof Position))
			return false;
		
		Position other = (Position) obj;
		
		return rule.equals(other.rule) && posInRule == other.posInRule && posInSymbol == other.posInSymbol;
	}
	
	@Override
	public String toString() {
		
		if (posInSymbol == -1) 
			return "-";
		
		StringBuilder sb = new StringBuilder();
		sb.append(rule.getHead()).append(" ::= ");
		
		if (rule.size() == 0) { 
			sb.append(".");
		} else {
			int i;
			if (posInRule == 0 && posInSymbol == 0) {
				sb.append(" . ");
			}
			for (i = 0; i < rule.size(); i++) {
				if (i + 1 == posInRule) {
					sb.append(rule.symbolAt(i).toString(posInSymbol) + " ");
				} else {
					sb.append(rule.symbolAt(i) + " ");
				}
			}
			
		}
		
		return sb.toString().trim();
	}
}
