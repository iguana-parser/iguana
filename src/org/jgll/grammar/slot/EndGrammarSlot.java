package org.jgll.grammar.slot;

import java.util.Collections;
import java.util.Set;

import org.jgll.grammar.GrammarSlotRegistry;
import org.jgll.grammar.symbol.Position;
import org.jgll.parser.GLLParser;
import org.jgll.sppf.NonPackedNode;
import org.jgll.util.Input;

public class EndGrammarSlot extends BodyGrammarSlot {
	
	protected final NonterminalGrammarSlot nonterminal;

	public EndGrammarSlot(Position position, NonterminalGrammarSlot nonterminal) {
		super(position);
		this.nonterminal = nonterminal;
	}

	@Override
	public void execute(GLLParser parser, Input input, NonPackedNode node) {
		if (nonterminal.test(node.getRightExtent()))
			parser.pop();
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
