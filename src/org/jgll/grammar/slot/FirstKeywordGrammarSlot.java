package org.jgll.grammar.slot;

import org.jgll.grammar.HeadGrammarSlot;
import org.jgll.grammar.Keyword;

public class FirstKeywordGrammarSlot extends KeywordGrammarSlot {

	private static final long serialVersionUID = 1L;

	public FirstKeywordGrammarSlot(String label, HeadGrammarSlot keywordHead, Keyword keyword, HeadGrammarSlot head) {
		super(label, 0, keywordHead, keyword, null, head);
	}
	
}
