package org.jgll.parser;

import org.jgll.grammar.BodyGrammarSlot;
import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarSlot;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TerminalSymbolNode;
import org.jgll.util.Input;

public interface GLLParser {
	
	public NonterminalSymbolNode parse(Input input, Grammar grammar, String startSymbolName) throws ParseError;
	
	public void add(GrammarSlot label, GSSNode u, int inputIndex, SPPFNode w);

	public void pop(GSSNode u, int i, SPPFNode z);
	
	public GSSNode create(GrammarSlot L, GSSNode u, int i, SPPFNode w);
	
	public TerminalSymbolNode getNodeT(int label, int i);
	
	public SPPFNode getNodeP(BodyGrammarSlot slot, SPPFNode leftChild, SPPFNode rightChild);
	
	public boolean hasNextDescriptor();
	
	public Descriptor nextDescriptor();
	
	public void removeDescriptor(Descriptor descriptor);
	
	public void newParseError(GrammarSlot slot, int inputIndex);
	
}
