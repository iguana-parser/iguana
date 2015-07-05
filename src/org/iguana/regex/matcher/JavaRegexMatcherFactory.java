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

package org.iguana.regex.matcher;

import java.util.HashMap;
import java.util.Map;

import org.iguana.grammar.symbol.Character;
import org.iguana.grammar.symbol.CharacterRange;
import org.iguana.grammar.symbol.Terminal;
import org.iguana.regex.RegularExpression;
import org.iguana.traversal.HasConditionsVisitor;

public class JavaRegexMatcherFactory implements MatcherFactory {

	private Map<RegularExpression, Matcher> cache = new HashMap<>();
	private Map<RegularExpression, Matcher> backwardsCache = new HashMap<>();
	
	@Override
	public Matcher getMatcher(RegularExpression regex) {
		Boolean hasConditions = regex.accept(new HasConditionsVisitor());
		
 		if (hasConditions) 
			return cache.computeIfAbsent(regex, JavaRegexMatcher::new);
		else 
			return cache.computeIfAbsent(regex, DFAMatcher::new);
	}

    public Matcher getBackwardsMatcher(RegularExpression regex) {
    	return backwardsCache.computeIfAbsent(regex, this::createBackwardsMatcher);
    }
    
    private Matcher createBackwardsMatcher(RegularExpression regex) {
        if (regex instanceof Terminal)
            return getBackwardsMatcher(((Terminal) regex).getRegularExpression());
        
        if (regex instanceof Character)
            return DFAMatcherFactory.characterBackwardsMatcher((Character) regex);
        
        if (regex instanceof CharacterRange)
            return DFAMatcherFactory.characterRangeBackwardsMatcher((CharacterRange) regex);
        
        return backwardsCache.computeIfAbsent(regex, DFABackwardsMatcher::new);
    }
}
