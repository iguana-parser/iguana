package org.jgll.grammar.slot;

import java.util.Collections;
import java.util.Set;

import org.jgll.grammar.GrammarSlotRegistry;
import org.jgll.grammar.condition.Condition;
import org.jgll.parser.GLLParser;
import org.jgll.parser.gss.GSSNode;
import org.jgll.sppf.NonPackedNode;
import org.jgll.util.logging.LoggerWrapper;


public class NonterminalTransition extends AbstractTransition {
	
	private static final LoggerWrapper log = LoggerWrapper.getLogger(NonterminalTransition.class);

	private final NonterminalGrammarSlot nonterminal;
	
	private final Set<Condition> preConditions;

	public NonterminalTransition(NonterminalGrammarSlot nonterminal, BodyGrammarSlot origin, BodyGrammarSlot dest) {
		this(nonterminal, origin, dest, Collections.emptySet());
	}	
	
	public NonterminalTransition(NonterminalGrammarSlot nonterminal, BodyGrammarSlot origin, BodyGrammarSlot dest, Set<Condition> preConditions) {
		super(origin, dest);
		this.nonterminal = nonterminal;
		this.preConditions = preConditions;
	}

	@Override
	public void execute(GLLParser parser, GSSNode u, int i, NonPackedNode node) {
		
		log.trace("Processing %s", this);
		
		if (!nonterminal.test(parser.getInput().charAt(i))) {
			parser.recordParseError(origin);
			return;
		}
		
		for (Condition c : preConditions) {
			if (c.getSlotAction().execute(parser.getInput(), u, i)) 
				return;
		}
		
		parser.create(dest, nonterminal, u, i, node);
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
