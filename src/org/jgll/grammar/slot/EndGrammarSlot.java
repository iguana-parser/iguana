package org.jgll.grammar.slot;

import java.util.Collections;
import java.util.Set;

import org.jgll.datadependent.env.Environment;
import org.jgll.grammar.condition.Condition;
import org.jgll.grammar.symbol.Position;
import org.jgll.parser.GLLParser;
import org.jgll.parser.gss.GSSNode;
import org.jgll.parser.gss.lookup.GSSNodeLookup;
import org.jgll.sppf.NonPackedNode;

public class EndGrammarSlot extends BodyGrammarSlot {
	
	protected final NonterminalGrammarSlot nonterminal;

	public EndGrammarSlot(int id, Position position, NonterminalGrammarSlot nonterminal, GSSNodeLookup nodeLookup, 
			String label, String variable, Set<Condition> conditions) {
		super(id, position, nodeLookup, label, variable, conditions);
		this.nonterminal = nonterminal;
	}

	@Override
	public void execute(GLLParser parser, GSSNode u, int i, NonPackedNode node) {
		if (nonterminal.test(i))
			parser.pop(u, i, node);
	}
	
	@Override
	public String getConstructorCode() {
		return new StringBuilder()
			.append("new EndGrammarSlot(")
			.append(position)
			.append("), slot" + nonterminal.getId() + ")")
			.toString();
	}
	
	public Object getObject() {
		return null;
	}
	
	public NonterminalGrammarSlot getNonterminal() {
		return nonterminal;
	}

	@Override
	public Set<Transition> getTransitions() {
		return Collections.emptySet();
	}

	@Override
	public boolean addTransition(Transition transition) {
		return false;
	}
	
	@Override
	public boolean isLast() {
		return true;
	}
	
	/**
	 * 
	 * Data-dependent GLL parsing
	 * 
	 */
	@Override
	public void execute(GLLParser parser, GSSNode u, int i, NonPackedNode node, Environment env) {
		if (nonterminal.test(i))
			parser.pop(u, i, node);
	}

}
