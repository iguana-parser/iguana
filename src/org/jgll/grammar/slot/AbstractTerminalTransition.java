package org.jgll.grammar.slot;

import java.util.Set;

import org.jgll.datadependent.ast.Expression;
import org.jgll.datadependent.env.Environment;
import org.jgll.grammar.condition.Condition;
import org.jgll.grammar.condition.Conditions;
import org.jgll.grammar.condition.ConditionsFactory;
import org.jgll.parser.GLLParser;
import org.jgll.parser.gss.GSSNode;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.TerminalNode;
import org.jgll.util.Input;


public abstract class AbstractTerminalTransition extends AbstractTransition {
	
	protected final TerminalGrammarSlot slot;
	
	private final Conditions preConditions;
	
	private final Conditions postConditions;
	
	public AbstractTerminalTransition(TerminalGrammarSlot slot, BodyGrammarSlot origin, BodyGrammarSlot dest, 
							  		  Set<Condition> preConditions, Set<Condition> postConditions) {
		super(origin, dest);
		this.slot = slot;
		this.preConditions = ConditionsFactory.getConditions(preConditions);
		this.postConditions = ConditionsFactory.getConditions(postConditions);
	}

	@Override
	public void execute(GLLParser parser, GSSNode u, int i, NonPackedNode node) {
		
		Input input = parser.getInput();
		
		if (dest.getLabel() == null) {
			
			if (preConditions.execute(input, u, i))
				return;
			
			int length = slot.match(input, i);
			
			if (length < 0) {
				parser.recordParseError(origin);
				return;
			}
			
			if (postConditions.execute(input, u, i + length))
				return;
			
			TerminalNode cr = parser.getTerminalNode(slot, i, i + length);
			
			createNode(length, cr, parser, u, i, node);
			
		} else {
			
			Environment env = parser.getEmptyEnvironment().declare(String.format(Expression.LeftExtent.format, dest.getLabel()), i);
			parser.setEnvironment(env);
			
			if (preConditions.execute(input, u, i, parser.getEvaluatorContext()))
				return;
			
			int length = slot.match(input, i);
			
			if (length < 0) {
				parser.recordParseError(origin);
				return;
			}
			
			// FIXME: SPPF
			TerminalNode cr = parser.getTerminalNode(slot, i, i + length);
			
			parser.getEvaluatorContext().declareVariable(dest.getLabel(), cr);
			
			if (postConditions.execute(input, u, i + length, parser.getEvaluatorContext()))
				return;
			
			createNode(length, cr, parser, u, i, node, parser.getEnvironment());
			
		}

	}
	
	public TerminalGrammarSlot getSlot() {
		return slot;
	}
	
	protected abstract void createNode(int length, TerminalNode cr, GLLParser parser, GSSNode u, int i, NonPackedNode node);
	
	@Override
	public String getConstructorCode() {
		return new StringBuilder()
			.append("new NonterminalTransition(")
			.append("slot" + slot.getId()).append(", ")
			.append("slot" + origin.getId()).append(", ")
			.append("slot" + dest.getId()).append(", ")
			.toString();
	}

	@Override
	public String getLabel() {
		return getSlot().toString();
	}
	
	/**
	 * 
	 * Data-dependent GLL parsing
	 * 
	 */
	@Override
	public void execute(GLLParser parser, GSSNode u, int i, NonPackedNode node, Environment env) {
		
		Input input = parser.getInput();
		
		parser.setEnvironment(env);
		
		parser.getEvaluatorContext().declareVariable(String.format(Expression.LeftExtent.format, dest.getLabel()), i);

		if (preConditions.execute(input, u, i, parser.getEvaluatorContext()))
			return;
		
		int length = slot.match(input, i);
		
		if (length < 0) {
			parser.recordParseError(origin);
			return;
		}
		
		// FIXME: SPPF
		TerminalNode cr = parser.getTerminalNode(slot, i, i + length);
		
		parser.getEvaluatorContext().declareVariable(dest.getLabel(), cr);

		if (postConditions.execute(input, u, i + length, parser.getEvaluatorContext()))
			return;
		
		createNode(length, cr, parser, u, i, node, parser.getEnvironment());
	}
	
	protected abstract void createNode(int length, TerminalNode cr, GLLParser parser, GSSNode u, int i, NonPackedNode node, Environment env);
	
}
