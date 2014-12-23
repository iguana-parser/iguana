package org.jgll.grammar.slotnew;

import java.util.Set;

import org.jgll.grammar.condition.Condition;
import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.grammar.slot.nodecreator.NodeCreator;
import org.jgll.parser.GLLParser;
import org.jgll.sppf.NonPackedNode;
import org.jgll.util.Input;


public class NonterminalTransition extends AbstractTransition {

	private NonterminalGrammarSlot slot;

	public NonterminalTransition(NonterminalGrammarSlot slot, GrammarSlot origin, GrammarSlot dest, Set<Condition> preConditions, 
								 Set<Condition> postConditions, NodeCreator nodeCreator) {
		super(origin, dest, preConditions, postConditions, nodeCreator);
		this.slot = slot;
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
		
		parser.create(next, nonterminal).execute(parser, input, node);
	}

}
