package org.jgll.grammar.slot;

import java.util.Collections;
import java.util.Set;

import org.jgll.datadependent.ast.Expression;
import org.jgll.datadependent.env.Environment;
import org.jgll.grammar.GrammarRegistry;
import org.jgll.grammar.condition.Condition;
import org.jgll.parser.GLLParser;
import org.jgll.parser.gss.GSSNode;
import org.jgll.sppf.NonPackedNode;


public class NonterminalTransition extends AbstractTransition {
	
	private final NonterminalGrammarSlot nonterminal;
	
	private final Set<Condition> preConditions;
	
	private final String label;
	
	private final String variable;
	
	private final Expression[] arguments;

	public NonterminalTransition(NonterminalGrammarSlot nonterminal, BodyGrammarSlot origin, BodyGrammarSlot dest) {
		this(nonterminal, origin, dest, null, null, new Expression[0], Collections.emptySet());
	}	
	
	public NonterminalTransition(NonterminalGrammarSlot nonterminal, BodyGrammarSlot origin, BodyGrammarSlot dest, 
			String label, String variable, Expression[] arguments, Set<Condition> preConditions) {
		super(origin, dest);
		this.nonterminal = nonterminal;
		this.preConditions = preConditions;
		this.label = label;
		this.variable = variable;
		this.arguments = arguments;
	}

	@Override
	public void execute(GLLParser parser, GSSNode u, int i, NonPackedNode node, Environment env) {
		
		if (!nonterminal.test(parser.getInput().charAt(i))) {
			parser.recordParseError(origin);
			return;
		}
		
		for (Condition c : preConditions) {
			if (c.getSlotAction().execute(parser.getInput(), u, i, env)) 
				return;
		}
				
		parser.create(dest, nonterminal, u, i, node, arguments, env);
	}
	
	public String getLabel() {
		return label;
	}
	
	public String getVariable() {
		return variable;
	}
	
	@Override
	public String getConstructorCode(GrammarRegistry registry) {
		return new StringBuilder()
			.append("new NonterminalTransition(")
			.append("slot" + registry.getId(nonterminal)).append(", ")
			.append("slot" + registry.getId(origin)).append(", ")
			.append("slot" + registry.getId(dest)).append(", ")
			.append(getConstructorCode(preConditions, registry))
			.toString();
	}

}
