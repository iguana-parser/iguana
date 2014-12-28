package org.jgll.grammar.slot;

import java.util.HashMap;
import java.util.Map;

import org.jgll.grammar.GrammarSlotRegistry;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.parser.gss.GSSNode;


public class NonterminalGrammarSlot extends BodyGrammarSlot {

	private final Nonterminal nonterminal;
	
	private Map<Integer, GSSNode> gssNodes;

	public NonterminalGrammarSlot(Nonterminal nonterminal) {
		this.nonterminal = nonterminal;
		this.gssNodes = new HashMap<>();
	}
	
	public boolean test(int v)  {
		return true;
	}
	
	public Nonterminal getNonterminal() {
		return nonterminal;
	}
	
	@Override
	public String toString() {
		return nonterminal.getName();
	}
	
	@Override
	public String getConstructorCode(GrammarSlotRegistry registry) {
		return new StringBuilder()
		           .append("new NonterminalGrammarSlot(")
		           .append(nonterminal.getConstructorCode(registry))
		           .append(")").toString();
	}
	
	@Override
	public GSSNode getGSSNode(int inputIndex) {
		return gssNodes.computeIfAbsent(inputIndex, k -> new GSSNode(this, inputIndex));
	}
	
	@Override
	public GSSNode hasGSSNode(int inputIndex) { 
		return gssNodes.get(inputIndex); 
	}

}
