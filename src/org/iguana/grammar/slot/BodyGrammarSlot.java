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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;

import iguana.parsetrees.sppf.IntermediateNode;
import iguana.parsetrees.sppf.NonPackedNode;
import iguana.parsetrees.sppf.NonterminalNode;
import iguana.parsetrees.sppf.SPPFNodeFactory;
import iguana.utils.collections.Keys;
import iguana.utils.collections.key.IntKey2;
import iguana.utils.input.Input;
import org.iguana.datadependent.env.Environment;
import org.iguana.grammar.condition.Conditions;
import org.iguana.grammar.slot.lookahead.FollowTest;
import org.iguana.grammar.symbol.Position;
import org.iguana.parser.GLLParser;
import org.iguana.parser.gss.GSSNode;
import org.iguana.util.Holder;
import iguana.utils.collections.key.Key;
import iguana.utils.collections.hash.MurmurHash3;


public class BodyGrammarSlot extends AbstractGrammarSlot {
	
	protected final Position position;
	
	private HashMap<Key, IntermediateNode> intermediateNodes;
	
	private final Conditions conditions;
	
	private final String label;
	
	private final int i1;
	
	private final String variable;
	
	private final int i2;
	
	private final Set<String> state;
	
	private FollowTest followTest;
	
	public BodyGrammarSlot(int id, Position position, String label, String variable, Set<String> state, Conditions conditions) {
		this(id, position, label, -1, variable, -1, state, conditions);
	}
	
	public BodyGrammarSlot(int id, Position position, String label, int i1, String variable, int i2, Set<String> state, Conditions conditions) {
		super(id);
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
	public String getConstructorCode() {
		return new StringBuilder()
    	  .append("new BodyGrammarSlot(")
    	  .append(")").toString();
	}
	
	@Override
	public String toString() {
		return position.toString();
	}
	
	@Override
	public boolean isFirst() {
		return position.isFirst();
	}
	
	public void setFollowTest(FollowTest followTest) {
		this.followTest = followTest;
	}
	
	public boolean testFollow(int v) {
		return followTest.test(v);
	}
	
	public IntermediateNode createIntermediateNode(GLLParser parser, NonPackedNode leftChild, NonPackedNode rightChild) {
		IntermediateNode newNode = SPPFNodeFactory.createIntermediateNode(this, leftChild, rightChild);
		parser.intermediateNodeAdded(newNode);
        parser.packedNodeAdded(this, leftChild.getRightExtent());
		return newNode;
	}
	
	public NonPackedNode getIntermediateNode2(GLLParser parser, Input input, NonPackedNode leftChild, NonPackedNode rightChild) {
		
		if (isFirst())
			return rightChild;
		
		Holder<IntermediateNode> holder = new Holder<>();
		
		BiFunction<Key, IntermediateNode, IntermediateNode> creator = (key, value) -> {
			if (value != null) {
				boolean ambiguous = value.addPackedNode(this, leftChild, rightChild);
                parser.packedNodeAdded(this, leftChild.getRightExtent());
                if (ambiguous) parser.ambiguousNodeAdded(value);
				return value;
			} else {
				IntermediateNode newNode = createIntermediateNode(parser, leftChild, rightChild);
				holder.set(newNode);
				return newNode;				
			}
		};

        Key key = Keys.from((x, y) -> x * input.length() + y, leftChild.getLeftExtent(), rightChild.getRightExtent());

        intermediateNodes.compute(key, creator);
		
		return holder.get();
	}
	
	public NonPackedNode getIntermediateNode2(GLLParser parser, Input input, NonPackedNode leftChild, NonPackedNode rightChild, Environment env) {
		
		if (isFirst())
			return rightChild;
		
		Holder<IntermediateNode> holder = new Holder<>();
		BiFunction<Key, IntermediateNode, IntermediateNode> creator = (key, value) -> {
			if (value != null) {
                boolean ambiguous = value.addPackedNode(this, leftChild, rightChild);
                parser.packedNodeAdded(this, leftChild.getRightExtent());
                if (ambiguous) parser.ambiguousNodeAdded(value);
				return value;
			} else {
				IntermediateNode newNode = createIntermediateNode(parser, leftChild, rightChild);
				holder.set(newNode);
				return newNode;				
			}
		};

        Key key = Keys.from(leftChild.getLeftExtent(), rightChild.getRightExtent(), env);
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
	
	public void execute(GLLParser parser, Input input, GSSNode u, int i, NonPackedNode node) {
		getTransitions().forEach(t -> t.execute(parser, input, u, i, node));
	}
	
	/**
	 * 
	 * Data-dependent GLL parsing
	 * 
	 */
	public String getLabel() {
		return label;
	}
	
	public String getVariable() {
		return variable;
	}
	
	public void execute(GLLParser parser, Input input, GSSNode u, int i, NonPackedNode node, Environment env) {
		getTransitions().forEach(t -> t.execute(parser, input, u, i, node, env));
	}
		
	public boolean requiresBinding() {
		return label != null || variable != null || state != null; 
	}
	
	public Environment doBinding(NonPackedNode sppfNode, Environment env) {
		
		if (label != null) {
			if (i1 != -1)
				env = env._declare(sppfNode);
			else
				env = env._declare(label, sppfNode);
		}
		
		if (variable != null && state == null) {
			if (i2 != -1)
				env = env._declare(((NonterminalNode) sppfNode).getValue());
			else
				env = env._declare(variable, ((NonterminalNode) sppfNode).getValue());
		}

		if (variable == null && state != null) { // TODO: support for the array-based environment implementation
			if (state.size() == 1) {
				String v = state.iterator().next();
				if (!v.equals("_")) {
					Object value = ((NonterminalNode) sppfNode).getValue();
					env = env._declare(v, value);
				}
			} else {
				List<?> values = (List<?>) ((NonterminalNode) sppfNode).getValue();
				Iterator<?> it = values.iterator();
				for (String v : state) {
					if (!v.equals("_"))
						env = env._declare(v, it.next());
				}
			}
		}
		
		if (variable != null && state != null) { // TODO: support for the array-based environment implementation
			List<?> values = (List<?>) ((NonterminalNode) sppfNode).getValue();
			Iterator<?> it = values.iterator();
			
			env = env._declare(variable, it.next());
			
			for (String v : state) {
				if (!v.equals("_"))
					env = env._declare(v, it.next());
			}
		}
		
		return env;
	}

}
