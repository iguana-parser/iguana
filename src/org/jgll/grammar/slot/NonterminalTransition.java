package org.jgll.grammar.slot;

import java.util.Collections;
import java.util.Set;

import org.jgll.datadependent.ast.Expression;
import org.jgll.datadependent.env.Environment;
import org.jgll.grammar.condition.Condition;
import org.jgll.grammar.condition.Conditions;
import org.jgll.grammar.condition.ConditionsFactory;
import org.jgll.parser.GLLParser;
import org.jgll.parser.gss.GSSNode;
import org.jgll.sppf.NonPackedNode;
import org.jgll.util.generator.GeneratorUtil;


public class NonterminalTransition extends AbstractTransition {
	
	private final NonterminalGrammarSlot nonterminal;
	
	private final Conditions preConditions;
	
	private final Expression[] arguments;

	public NonterminalTransition(NonterminalGrammarSlot nonterminal, BodyGrammarSlot origin, BodyGrammarSlot dest, Set<Condition> preConditions) {
		this(nonterminal, origin, dest, null, preConditions);
	}
	
	public NonterminalTransition(NonterminalGrammarSlot nonterminal, BodyGrammarSlot origin, BodyGrammarSlot dest) {
		this(nonterminal, origin, dest, null, Collections.emptySet());
	}	
	
	public NonterminalTransition(NonterminalGrammarSlot nonterminal, BodyGrammarSlot origin, BodyGrammarSlot dest, 
			Expression[] arguments, Set<Condition> preConditions) {
		super(origin, dest);
		this.nonterminal = nonterminal;
		this.arguments = arguments;
		this.preConditions = ConditionsFactory.getConditions(preConditions);
	}

	@Override
	public void execute(GLLParser parser, GSSNode u, int i, NonPackedNode node) {
		
		if (!nonterminal.test(parser.getInput().charAt(i))) {
			parser.recordParseError(origin);
			return;
		}
		
		if (nonterminal.getParameters() == null && dest.getLabel() == null) {
			if (preConditions.execute(parser.getInput(), u, i))
				return;
			
			parser.create(dest, nonterminal, u, i, node);
		} else {
			Environment env = parser.getEmptyEnvironment();
			
			if (dest.getLabel() != null) {
				env = env.declare(String.format(Expression.LeftExtent.format, dest.getLabel()), i);
			}
			
			parser.setEnvironment(env);
			
			if (preConditions.execute(parser.getInput(), u, i, parser.getEvaluatorContext()))
				return;
			
			parser.create(dest, nonterminal, u, i, node, arguments, parser.getEnvironment());
		}
		
	}
	
	public NonterminalGrammarSlot getSlot() {
		return nonterminal;
	}
	
	@Override
	public String getConstructorCode() {
		return new StringBuilder()
			.append("new NonterminalTransition(")
			.append("slot" + nonterminal.getId()).append(", ")
			.append("slot" + origin.getId()).append(", ")
			.append("slot" + dest.getId()).append(", ")
			.append(preConditions)
			.toString();
	}
	
	@Override
	public String getLabel() {
		return (dest.getVariable() != null? dest.getVariable() + "=" : "") 
				+ (dest.getLabel() != null? dest.getLabel() + ":"  : "")
				+ (arguments != null? String.format("%s(%s)", getSlot().toString(), GeneratorUtil.listToString(arguments, ",")) : getSlot().toString());
	}

	/**
	 * 
	 * Data-dependent GLL parsing
	 * 
	 */
	@Override
	public void execute(GLLParser parser, GSSNode u, int i, NonPackedNode node, Environment env) {
		
		if (!nonterminal.test(parser.getInput().charAt(i))) {
			parser.recordParseError(origin);
			return;
		}
		
		if (dest.getLabel() != null) {
			env = env.declare(String.format(Expression.LeftExtent.format, dest.getLabel()), i);
		}
		
		parser.setEnvironment(env);
		
		if (preConditions.execute(parser.getInput(), u, i, parser.getEvaluatorContext()))
			return;
						
		parser.create(dest, nonterminal, u, i, node, arguments, parser.getEnvironment());
		
	}

}
