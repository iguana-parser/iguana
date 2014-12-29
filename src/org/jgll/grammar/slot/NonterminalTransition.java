package org.jgll.grammar.slot;

import java.util.Collections;
import java.util.Set;

import org.jgll.grammar.GrammarSlotRegistry;
import org.jgll.grammar.condition.Condition;
import org.jgll.parser.GLLParser;
import org.jgll.sppf.NonPackedNode;


public class NonterminalTransition extends AbstractTransition {

	private final NonterminalGrammarSlot nonterminal;
	
	private final Set<Condition> preConditions;

	public NonterminalTransition(NonterminalGrammarSlot nonterminal, GrammarSlot origin, GrammarSlot dest) {
		this(nonterminal, origin, dest, Collections.emptySet());
	}	
	
	public NonterminalTransition(NonterminalGrammarSlot nonterminal, GrammarSlot origin,
			                     GrammarSlot dest, Set<Condition> preConditions) {
		super(origin, dest);
		this.nonterminal = nonterminal;
		this.preConditions = preConditions;
	}

	@Override
	public void execute(GLLParser parser, int i, NonPackedNode node) {
		
		if (!nonterminal.test(parser.getInput().charAt(i))) {
			parser.recordParseError(origin);
			return;
		}
		
		if (preConditions.stream().anyMatch(c -> c.getSlotAction().execute(parser.getInput(), parser.getCurrentGSSNode(), i)))
			return;
		
		NonterminalGrammarSlot n = parser.create(dest, nonterminal, parser.getCurrentGSSNode(), i, node);
		
		if (n != null) 
			n.execute(parser, i, node);
	}
	
	@Override
	public String getConstructorCode(GrammarSlotRegistry registry) {
		return new StringBuilder()
			.append("new NonterminalTransition(")
			.append("slot" + registry.getId(nonterminal)).append(", ")
			.append("slot" + registry.getId(origin)).append(", ")
			.append("slot" + registry.getId(dest)).append(", ")
			.append(getConstructorCode(preConditions, registry))
			.toString();
	}

}
