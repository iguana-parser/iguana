package org.jgll.lexer;

import dk.brics.automaton.RunAutomaton;

/**
 * A {@code GLLLexer} is a lexical analyzer that is used to analyze input
 * strings that are processed by a GLL parser. A {@code GLLParser} requests the
 * tokens that can be found at a given position in the input string and the
 * {@code GLLLexer} returns a list of all possible tokens starting at that
 * position.
 * 
 *
 * @author Maarten Manders
 * @author Ali Afroozeh
 * 
 */
public class GLLLexer {
	
	/**
	 * A mapping between the token names and their associatied lexical patterns.
	 */
	protected RunAutomaton[] patterns;

	/**
	 * Keeps a list of pattern which matched tokens should be checked against.
	 */
	protected  RunAutomaton[][] exclusions;
	
	/**
	 * 
	 */
	protected  RunAutomaton[][] followRestrictions;
	
	/**
	 * The input string used by this lexer.
	 */
	private String input;
	
	/**
	 * 
	 */
	private LexicalMatch[][] cache;
	
	public void setInput(String input) {
		this.input = input;
		cache = new LexicalMatch[input.length() + 1][];
	}

	/**
	 * Returns a list of tokens at a given input position.
	 * 
	 * @param inputIndex
	 *            the index in the input position for which patterns have to be
	 *            matched.
	 * @param expectedSet
	 *            a set of expected patterns to match at position {@code index}
	 *            in the input.
	 * @return the tokens that contains the index in {@code patterns} and the
	 *         length of the lexeme that matched the patterns in
	 *         {@code expectedSet}.
	 */
	public boolean test(int inputIndex, int...expectedSet) {
				
		for (int patternIndex : expectedSet) {
				
			if(patternIndex == -1) {
				if(inputIndex == input.length()) {
					return true;
				}
				
				continue;
			}

			if(cache[inputIndex] != null && cache[inputIndex][patternIndex] != null) {
				if(cache[inputIndex][patternIndex].isMatched()) {
					return true;
				}
				continue;
			}
			
			RunAutomaton r = patterns[patternIndex];
			
			if(r == null) {
				throw new RuntimeException("Pattern not found.");
			}
						
			int length = r.run(input, inputIndex);
			
			if (length == -1) {
				fail(inputIndex, patternIndex);
				continue;
			}

			String lexeme = input.substring(inputIndex, inputIndex + length);
			
			if(shouldBeExluded(inputIndex, patternIndex, lexeme)) {
				fail(inputIndex, patternIndex);
				continue;
			}
			
			if(isFollowRestricted(inputIndex, patternIndex, length)) {
				fail(inputIndex, patternIndex);
				continue;				
			}
			
			success(inputIndex, patternIndex, lexeme);
			return true;
		}
		
		return false;
	}
	
	private boolean shouldBeExluded(int inputIndex, int patternIndex, String lexeme) {
		for (RunAutomaton exclusionPattern : exclusions[patternIndex]) {
			if(exclusionPattern.run(lexeme)) {
				return true;
			}
		}		
		return false;
	}
	
	private boolean isFollowRestricted(int inputIndex, int patternIndex, int length) {
		for(RunAutomaton followRestriction : followRestrictions[patternIndex]) {
			if(followRestriction.run(input, inputIndex + length) > -1) {
				return true;
			}
		}
		
		return false;
	}
	
	private void fail(int inputIndex, int patternIndex) {
		if(cache[inputIndex] == null) {
			cache[inputIndex] = new LexicalMatch[patterns.length];
		}
		if(cache[inputIndex][patternIndex] != null) {
			return;
		}
		cache[inputIndex][patternIndex] = LexicalMatch.fail();
	}
	

	private void success(int inputIndex, int patternIndex, String lexeme) {
		if(cache[inputIndex] == null) {
			cache[inputIndex] = new LexicalMatch[patterns.length];
		}
		if(cache[inputIndex][patternIndex] != null) {
			return;
		}
		cache[inputIndex][patternIndex] = LexicalMatch.success(lexeme);
	}
	
	
	/**
	 * Returns the lexeme corresponding to the given start and end indices. 
	 * 
	 * @param start the start index of the lexeme to return.
	 *            
	 * @param end the end index of the lexeme to return.      
	 *      
	 * @return the substring located between the {@code start} and  {@code length} indices of the input.
	 * 
	 */
	public String getLexeme(int start, int end) {
		if(end < start) {
			throw new IllegalArgumentException("The end index (" + end + ") cannot be less than the start index (" + start + ")");
		}
		return input.substring(start, end);
	}


	/**
	 * Returns the token at the provided {@code index} for the provided pattern
	 * {@code name} If the pattern is not found at the position, null is
	 * returned.
	 */
	public String getToken(int inputIndex, int patternIndex) {
		
		if(cache[inputIndex] != null && cache[inputIndex][patternIndex] != null) {
			return cache[inputIndex][patternIndex].getLexeme();
		}

		RunAutomaton r = patterns[patternIndex];

		int length = r.run(input, inputIndex);
				
		if (length == -1) {
			fail(inputIndex, patternIndex);
			return null;
		}

		String lexeme = input.substring(inputIndex, inputIndex + length);
		
		if(shouldBeExluded(inputIndex, patternIndex, lexeme)) {
			fail(inputIndex, patternIndex);
			return null;
		}
		
		if(isFollowRestricted(inputIndex, patternIndex, length)) {
			fail(inputIndex, patternIndex);
			return null;				
		}

		success(inputIndex, patternIndex, lexeme);
		return lexeme;
	}
}
