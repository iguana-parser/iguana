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

import iguana.regex.RegularExpression;
import org.iguana.traversal.IConditionVisitor;

/**
 * Conditions relating to the keyword exclusions or follow restrictions. 
 * For example, Id !>> "[]" or Id \ "if"
 * 
 * @author Ali Afroozeh
 *
 */
public class RegularExpressionCondition extends Condition {
	
	private static final long serialVersionUID = 1L;
	
	private RegularExpression regularExpression;
	
	public RegularExpressionCondition(ConditionType type, RegularExpression regularExpression) {
		super(type);
		this.regularExpression = regularExpression;
	}

	@Override
	public String toString() {
		return type.toString() + " " + regularExpression;
	}
	
	public RegularExpression getRegularExpression() {
		return regularExpression;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(this == obj) 
			return true;
		
		if(!(obj instanceof RegularExpressionCondition)) 
			return false;
		
		RegularExpressionCondition other = (RegularExpressionCondition) obj;
		
		return type == other.type && regularExpression.equals(other.regularExpression);
	}
	
	@Override
	public int hashCode() {
		return type.hashCode() * 31 + regularExpression.hashCode();
	}

	public static RegularExpressionCondition notMatch(RegularExpression regularExpression) {
		return new RegularExpressionCondition(ConditionType.NOT_MATCH, regularExpression);
	}
	
	public static RegularExpressionCondition match(RegularExpression regularExpression) {
		return new RegularExpressionCondition(ConditionType.MATCH, regularExpression);
	}
	
	public static RegularExpressionCondition notFollow(RegularExpression regularExpression) {
		return new RegularExpressionCondition(ConditionType.NOT_FOLLOW, regularExpression);
	}
	
	public static RegularExpressionCondition followIgnoreLayout(RegularExpression regularExpression) {
		return new RegularExpressionCondition(ConditionType.FOLLOW_IGNORE_LAYOUT, regularExpression);
	}
	
	public static RegularExpressionCondition notFollowIgnoreLayout(RegularExpression regularExpression) {
		return new RegularExpressionCondition(ConditionType.NOT_FOLLOW_IGNORE_LAYOUT, regularExpression);
	}
	
	public static RegularExpressionCondition follow(RegularExpression regularExpression) {
		return new RegularExpressionCondition(ConditionType.FOLLOW, regularExpression);
	}
	
	public static RegularExpressionCondition precede(RegularExpression regularExpression) {
		return new RegularExpressionCondition(ConditionType.PRECEDE, regularExpression);
	}
	
	public static RegularExpressionCondition notPrecede(RegularExpression regularExpression) {
		return new RegularExpressionCondition(ConditionType.NOT_PRECEDE, regularExpression);
	}

	@Override
	public <T> T accept(IConditionVisitor<T> visitor) {
		return visitor.visit(this);
	}
	
}
