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

import java.io.Serializable;
import java.util.Arrays;

import org.iguana.util.generator.GeneratorUtil;

public class PrecedenceLevel implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private final int lhs;
	private int rhs = -1;
	
	private boolean hasPrefixUnary = false;
	private boolean hasPrefixUnaryBelow = false;
	
	private boolean hasPostfixUnary = false;
	private boolean hasPostfixUnaryBelow = false;
	
	public Integer[] prefixUnaryBelow = new Integer[0];
	public Integer[] postfixUnaryBelow = new Integer[0];
	
	private int undefined = -1;
	
	private int index;
	
	private boolean containsAssociativityGroup = false;
	private int assoc_lhs = -1;
	private int assoc_rhs = -1;
	
	PrecedenceLevel(int lhs) {
		this.lhs = lhs;
		this.index = lhs;
	}
	
	public static PrecedenceLevel from(int lhs, int rhs, int undefined, boolean hasPrefixUnary, boolean hasPostfixUnary, 
			   						   boolean hasPrefixUnaryBelow, boolean hasPostfixUnaryBelow) {
		PrecedenceLevel level = new PrecedenceLevel(lhs);
		level.rhs = rhs;
		level.undefined = undefined;
		level.hasPrefixUnary = hasPrefixUnary;
		level.hasPostfixUnary = hasPostfixUnary;
		level.hasPrefixUnaryBelow = hasPrefixUnaryBelow;
		level.hasPostfixUnaryBelow = hasPostfixUnaryBelow;
		return level;
	}
	
	public static PrecedenceLevel from(int lhs, int rhs, int undefined, boolean hasPrefixUnary, boolean hasPostfixUnary, 
									   boolean hasPrefixUnaryBelow, Integer[] prefixUnaryBelow, boolean hasPostfixUnaryBelow, Integer[] postfixUnaryBelow) {
		PrecedenceLevel level = new PrecedenceLevel(lhs);
		level.rhs = rhs;
		level.undefined = undefined;
		level.hasPrefixUnary = hasPrefixUnary;
		level.hasPostfixUnary = hasPostfixUnary;
		level.hasPrefixUnaryBelow = hasPrefixUnaryBelow;
		level.prefixUnaryBelow = prefixUnaryBelow;
		level.hasPostfixUnaryBelow = hasPostfixUnaryBelow;
		level.postfixUnaryBelow = postfixUnaryBelow;
		return level;
	}
	
	public static PrecedenceLevel getFirst() {
		return new PrecedenceLevel(1);
	}
	
	public static PrecedenceLevel getFirstAndDone() {
		PrecedenceLevel level = new PrecedenceLevel(1);
		level.done();
		return level;
	}
	
	public PrecedenceLevel getNext() {
		
		this.done();
		
		PrecedenceLevel next = new PrecedenceLevel(index);
		
		next.hasPrefixUnaryBelow = hasPrefixUnary || hasPrefixUnaryBelow;
		next.hasPostfixUnaryBelow = hasPostfixUnary || hasPostfixUnaryBelow;
		
		next.prefixUnaryBelow = Arrays.copyOf(prefixUnaryBelow, (hasPrefixUnary? 1 : 0) + prefixUnaryBelow.length);
		next.postfixUnaryBelow = Arrays.copyOf(postfixUnaryBelow, (hasPostfixUnary? 1 : 0) + postfixUnaryBelow.length);
		
		if (hasPrefixUnary)
			next.prefixUnaryBelow[next.prefixUnaryBelow.length - 1] = rhs;
		
		if (hasPostfixUnary)
			next.postfixUnaryBelow[next.postfixUnaryBelow.length - 1] = rhs;
		
		return next;
	}
	
	public int getLhs() {
		return lhs;
	}
	
	public int getRhs() {
		return rhs;
	}
	
	public boolean hasPrefixUnary() {
		return hasPrefixUnary;
	}
	
	public boolean hasPostfixUnary() {
		return hasPostfixUnary;
	}
	
	public boolean hasPrefixUnaryBelow() {
		return hasPrefixUnaryBelow;
	}
	
	public boolean hasPostfixUnaryBelow() {
		return hasPostfixUnaryBelow;
	}
	
	public int getPrecedence(Rule rule) {
		
		if (rule.isUnary() && rule.isRightRecursive()) hasPrefixUnary = true;
		if (rule.isUnary() && rule.isLeftRecursive()) hasPostfixUnary = true;
		
		if (!(rule.isLeftOrRightRecursive() 
				|| rule.isiLeftOrRightRecursive())) return -1;
		else if (rule.getAssociativity() == Associativity.UNDEFINED) {
			if (undefined == -1)
				undefined = index++;
			return undefined;
		} else
			return index++;
	}
	
	int getPrecedenceFromAssociativityGroup(Rule rule) {
		if (rule.isUnary() && rule.isRightRecursive()) hasPrefixUnary = true;
		if (rule.isUnary() && rule.isLeftRecursive()) hasPostfixUnary = true;
		
		if (!(rule.isLeftOrRightRecursive() 
				|| rule.isiLeftOrRightRecursive())) return -1;
		else return index++;
	}
	
	void setHasPrefixUnaryFromAssociativityGroup() {
		this.hasPrefixUnary = true;
	}
	
	void setHasPostfixUnaryFromAssociativityGroup() {
		this.hasPostfixUnary = true;
	}
	
	public void setUndefinedIfNeeded() {
		if (undefined == -1) {
			int rhs = index == lhs? index : index - 1;
			if (lhs != rhs && !(containsAssociativityGroup && lhs == assoc_lhs && rhs == assoc_rhs)) 
				undefined = index++;
		} 
	}
	
	public boolean isUndefined(int precedence) {
		return this.undefined != -1 && this.undefined == precedence;
	}
	
	public int getUndefined() {
		if (lhs == 1)
			return 0;
		return undefined;
	}
	
	public void done() {
		assert rhs != -1;
		rhs = index == lhs? index : index - 1;
	}
	
	int getCurrent() {
		return index == lhs? index : index - 1;
	}
	
	public void containsAssociativityGroup(int l, int r) {
		this.containsAssociativityGroup = true;
		this.assoc_lhs = l;
		this.assoc_rhs = r;
	}
		
	public String getConstructorCode() {
		return getClass().getSimpleName() + ".from(" + lhs + "," + rhs + "," + undefined + "," + hasPrefixUnary + "," + hasPostfixUnary + "," 
												     + hasPrefixUnaryBelow + "," + "new Integer[]{" + GeneratorUtil.listToString(prefixUnaryBelow, ",") + "}" + "," 
												     + hasPostfixUnaryBelow + "," + "new Integer[]{" + GeneratorUtil.listToString(postfixUnaryBelow, ",") + "}" + ")";
	}
	
	@Override
	public String toString() {
		return "PREC(" + lhs + "," + rhs + ")";
	}
}
