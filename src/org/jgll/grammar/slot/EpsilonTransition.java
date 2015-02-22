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
import org.jgll.util.generator.GeneratorUtil;

public class EpsilonTransition extends AbstractTransition {
	
	private final Type type;
	private final String label;
	private final Conditions conditions;
	
	private final String conditions2string;

	public EpsilonTransition(Set<Condition> conditions, BodyGrammarSlot origin, BodyGrammarSlot dest) {
		this(Type.DUMMY, conditions, origin, dest);
	}
	
	public EpsilonTransition(Type type, Set<Condition> conditions, BodyGrammarSlot origin, BodyGrammarSlot dest) {
		super(origin, dest);
		this.type = type;
		this.label = null;
		this.conditions = ConditionsFactory.getConditions(conditions);
		this.conditions2string = conditions.isEmpty()? "" : "[" + GeneratorUtil.listToString(conditions, ";") + "]";
	}
	
	public EpsilonTransition(Type type, String label, Set<Condition> conditions, BodyGrammarSlot origin, BodyGrammarSlot dest) {
		super(origin, dest);
		
		assert label != null && (type == Type.DECLARE_LABEL || type == Type.STORE_LABEL);
		
		this.type = type;
		this.label = label;
		this.conditions = ConditionsFactory.getConditions(conditions);
		this.conditions2string = conditions.isEmpty()? "" : "[" + GeneratorUtil.listToString(conditions, ";") + "]";
	}

	@Override
	public void execute(GLLParser parser, GSSNode u, int i, NonPackedNode node) {
		dest.execute(parser, u, i, node);
	}

	@Override
	public String getLabel() {
		switch(type) {
		case CLEAR_LABEL:
			return "?";
		case CLOSE:
			return "} " + conditions2string;
		case DECLARE_LABEL:
			return "declare: " + label + " " + conditions2string;
		case DUMMY:
			return conditions2string;
		case OPEN:
			return conditions2string + " {";
		case STORE_LABEL:
			return "store: " + label + " " + conditions2string;
		}
		throw new RuntimeException("Unknown type of an epsilon transition.");
	}

	@Override
	public void execute(GLLParser parser, GSSNode u, int i, NonPackedNode node, Environment env) {
		
		parser.setEnvironment(env);
		
		switch(type) {
		
		case DUMMY:
			
			if (conditions.execute(parser.getInput(), u, i, parser.getEvaluatorContext()))
				return;
			
			break;
			
		case CLEAR_LABEL: // TODO: Decide if this case is needed
			break;
			
		case OPEN: 
			
			if (conditions.execute(parser.getInput(), u, i, parser.getEvaluatorContext())) 
				return;
			
			parser.getEvaluatorContext().pushEnvironment();
			break;
			
		case CLOSE:
			
			parser.getEvaluatorContext().popEnvironment();
			
			if (conditions.execute(parser.getInput(), u, i, parser.getEvaluatorContext()))
				return;
			
			break;
			
		case DECLARE_LABEL:
			
			parser.getEvaluatorContext().declareVariable(label, Tuple.<Integer, Integer>of(i, -1));
			parser.getEvaluatorContext().declareVariable(String.format(Expression.LeftExtent.format, label), Tuple.<Integer, Integer>of(i, -1));
			
			if (conditions.execute(parser.getInput(), u, i, parser.getEvaluatorContext()))
				return;
			
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
			
			if (conditions.execute(parser.getInput(), u, i, parser.getEvaluatorContext()))
				return;
			
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
