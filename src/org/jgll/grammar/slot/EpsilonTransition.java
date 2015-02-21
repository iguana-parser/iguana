package org.jgll.grammar.slot;

import java.util.Set;

import org.jgll.datadependent.ast.AST;
import org.jgll.datadependent.ast.Expression;
import org.jgll.datadependent.env.Environment;
import org.jgll.grammar.condition.Condition;
import org.jgll.grammar.condition.Conditions;
import org.jgll.grammar.condition.ConditionsFactory;
import org.jgll.grammar.exception.UnexpectedRuntimeTypeException;
import org.jgll.parser.GLLParser;
import org.jgll.parser.gss.GSSNode;
import org.jgll.sppf.NonPackedNode;
import org.jgll.util.Tuple;

public class EpsilonTransition extends AbstractTransition {
	
	private final Type type;
	private final String label;
	private final Conditions conditions;

	public EpsilonTransition(Set<Condition> conditions, BodyGrammarSlot origin, BodyGrammarSlot dest) {
		this(Type.DUMMY, conditions, origin, dest);
	}
	
	public EpsilonTransition(Type type, Set<Condition> conditions, BodyGrammarSlot origin, BodyGrammarSlot dest) {
		super(origin, dest);
		this.type = type;
		this.label = null;
		this.conditions = ConditionsFactory.getConditions(conditions);
	}
	
	public EpsilonTransition(Type type, String label, Set<Condition> conditions, BodyGrammarSlot origin, BodyGrammarSlot dest) {
		super(origin, dest);
		
		assert label != null && (type == Type.DECLARE_LABEL || type == Type.STORE_LABEL);
		
		this.type = type;
		this.label = label;
		this.conditions = ConditionsFactory.getConditions(conditions);
	}

	@Override
	public void execute(GLLParser parser, GSSNode u, int i, NonPackedNode node) {
		dest.execute(parser, u, i, node);
	}

	@Override
	public String getLabel() {
		return "";
	}

	@Override
	public void execute(GLLParser parser, GSSNode u, int i, NonPackedNode node, Environment env) {
		
		parser.setEnvironment(env);
		
		switch(type) {
		
		case DUMMY:
			conditions.execute(parser.getInput(), u, i, parser.getEvaluatorContext());
			break;
			
		case CLEAR_LABEL: // TODO: Decide if this case is needed
			break;
			
		case OPEN:
			
			conditions.execute(parser.getInput(), u, i, parser.getEvaluatorContext());
			parser.getEvaluatorContext().pushEnvironment();
			break;
			
		case CLOSE:
			
			parser.getEvaluatorContext().popEnvironment();
			conditions.execute(parser.getInput(), u, i, parser.getEvaluatorContext());
			break;
			
		case DECLARE_LABEL:
			
			parser.getEvaluatorContext().declareVariable(label, Tuple.<Integer, Integer>of(i, -1));
			parser.getEvaluatorContext().declareVariable(String.format(Expression.LeftExtent.format, label), Tuple.<Integer, Integer>of(i, -1));
			conditions.execute(parser.getInput(), u, i, parser.getEvaluatorContext());
			break;
			
		case STORE_LABEL:
			
			Object value = parser.getEvaluatorContext().lookupVariable(label);
			
			Integer lhs;
			if (!(value instanceof Tuple)) {
				lhs = (Integer) ((Tuple<?,?>) value).getFirst();
			} else {
				throw new UnexpectedRuntimeTypeException(AST.var(label));
			}
			
			parser.getEvaluatorContext().storeVariable(label, Tuple.<Integer, Integer>of(lhs, i));
			conditions.execute(parser.getInput(), u, i, parser.getEvaluatorContext());
			
			break;
			
		}
		
		dest.execute(parser, u, i, node, parser.getEnvironment());
	}

	@Override
	public String getConstructorCode() {
		return null;
	}
	
	public static enum Type {
		DUMMY, OPEN, CLOSE, DECLARE_LABEL, STORE_LABEL, CLEAR_LABEL;
	}

}
