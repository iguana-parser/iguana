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

import org.iguana.regex.automaton.Automaton;
import org.iguana.regex.visitor.RegularExpressionVisitor;
import org.iguana.regex.visitor.ToAutomatonRegexVisitor;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public interface RegularExpression extends Serializable {

	long serialVersionUID = 1L;

	boolean isNullable();

    Set<org.iguana.regex.CharRange> getLookaheads();

    Set<org.iguana.regex.CharRange> getLookbehinds();
	
	Set<org.iguana.regex.CharRange> getFirstSet();
	
	/**
	 * The set of characters (ranges) that cannot follow this regular expressions. 
	 */
	Set<CharRange> getNotFollowSet();
		
	int length();

    <T> T accept(RegularExpressionVisitor<T> visitor);
	
	default Automaton getAutomaton() {
		return accept(new ToAutomatonRegexVisitor());
	}

	default List<? extends RegularExpression> getChildren() {
		return Collections.emptyList();
	}

    RegexBuilder<? extends RegularExpression> copy();

	static <T> Comparator<T> lengthComparator() {
		return (T o1, T o2) -> {
			if (!(o1 instanceof RegularExpression) || !(o2 instanceof RegularExpression)) { return 0; }

			RegularExpression r1 = (RegularExpression) o1;
			RegularExpression r2 = (RegularExpression) o2;
			return r2.length() - r1.length();
		};
	}
}
