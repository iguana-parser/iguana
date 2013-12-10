package org.jgll.recognizer;

import org.jgll.grammar.Grammar;
import org.jgll.recognizer.lookup.HashTableLookup;

public class RecognizerFactory {

	public static GLLRecognizer contextFreeRecognizer(Grammar grammar) {
		return new InterpretedGLLRecognizer(new HashTableLookup(), grammar);
	}
	
	public static GLLRecognizer prefixContextFreeRecognizer(Grammar grammar) {
		return new PrefixGLLRecognizer(new HashTableLookup(), grammar);
	}
	
}
