package org.jgll.grammar.slot;

import java.util.Collections;
import java.util.Set;

import org.jgll.grammar.GrammarSlotRegistry;
import org.jgll.grammar.symbol.Position;
import org.jgll.parser.GLLParser;
import org.jgll.parser.gss.GSSNode;
import org.jgll.sppf.NonPackedNode;

public class EndGrammarSlot extends BodyGrammarSlot {
	
	protected final NonterminalGrammarSlot nonterminal;

	public EndGrammarSlot(Position position, NonterminalGrammarSlot nonterminal) {
		super(position);
		this.nonterminal = nonterminal;
	}

	@Override
	public void execute(GLLParser parser, GSSNode u, int i, NonPackedNode node) {
		if (nonterminal.test(i))
			parser.pop(u, i, node);
	}
	
	@Override
	public String getConstructorCode(GrammarSlotRegistry registry) {
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
