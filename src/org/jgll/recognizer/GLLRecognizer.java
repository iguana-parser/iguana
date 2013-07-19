package org.jgll.recognizer;

import org.jgll.grammar.BodyGrammarSlot;
import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarSlot;
import org.jgll.util.Input;

public interface GLLRecognizer {

	public boolean recognize(Input input, Grammar grammar, String nonterminalName);
	
	public boolean recognize(Input input, BodyGrammarSlot slot);
	
	public boolean recognizePrefix(Input input, BodyGrammarSlot slot);
		
	public void add(GrammarSlot label, GSSNode u, int inputIndex);

	public void pop(GSSNode u, int i);

	public GSSNode create(GrammarSlot L, GSSNode u, int i);
	
	public boolean hasNextDescriptor();
	
	public Descriptor nextDescriptor();
	
	public int getCi();
	
	public GSSNode getCu();
	
	public void update(GSSNode gssNode, int inputIndex);
	
	public void recognitionError(GSSNode gssNode, int inputIndex);
	
}
