/*
 * Copyright (c) 2015, Ali Afroozeh and Anastasia Izmaylova, Centrum Wiskunde & Informatica (CWI)
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


import iguana.utils.input.Input;
import org.iguana.datadependent.ast.AST;
import org.iguana.datadependent.ast.Expression;
import org.iguana.datadependent.env.Environment;
import org.iguana.grammar.condition.Conditions;
import org.iguana.grammar.exception.UnexpectedRuntimeTypeException;
import org.iguana.parser.ParserRuntime;
import org.iguana.parser.descriptor.ResultOps;
import org.iguana.parser.gss.GSSNode;
import org.iguana.util.Tuple;

public class EpsilonTransition<T> extends AbstractTransition<T> {
	
	private final Type type;
    private final String label;
    private final Conditions conditions;

	public EpsilonTransition(Conditions conditions, BodyGrammarSlot<T> origin, BodyGrammarSlot<T> dest, ParserRuntime runtime, ResultOps<T> ops) {
		this(Type.DUMMY, conditions, origin, dest, runtime, ops);
	}
	
	public EpsilonTransition(Type type, Conditions conditions, BodyGrammarSlot<T> origin, BodyGrammarSlot<T> dest, ParserRuntime runtime, ResultOps<T> ops) {
		super(origin, dest, runtime, ops);
		this.type = type;
        this.label = null;
        this.conditions = conditions;
    }
	
	public EpsilonTransition(Type type, String label, Conditions conditions, BodyGrammarSlot<T> origin, BodyGrammarSlot<T> dest, ParserRuntime runtime, ResultOps<T> ops) {
		super(origin, dest, runtime, ops);
		
		assert label != null && (type == Type.DECLARE_LABEL || type == Type.STORE_LABEL);
		
		this.type = type;
		this.label = label;
		this.conditions = conditions;
	}

	@Override
	public void execute(Input input, GSSNode<T> u, T result) {
        int i = ops.getRightIndex(result);
		switch(type) {
            case DUMMY:
                if (conditions.execute(input, u, i))
                    return;
                break;

            case CLEAR_LABEL: // TODO: Decide if this case is needed
                break;

            case OPEN:
                if (conditions.execute(input, u, i))
                    return;

                runtime.setEnvironment(runtime.getEmptyEnvironment());
                runtime.getEvaluatorContext().pushEnvironment();

                dest.execute(input, u, result, runtime.getEnvironment());
                return;

            case CLOSE:
                if (conditions.execute(input, u, i))
                    return;
                break;

            case DECLARE_LABEL:

                runtime.setEnvironment(runtime.getEmptyEnvironment());

                runtime.getEvaluatorContext().declareVariable(label, Tuple.of(i, -1));
                runtime.getEvaluatorContext().declareVariable(String.format(Expression.LeftExtent.format, label), Tuple.<Integer, Integer>of(i, -1));

                if (conditions.execute(input, u, i, runtime.getEvaluatorContext()))
                    return;

                dest.execute(input, u, result, runtime.getEnvironment());
                return;

            case STORE_LABEL:

                runtime.setEnvironment(runtime.getEmptyEnvironment());

                Object value = runtime.getEvaluatorContext().lookupVariable(label);

                Integer lhs;
                if (!(value instanceof Tuple)) {
                    lhs = (Integer) ((Tuple<?,?>) value).getFirst();
                } else {
                    throw new UnexpectedRuntimeTypeException(AST.var(label));
                }

                runtime.getEvaluatorContext().storeVariable(label, Tuple.<Integer, Integer>of(lhs, i));

                if (conditions.execute(input, u, i, runtime.getEvaluatorContext()))
                    return;

                dest.execute(input, u, result, runtime.getEnvironment());
                return;
        }
		
		dest.execute(input, u, result);
	}

	@Override
	public String getLabel() {
		switch(type) {
		case CLEAR_LABEL:
			return "?";
		case CLOSE:
			return "} " + conditions;
		case DECLARE_LABEL:
			return label + ".lExt " + conditions;
		case DUMMY:
			return conditions.toString().equals("") ? String.valueOf('\u2205') : conditions.toString();
		case OPEN:
			return conditions + " {";
		case STORE_LABEL:
			return label + ".rExt " + conditions;
		}
		throw new RuntimeException("Unknown type of an epsilon transition.");
	}

	@Override
	public void execute(Input input, GSSNode<T> u, T result, Environment env) {

        int i = ops.getRightIndex(result);

		runtime.setEnvironment(env);
		
		switch(type) {
		
            case DUMMY:
                if (conditions.execute(input, u, i, runtime.getEvaluatorContext()))
                    return;
                break;

            case CLEAR_LABEL: // TODO: Decide if this case is needed
                break;

            case OPEN:
                if (conditions.execute(input, u, i, runtime.getEvaluatorContext()))
                    return;
                runtime.getEvaluatorContext().pushEnvironment();
                break;

            case CLOSE:
                runtime.getEvaluatorContext().popEnvironment();
                if (conditions.execute(input, u, i, runtime.getEvaluatorContext()))
                    return;
                break;

            case DECLARE_LABEL:
                runtime.getEvaluatorContext().declareVariable(label, Tuple.of(i, -1));
                runtime.getEvaluatorContext().declareVariable(String.format(Expression.LeftExtent.format, label), Tuple.of(i, -1));

                if (conditions.execute(input, u, i, runtime.getEvaluatorContext()))
                    return;
                break;

            case STORE_LABEL:

                Object value = runtime.getEvaluatorContext().lookupVariable(label);

                Integer lhs;
                if (!(value instanceof Tuple)) {
                    lhs = (Integer) ((Tuple<?,?>) value).getFirst();
                } else {
                    throw new UnexpectedRuntimeTypeException(AST.var(label));
                }

                runtime.getEvaluatorContext().storeVariable(label, Tuple.<Integer, Integer>of(lhs, i));

                if (conditions.execute(input, u, i, runtime.getEvaluatorContext()))
                    return;
                break;
            }
		
		dest.execute(input, u, result, runtime.getEnvironment());
	}

	public static enum Type {
		DUMMY, OPEN, CLOSE, DECLARE_LABEL, STORE_LABEL, CLEAR_LABEL;
	}

}
