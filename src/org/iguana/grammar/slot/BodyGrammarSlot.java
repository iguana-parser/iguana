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

import org.iguana.datadependent.env.Environment;
import org.iguana.grammar.condition.Conditions;
import org.iguana.grammar.slot.lookahead.FollowTest;
import org.iguana.grammar.symbol.Position;
import org.iguana.parser.GLLParser;
import org.iguana.parser.gss.GSSNode;
import org.iguana.sppf.IntermediateNode;
import org.iguana.sppf.NonPackedNode;
import org.iguana.sppf.NonterminalNode;
import org.iguana.util.Holder;
import org.iguana.util.Input;
import org.iguana.util.collections.Key;
import org.iguana.util.hashing.hashfunction.MurmurHash3;


public class BodyGrammarSlot extends AbstractGrammarSlot {
	
	protected final Position position;
	
	private HashMap<Key, IntermediateNode> intermediateNodes;
	
	private final Conditions conditions;
	
	private final String label;
	
	private final String variable;
	
	private final Set<String> state;
	
	private FollowTest followTest;
	
	public BodyGrammarSlot(int id, Position position, String label, String variable, Set<String> state, Conditions conditions) {
		super(id);
		this.position = position;
		this.conditions = conditions;
		this.label = label;
		this.variable = variable;
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
		IntermediateNode newNode = new IntermediateNode(this, leftChild.getLeftExtent(), rightChild.getRightExtent());
		newNode.addPackedNode(parser, this, leftChild, rightChild);
		parser.intermediateNodeAdded(newNode);
		return newNode;
	}
	
	public NonPackedNode getIntermediateNode2(GLLParser parser, NonPackedNode leftChild, NonPackedNode rightChild) {
		
		if (isFirst())
			return rightChild;
		
		Holder<IntermediateNode> holder = new Holder<>();
		
		BiFunction<Key, IntermediateNode, IntermediateNode> creator = (key, value) -> {
			if (value != null) {
				value.addPackedNode(parser, this, leftChild, rightChild);
				return value;
			} else {
				IntermediateNode newNode = createIntermediateNode(parser, leftChild, rightChild);
				holder.set(newNode);
				return newNode;				
			}
		};
		
		intermediateNodes.compute(IntKey2.from(leftChild.getLeftExtent(), rightChild.getRightExtent(), parser.getInput().length()), creator);
		
		return holder.get();
	}
	
	public NonPackedNode getIntermediateNode2(GLLParser parser, NonPackedNode leftChild, NonPackedNode rightChild, Environment env) {
		
		if (isFirst())
			return rightChild;
		
		Holder<IntermediateNode> holder = new Holder<>();
		BiFunction<Key, IntermediateNode, IntermediateNode> creator = (key, value) -> {
			if (value != null) {
				value.addPackedNode(parser, this, leftChild, rightChild);
				return value;
			} else {
				IntermediateNode newNode = createIntermediateNode(parser, leftChild, rightChild);
				holder.set(newNode);
				return newNode;				
			}
		};
		
		intermediateNodes.compute(IntKey2PlusObject.from(env, leftChild.getLeftExtent(), rightChild.getRightExtent(), parser.getInput().length()), creator);
		
		return holder.get();
	}	
	
	public Conditions getConditions() {
		return conditions;
	}

	@Override
	public void reset(Input input) {
		intermediateNodes = new HashMap<>();
	}
	
	public void execute(GLLParser parser, GSSNode u, int i, NonPackedNode node) {
		getTransitions().forEach(t -> t.execute(parser, u, i, node));
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
	
	public void execute(GLLParser parser, GSSNode u, int i, NonPackedNode node, Environment env) {
		getTransitions().forEach(t -> t.execute(parser, u, i, node, env));
	}
		
	public boolean requiresBinding() {
		return label != null || variable != null || state != null; 
	}
	
	public Environment doBinding(NonPackedNode sppfNode, Environment env) {
		
		if (label != null)
			env = env.declare(label, sppfNode);
		
		if (variable != null && state == null)
			env = env.declare(variable, ((NonterminalNode) sppfNode).getValue());
		
		if (variable == null && state != null) {
			if (state.size() == 1) {
				String v = state.iterator().next();
				if (!v.equals("_")) {
					Object value = ((NonterminalNode) sppfNode).getValue();
					env = env.declare(v, value);
				}
			} else {
				List<?> values = (List<?>) ((NonterminalNode) sppfNode).getValue();
				Iterator<?> it = values.iterator();
				for (String v : state) {
					if (!v.equals("_"))
						env = env.declare(v, it.next());
				}
			}
		}
		
		if (variable != null && state != null) {
			List<?> values = (List<?>) ((NonterminalNode) sppfNode).getValue();
			Iterator<?> it = values.iterator();
			
			env = env.declare(variable, it.next());
			
			for (String v : state) {
				if (!v.equals("_"))
					env = env.declare(v, it.next());
			}
		}
		
		return env;
	}
	
	static class IntKey2 implements Key, Comparable<IntKey2> {
		
		private final int k1;
		private final int k2;
		private final int hash;

		private IntKey2(int k1, int k2, int size) {
			this.k1 = k1;
			this.k2 = k2;
			this.hash = k1 * size + k2;
		}
		
		public static Key from(int k1, int k2, int size) {
			return new IntKey2(k1, k2, size);
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			
			if (!(obj instanceof IntKey2))
				return false;
			
			IntKey2 other = (IntKey2) obj;
			return k1 == other.k1 && k2 == other.k2;
		}
		
		@Override
		public int hashCode() {
			return hash;
		}

		@Override
		public int[] components() {
			return new int[] {k1, k2};
		}

		@Override
		public int compareTo(IntKey2 o) {
			int r;
			return (r = k1 - o.k1) != 0 ? r : k2 - o.k2;
		}
		
		@Override
		public String toString() {
			return String.format("(%d, %d)", k1, k2);
		}
	}
	
	
	static class IntKey2PlusObject implements Key {
		
		private static MurmurHash3 f = new MurmurHash3();
		
		private final int k1;
		private final int k2;
		private final Object obj;
		
		private final int hash;

		private IntKey2PlusObject(Object obj, int k1, int k2, int size) {
			this.k1 = k1;
			this.k2 = k2;
			this.obj = obj;
			this.hash =  f.hash(obj.hashCode(), k1, k2);
		}
		
		public static Key from(Object obj, int k1, int k2, int size) {
			return new IntKey2PlusObject(obj, k1, k2, size);
		}
		
		@Override
		public boolean equals(Object other) {
			if (this == other) return true;
			
			if (!(other instanceof IntKey2PlusObject)) return false;
			
			IntKey2PlusObject that = (IntKey2PlusObject) other;
			return hash == that.hash 
					&& k1 == that.k1 && k2 == that.k2 
					&& obj.equals(that.obj);
		}
		
		@Override
		public int hashCode() {
			return hash;
		}

		@Override
		public int[] components() {
			return new int[] {k1, k2};
		}

		@Override
		public String toString() {
			return String.format("(%d, %d, %s)", k1, k2, obj);
		}

	}


}
