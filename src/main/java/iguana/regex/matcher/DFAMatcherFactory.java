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

package iguana.regex.matcher;

import iguana.regex.Char;
import iguana.regex.CharRange;
import iguana.regex.Epsilon;
import iguana.regex.RegularExpression;

import java.util.HashMap;
import java.util.Map;

public class DFAMatcherFactory implements MatcherFactory {
	
	private Map<RegularExpression, Matcher> cache = new HashMap<>();

    public Matcher getMatcher(RegularExpression regex) {
        
    	if (regex == Epsilon.getInstance())
    		return epsilonMatcher();
        
        if (regex instanceof Char)
            return characterMatcher((Char) regex);
        
        if (regex instanceof CharRange)
            return characterRangeMatcher((CharRange) regex);
            
        return cache.computeIfAbsent(regex, DFAMatcher::new);
    }
    
    public Matcher getBackwardsMatcher(RegularExpression regex) {
        
        if (regex instanceof Char)
            return characterBackwardsMatcher((Char) regex);
        
        if (regex instanceof CharRange)
            return characterRangeBackwardsMatcher((CharRange) regex);
        
        return cache.computeIfAbsent(regex, DFABackwardsMatcher::new);
    }
    
    public static Matcher characterMatcher(Char c) {
        return (input, i) -> input.charAt(i) == c.getValue() ? 1 : -1;
    }
    
    public static Matcher characterBackwardsMatcher(Char c) {
        return (input, i) ->  i == 0 ? -1 : ( input.charAt(i - 1) == c.getValue() ? 1 : -1 );
    }
    
    public static Matcher characterRangeMatcher(CharRange range) {
        return (input, i) -> input.charAt(i) >= range.getStart() && input.charAt(i) <= range.getEnd() ? 1 : -1;
    }
    
    public static Matcher characterRangeBackwardsMatcher(CharRange range) {
        return (input, i) -> i == 0 ? -1 : ( input.charAt(i - 1) >= range.getStart() && input.charAt(i - 1) <= range.getEnd() ? 1 : -1 );
    }
    
    public static Matcher epsilonMatcher() {
    	return (input, i) -> 0;
    }
	
}
