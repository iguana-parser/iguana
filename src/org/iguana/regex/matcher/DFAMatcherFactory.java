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

import java.util.List;

import org.iguana.grammar.symbol.Character;
import org.iguana.grammar.symbol.CharacterRange;
import org.iguana.grammar.symbol.Terminal;
import org.iguana.regex.RegularExpression;
import org.iguana.regex.Sequence;

public class DFAMatcherFactory implements MatcherFactory {

    public Matcher getMatcher(RegularExpression regex) {
        
        if (regex instanceof Terminal)
            return getMatcher(((Terminal) regex).getRegularExpression());
        
        if (regex instanceof Sequence<?>)
            return sequenceMatcher((Sequence<?>) regex);
            
        if (regex instanceof Character)
            return characterMatcher((Character) regex);
        
        if (regex instanceof CharacterRange)
            return characterRangeMatcher((CharacterRange) regex);
            
        return createMatcher(regex);
    }
    
    public Matcher getBackwardsMatcher(RegularExpression regex) {
        
        if (regex instanceof Terminal)
            return getBackwardsMatcher(((Terminal) regex).getRegularExpression());
        
        if (regex instanceof Sequence<?>)
            return sequenceBackwardsMatcher((Sequence<?>) regex);
        
        if (regex instanceof Character)
            return characterBackwardsMatcher((Character) regex);
        
        if (regex instanceof CharacterRange)
            return characterRangeBackwardsMatcher((CharacterRange) regex);
        
        return createBackwardsMatcher(regex);
    }

    public static Matcher sequenceMatcher(Sequence<?> seq) {
        if (seq.isCharSequence()) {
            List<Character> characters = seq.asCharacters();
            return (input, i) -> {
                for (Character c : characters) {
                    if (c.getValue() != input.charAt(i++)) {
                        return -1;
                    }
                }
                return characters.size();
            };
        }
        return createMatcher(seq);
    }
    
    public static Matcher sequenceBackwardsMatcher(Sequence<?> seq) {
        if (seq.isCharSequence()) {
            List<Character> characters = seq.asCharacters();
            return (input, i) -> {
                if (i == 0) return -1;
                --i;
                for (Character c : characters) {
                    if (c.getValue() != input.charAt(i--)) {
                        return -1;
                    }
                }
                return characters.size();
            };
        }
        return createBackwardsMatcher(seq);
    }
    
    public static Matcher characterMatcher(Character c) {
        return (input, i) -> input.charAt(i) == c.getValue() ? 1 : -1;
    }
    
    public static Matcher characterBackwardsMatcher(Character c) {
        return (input, i) ->  i == 0 ? -1 : ( input.charAt(i - 1) == c.getValue() ? 1 : -1 );
    }
    
    public static Matcher characterRangeMatcher(CharacterRange range) {
        return (input, i) -> input.charAt(i) >= range.getStart() && input.charAt(i) <= range.getEnd() ? 1 : -1;
    }
    
    public static Matcher characterRangeBackwardsMatcher(CharacterRange range) {
        return (input, i) -> i == 0 ? -1 : ( input.charAt(i - 1) >= range.getStart() && input.charAt(i - 1) <= range.getEnd() ? 1 : -1 );
    }
    
    public static Matcher createMatcher(RegularExpression regex) {
        return new DFAMatcher(regex.getAutomaton());
    }
    
    public static Matcher createBackwardsMatcher(RegularExpression regex) {
        return new DFABackwardsMatcher(regex.getAutomaton());
    }
	
}
