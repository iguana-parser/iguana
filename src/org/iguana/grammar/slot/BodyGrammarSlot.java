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

import iguana.utils.collections.Keys;
import iguana.utils.collections.key.Key;
import iguana.utils.input.Input;
import org.iguana.datadependent.env.Environment;
import org.iguana.grammar.condition.Conditions;
import org.iguana.grammar.slot.lookahead.FollowTest;
import org.iguana.grammar.symbol.Position;
import org.iguana.grammar.symbol.Rule;
import org.iguana.gss.GSSNode;
import org.iguana.parser.ParserRuntime;
import org.iguana.result.ResultOps;
import org.iguana.util.Holder;

import java.util.*;
import java.util.function.BiFunction;


public class BodyGrammarSlot<T> extends AbstractGrammarSlot<T> {
	
	protected final Position position;
	
	private Map<Key, T> intermediateNodes;
	
	private final Conditions conditions;
	
	private final String label;
	
	private final int i1;
	
	private final String variable;
	
	private final int i2;
	
	private final Set<String> state;
	
	private FollowTest followTest;

	public BodyGrammarSlot(Position position, String label, String variable, Set<String> state, Conditions conditions) {
		this(position, label, -1, variable, -1, state, conditions);
	}
	
	public BodyGrammarSlot(Position position, String label, int i1, String variable, int i2, Set<String> state, Conditions conditions) {
		this.position = position;
		this.conditions = conditions;
		this.label = label;
		this.i1 = i1;
		this.variable = variable;
		this.i2 = i2;
		this.state = state;
		this.intermediateNodes = new HashMap<>();
	}
	
	@Override
	public String toString() {
		return position.toString();
	}
	
	public void setFollowTest(FollowTest followTest) {
		this.followTest = followTest;
	}
	
	public boolean testFollow(int v) {
		return followTest.test(v);
	}

	public T getIntermediateNode2(T leftResult, int destinationIndex, T rightResult, Environment env, ParserRuntime<T> runtime) {
		if (isFirst())
			return rightResult;
		
		Holder<T> holder = new Holder<>();
		BiFunction<Key, T, T> creator = (key, value) -> {
			if (value != null) {
				return runtime.getResultOps().merge(value, leftResult, rightResult, this);
			} else {
				T newNode = runtime.getResultOps().merge(null, leftResult, rightResult, this);
				holder.set(newNode);
				return newNode;
			}
		};

        Key key = Keys.from(destinationIndex, runtime.getResultOps().getRightIndex(rightResult), env);
        intermediateNodes.compute(key, creator);
		
		return holder.get();
	}	
	
	public Conditions getConditions() {
		return conditions;
	}

	@Override
	public void reset(Input input) {
		intermediateNodes = new HashMap<>();
	}

	public String getLabel() {
		return label;
	}
	
	public String getVariable() {
		return variable;
	}
	
	public void execute(Input input, GSSNode<T> u, T result, Environment env, ParserRuntime<T> runtime) {
        for (int i = 0; i < getTransitions().size(); i++) {
            Transition<T> transition = getTransitions().get(i);
            transition.execute(input, u, result, env, runtime);
        }
	}
		
	public boolean requiresBinding() {
		return label != null || variable != null || state != null; 
	}
	
	public Environment doBinding(T result, Environment env, ParserRuntime<T> runtime) {
		ResultOps<T> ops = runtime.getResultOps();

		if (label != null) {
			if (i1 != -1)
				env = env._declare(result);
			else
				env = env._declare(label, result);
		}
		
		if (variable != null && state == null) {
			if (i2 != -1)
				env = env._declare(ops.getValue(result));
			else
				env = env._declare(variable, ops.getValue(result));
		}

		if (variable == null && state != null) { // TODO: support for the array-based environment implementation
			if (state.size() == 1) {
				String v = state.iterator().next();
				if (!v.equals("_")) {
					Object value = ops.getValue(result);
					env = env._declare(v, value);
				}
			} else {
				List<?> values = (List<?>) ops.getValue(result);
				Iterator<?> it = values.iterator();
				for (String v : state) {
					if (!v.equals("_"))
						env = env._declare(v, it.next());
				}
			}
		}
		
		if (variable != null && state != null) { // TODO: support for the array-based environment implementation
			List<?> values = (List<?>) ops.getValue(result);
			Iterator<?> it = values.iterator();
			
			env = env._declare(variable, it.next());
			
			for (String v : state) {
				if (!v.equals("_"))
					env = env._declare(v, it.next());
			}
		}
		
		return env;
	}

	public int getPosition() {
		return position.getPosition();
	}

	public Rule getRule() {
		return position.getRule();
	}

}
