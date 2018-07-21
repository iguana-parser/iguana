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
import iguana.utils.collections.rangemap.RangeMap;
import iguana.utils.input.Input;
import org.iguana.datadependent.ast.Expression;
import org.iguana.datadependent.env.Environment;
import org.iguana.grammar.slot.lookahead.FollowTest;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.gss.GSSNode;
import org.iguana.parser.IguanaRuntime;
import org.iguana.result.Result;
import org.iguana.util.Configuration.EnvironmentImpl;
import org.iguana.util.ParserLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class NonterminalGrammarSlot extends AbstractGrammarSlot {
	
	private final Nonterminal nonterminal;
	
	private final List<BodyGrammarSlot> firstSlots;

	private Map<Key, GSSNode> gssNodes;

	private RangeMap<BodyGrammarSlot> lookAheadTest;

	private FollowTest followTest;

	public NonterminalGrammarSlot(Nonterminal nonterminal) {
		this.nonterminal = nonterminal;
		this.gssNodes = new HashMap<>();
		this.firstSlots = new ArrayList<>();
	}
	
	public void addFirstSlot(BodyGrammarSlot slot) {
		firstSlots.add(slot);
	}
	
	public List<BodyGrammarSlot> getFirstSlots() {
		return firstSlots;
	}
	
	private List<BodyGrammarSlot> getFirstSlots(int v) {
		return lookAheadTest.get(v);
	}
	
	public void setLookAheadTest(RangeMap<BodyGrammarSlot> lookAheadTest) {
		this.lookAheadTest = lookAheadTest;
	}

	public void setFollowTest(FollowTest followTest) {
		this.followTest = followTest;
	}
	
	boolean testFollow(int v) {
		return followTest.test(v);
	}
	
	public Nonterminal getNonterminal() {
		return nonterminal;
	}

	public NonterminalNodeType getNodeType() {
		return nonterminal.getNodeType();
	}

	public String[] getParameters() {
		return nonterminal.getParameters();
	}

	public int countGSSNodes() {
		return gssNodes.size();
	}
	
	@Override
	public String toString() {
		return nonterminal.toString();
	}
	
	@Override
	public boolean isFirst() {
		return true;
	}

    @Override
    public int getPosition() {
        throw new UnsupportedOperationException();
    }

    public GSSNode getGSSNode(int i) {
		return gssNodes.computeIfAbsent(Keys.from(i), key -> new GSSNode(this, i));
	}

	public GSSNode getGSSNode(int i, Object[] data) {
		return gssNodes.computeIfAbsent(Keys.from(i, data), key -> new GSSNode(this, i, data));
	}
	
	public Iterable<GSSNode> getGSSNodes() {
		return gssNodes.values();
	}

	@Override
	public void reset(Input input) {
		gssNodes = new HashMap<>();
	}
	
	public <T extends Result> void create(Input input, BodyGrammarSlot returnSlot, GSSNode<T> u, T result, Expression[] arguments, Environment env, IguanaRuntime<T> runtime) {
        int i = result.isDummy() ? u.getInputIndex() : result.getIndex();

        Key key;
        Object[] data = null;

        if (arguments == null) {
        	key = Keys.from(i);
		} else {
			data = runtime.evaluate(arguments, env, input);
        	key = Keys.from(i, data);
		}

		GSSNode<T> gssNode = gssNodes.get(key);

		if (gssNode == null) {
			gssNode = new GSSNode<>(this, i, data);

			ParserLogger.getInstance().gssNodeAdded(gssNode, data);

			Environment newEnv = runtime.getEnvironment();

			if (data != null) {
				if (runtime.getConfiguration().getEnvImpl() == EnvironmentImpl.ARRAY || runtime.getConfiguration().getEnvImpl() == EnvironmentImpl.INT_ARRAY)
					newEnv = runtime.getEmptyEnvironment().declare(data);
				else
					newEnv = runtime.getEmptyEnvironment().declare(nonterminal.getParameters(), data);
			}

			List<BodyGrammarSlot> firstSlots = getFirstSlots(input.charAt(i));
			if (firstSlots != null) {
				for (int j = 0; j < firstSlots.size(); j++) {
					BodyGrammarSlot slot = firstSlots.get(j);

					runtime.setEnvironment(newEnv);

					if (slot.getLabel() != null)
						runtime.getEvaluatorContext().declareVariable(String.format(Expression.LeftExtent.format, slot.getLabel()), i);

					if (!slot.getConditions().execute(input, gssNode, i, runtime.getEvaluatorContext(), runtime))
						runtime.scheduleDescriptor(slot, gssNode, runtime.getResultOps().dummy(), runtime.getEnvironment());

				}
			}

			gssNodes.put(key, gssNode);
		}

		gssNode.createGSSEdge(input, returnSlot, u, result, env, runtime);
	}

}
