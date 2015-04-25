/*
 * Copyright (c) 2015, CWI
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this 
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this 
 *    list of conditions and the following disclaimer in the documentation and/or 
 *    other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT 
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY 
 * OF SUCH DAMAGE.
 *
 */

package org.iguana.grammar.slot;

import java.util.Set;

import org.iguana.datadependent.ast.AST;
import org.iguana.datadependent.ast.Expression;
import org.iguana.datadependent.env.Environment;
import org.iguana.grammar.condition.Condition;
import org.iguana.grammar.condition.Conditions;
import org.iguana.grammar.condition.ConditionsFactory;
import org.iguana.grammar.exception.UnexpectedRuntimeTypeException;
import org.iguana.parser.GLLParser;
import org.iguana.parser.gss.GSSNode;
import org.iguana.sppf.NonPackedNode;
import org.iguana.util.Tuple;
import org.iguana.util.generator.GeneratorUtil;

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
		switch(type) {
		
		case DUMMY:
			if (conditions.execute(parser.getInput(), u, i))
				return;	
			break;
			
		case CLEAR_LABEL: // TODO: Decide if this case is needed
			break;
			
		case OPEN: 
			if (conditions.execute(parser.getInput(), u, i)) 
				return;
			
			parser.setEnvironment(parser.getEmptyEnvironment());
			parser.getEvaluatorContext().pushEnvironment();
			
			dest.execute(parser, u, i, node, parser.getEnvironment());
			return;
			
		case CLOSE:
			if (conditions.execute(parser.getInput(), u, i))
				return;
			break;
			
		case DECLARE_LABEL:
			
			parser.setEnvironment(parser.getEmptyEnvironment());
			
			parser.getEvaluatorContext().declareVariable(label, Tuple.<Integer, Integer>of(i, -1));
			parser.getEvaluatorContext().declareVariable(String.format(Expression.LeftExtent.format, label), Tuple.<Integer, Integer>of(i, -1));
			
			if (conditions.execute(parser.getInput(), u, i, parser.getEvaluatorContext()))
				return;
			
			dest.execute(parser, u, i, node, parser.getEnvironment());
			return;
			
		case STORE_LABEL:
			
			parser.setEnvironment(parser.getEmptyEnvironment());
			
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
			
			dest.execute(parser, u, i, node, parser.getEnvironment());	
			return;
		}
		
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
			return label + ".lExt " + conditions2string;
		case DUMMY:
			return conditions2string.isEmpty()? String.valueOf('\u2205') : conditions2string;
		case OPEN:
			return conditions2string + " {";
		case STORE_LABEL:
			return label + ".rExt " + conditions2string;
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
