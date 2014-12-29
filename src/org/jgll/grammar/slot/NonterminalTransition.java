package org.jgll.grammar.slot;

import java.util.Collections;
import java.util.Set;

import org.jgll.grammar.GrammarSlotRegistry;
import org.jgll.grammar.condition.Condition;
import org.jgll.parser.GLLParser;
import org.jgll.sppf.NonPackedNode;


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
	public void execute(GLLParser parser, int i, NonPackedNode node) {
		
		if (!slot.test(parser.getInput().charAt(i))) {
			parser.recordParseError(origin);
			return;
		}
		
		if (preConditions.stream().anyMatch(c -> c.getSlotAction().execute(parser.getInput(), parser.getCurrentGSSNode(), i)))
			return;
		
		NonterminalGrammarSlot nonterminal = parser.create(dest, slot);
		
		if (nonterminal != null) 
			nonterminal.execute(parser, i, node);
	}
	
	@Override
	public String getConstructorCode(GrammarSlotRegistry registry) {
		return new StringBuilder()
			.append("new NonterminalTransition(")
			.append("slot" + registry.getId(slot)).append(", ")
			.append("slot" + registry.getId(origin)).append(", ")
			.append("slot" + registry.getId(dest)).append(", ")
			.append(getConstructorCode(preConditions, registry))
			.toString();
	}

}
