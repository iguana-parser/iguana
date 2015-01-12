package org.jgll.grammar.slot;

import java.util.Collections;
import java.util.Set;

import org.jgll.grammar.GrammarRegistry;
import org.jgll.grammar.condition.Condition;
import org.jgll.grammar.symbol.Position;
import org.jgll.parser.GLLParser;
import org.jgll.parser.gss.GSSNode;
import org.jgll.parser.gss.lookup.NodeLookup;
import org.jgll.sppf.NonPackedNode;

public class EndGrammarSlot extends BodyGrammarSlot {
	
	protected final NonterminalGrammarSlot nonterminal;

	public EndGrammarSlot(int id, Position position, NonterminalGrammarSlot nonterminal, NodeLookup nodeLookup, Set<Condition> conditions) {
		super(id, position, nodeLookup, conditions);
		this.nonterminal = nonterminal;
	}

	@Override
	public void execute(GLLParser parser, GSSNode u, int i, NonPackedNode node) {
		if (nonterminal.test(i))
			parser.pop(u, i, node);
	}
	
	@Override
	public String getConstructorCode(GrammarRegistry registry) {
		return new StringBuilder()
			.append("new EndGrammarSlot(")
			.append(position)
			.append("), slot" + registry.getId(nonterminal) + ")")
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
	
}
