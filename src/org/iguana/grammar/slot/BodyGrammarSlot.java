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

import org.iguana.utils.collections.Keys;
import org.iguana.utils.collections.OpenAddressingHashMap;
import org.iguana.utils.collections.key.Key;
import org.iguana.utils.input.Input;
import org.iguana.datadependent.env.Environment;
import org.iguana.grammar.condition.Conditions;
import org.iguana.grammar.slot.lookahead.FollowTest;
import org.iguana.grammar.runtime.Position;
import org.iguana.grammar.runtime.RuntimeRule;
import org.iguana.gss.GSSNode;
import org.iguana.parser.IguanaRuntime;
import org.iguana.result.Result;

import java.util.*;

public class BodyGrammarSlot implements GrammarSlot {
	
	protected final Position position;
	
	private Map<Key, Object> intermediateNodes;
	
	private final Conditions conditions;
	
	private final String label;
	
	private final int i1;
	
	private final String variable;
	
	private final int i2;
	
	private final Set<String> state;
	
	private final FollowTest followTest;

	private Transition outTransition;

	private Transition inTransition;

	public BodyGrammarSlot(
		Position position,
		String label,
		String variable,
		Set<String> state,
		Conditions conditions,
		FollowTest followTest
	) {
		this(position, label, -1, variable, -1, state, conditions, followTest);
	}

	public BodyGrammarSlot(
		Position position,
		String label,
		int i1,
		String variable,
		int i2,
		Set<String> state,
		Conditions conditions,
		FollowTest followTest
	) {
		this.position = position;
		this.conditions = conditions;
		this.label = label;
		this.i1 = i1;
		this.variable = variable;
		this.i2 = i2;
		this.state = state;
		this.followTest = followTest;
	}
	
	@Override
	public String toString() {
		return position.toString();
	}

	public boolean testFollow(int v) {
		return followTest.test(v);
	}

	@SuppressWarnings("unchecked")
	public <T extends Result> T getIntermediateNode(
		T leftResult,
		int destinationIndex,
		T rightResult,
		Environment env,
		IguanaRuntime<T> runtime
	) {
		if (isFirst())
			return rightResult;

		Key key = Keys.from(destinationIndex, rightResult.getRightExtent(), env);

		if (intermediateNodes == null) {
		    intermediateNodes = new OpenAddressingHashMap<>();
        }

		Object value = intermediateNodes.get(key);
		if (value == null) {
			T newNode = runtime.getResultOps().merge(null, leftResult, rightResult, this);
			intermediateNodes.put(key, newNode);
			return newNode;
		}

		runtime.getResultOps().merge((T) value, leftResult, rightResult, this);
		return null;
	}
	
	public Conditions getConditions() {
		return conditions;
	}

	@Override
	public void reset() {
		intermediateNodes = null;
	}

	public String getLabel() {
		return label;
	}
	
	public String getVariable() {
		return variable;
	}

	public <T extends Result> void execute(
		Input input,
		GSSNode<T> u,
		T result,
		Environment env,
		IguanaRuntime<T> runtime
	) {
        outTransition.execute(input, u, result, env, runtime);
	}
		
	public boolean requiresBinding() {
		return label != null || variable != null || state != null; 
	}
	
	public Environment doBinding(Result result, Environment env) {

		if (label != null) {
			if (i1 != -1)
				env = env._declare(result);
			else
				env = env._declare(label, result);
		}
		
		if (variable != null && state == null) {
			if (i2 != -1)
				env = env._declare(result.getValue());
			else
				env = env._declare(variable, result.getValue());
		}

		if (variable == null && state != null) { // TODO: support for the array-based environment implementation
			if (state.size() == 1) {
				String v = state.iterator().next();
				if (!v.equals("_")) {
					Object value = result.getValue();
					env = env._declare(v, value);
				}
			} else {
				if (result.getValue() instanceof Object[]) {
					Object[] values = (Object[]) result.getValue();
					int i = 0;
					for (String v : state) {
						if (!v.equals("_")) {
							env = env._declare(v, values[i]);
						}
						i++;
					}
				}
			}
		}
		
		if (variable != null && state != null) { // TODO: support for the array-based environment implementation
			Object[] values = (Object[]) result.getValue();
			env = env._declare(variable, values[0]);

			int i = 1;
			
			for (String v : state) {
				if (!v.equals("_")) {
					env = env._declare(v, values[i]);
				}
				i++;
			}
		}
		
		return env;
	}

	public int getPosition() {
		return position.getPosition();
	}

    public RuntimeRule getRule() {
		return position.getRule();
	}

    public void setOutTransition(Transition outTransition) {
		if (this.outTransition != null) {
			throw new RuntimeException("outTransition is already set");
		}
        this.outTransition = outTransition;
    }

    public void setInTransition(Transition inTransition) {
		if (this.inTransition != null) {
			throw new RuntimeException("inTransition is already set");
		}
		this.inTransition = inTransition;
    }

    public Transition getOutTransition() {
        return outTransition;
    }

    public Transition getInTransition() { return inTransition; }

    /*
     * Corresponds to a grammar position A ::= B . \alpha
     */
    public boolean isFirst() {
        return getPosition() == 1;
    }

    /*
     * Corresponds to a grammar position A ::= . \alpha
     */
    public boolean isStart() {
        return getPosition() == 0;
    }

    public boolean isEnd() {
        return false;
    }

	public FollowTest getFollowTest() {
		return followTest;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		BodyGrammarSlot that = (BodyGrammarSlot) o;
		return i1 == that.i1 &&
			   i2 == that.i2 &&
			   Objects.equals(position, that.position) &&
			   Objects.equals(conditions, that.conditions) &&
			   Objects.equals(label, that.label) &&
			   Objects.equals(variable, that.variable) &&
			   Objects.equals(state, that.state) &&
			   Objects.equals(followTest, that.followTest);
	}

	@Override
	public int hashCode() {
		return Objects.hash(position, conditions, label, i1, variable, i2, state, followTest);
	}
}
