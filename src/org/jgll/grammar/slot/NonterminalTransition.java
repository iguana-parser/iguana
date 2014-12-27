package org.jgll.grammar.slot;

import java.util.Collections;
import java.util.Set;

import org.jgll.grammar.condition.Condition;
import org.jgll.parser.GLLParser;
import org.jgll.sppf.NonPackedNode;
import org.jgll.util.Input;


public class NonterminalTransition extends AbstractTransition {

	private final NonterminalGrammarSlot slot;
	
	private final Set<Condition> preConditions;

	public NonterminalTransition(NonterminalGrammarSlot slot, GrammarSlot origin, GrammarSlot dest) {
		this(slot, origin, dest, Collections.emptySet());
	}	
	
	public NonterminalTransition(NonterminalGrammarSlot slot, GrammarSlot origin,
			                     GrammarSlot dest, Set<Condition> preConditions) {
		super(origin, dest);
		this.slot = slot;
		this.preConditions = preConditions;
	}

	@Override
	public void execute(GLLParser parser, Input input, NonPackedNode node) {
		int ci = node.getRightExtent();
		
		if (!slot.test(input.charAt(ci))) {
			parser.recordParseError(origin);
			return;
		}
		
		if (preConditions.stream().anyMatch(c -> c.getSlotAction().execute(parser.getInput(), parser.getCurrentGSSNode(), ci)))
			return;
		
		NonterminalGrammarSlot nonterminal = parser.create(dest, slot);
		
		if (nonterminal != null) 
			nonterminal.execute(parser, input, node);
	}

}
