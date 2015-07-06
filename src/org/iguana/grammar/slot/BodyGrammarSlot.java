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
import java.util.Set;

import org.iguana.datadependent.env.Environment;
import org.iguana.grammar.condition.Conditions;
import org.iguana.grammar.symbol.Position;
import org.iguana.parser.GLLParser;
import org.iguana.parser.gss.GSSNode;
import org.iguana.parser.gss.GSSNodeData;
import org.iguana.parser.gss.lookup.GSSNodeLookup;
import org.iguana.sppf.IntermediateNode;
import org.iguana.sppf.NonPackedNode;
import org.iguana.sppf.NonterminalNode;
import org.iguana.sppf.lookup.SPPFLookup.NodeCreator;
import org.iguana.util.Input;
import org.iguana.util.collections.Key;


public class BodyGrammarSlot extends AbstractGrammarSlot {
	
	protected final Position position;
	
	private HashMap<Key, IntermediateNode> intermediateNodes;
	
	private final GSSNodeLookup nodeLookup;
	
	private final Conditions conditions;
	
	private final String label;
	
	private final String variable;
	
	private final Set<String> state;
	
	public BodyGrammarSlot(int id, Position position, GSSNodeLookup nodeLookup, 
			String label, String variable, Set<String> state, Conditions conditions) {
		super(id);
		this.position = position;
		this.nodeLookup = nodeLookup;
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
	
	public IntermediateNode getIntermediateNode(Key key, NodeCreator<IntermediateNode> creator) {
		return intermediateNodes.computeIfAbsent(key, k -> creator.create(k));
	}
	
	public IntermediateNode findIntermediateNode(Key key) {
		return intermediateNodes.get(key);
	}
	
	@Override
	public GSSNode getGSSNode(int inputIndex) {
		return nodeLookup.getOrElseCreate(this, inputIndex);
	}
	
	@Override
	public GSSNode hasGSSNode(int inputIndex) { 
		if (nodeLookup.isInitialized()) {
			return nodeLookup.get(inputIndex);
		} else {
			nodeLookup.init();			
			return null;
		}
	}
	
	public Conditions getConditions() {
		return conditions;
	}

	@Override
	public void reset(Input input) {
		intermediateNodes = new HashMap<>();
		nodeLookup.reset(input);
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
	
	@Override
	public <T> GSSNode getGSSNode(int inputIndex, GSSNodeData<T> data) {
		return nodeLookup.getOrElseCreate(this, inputIndex, data);
	}
	
	@Override
	public <T> GSSNode hasGSSNode(int inputIndex, GSSNodeData<T> data) {
		return nodeLookup.get(inputIndex, data);
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
			Object[] values = (Object[]) ((NonterminalNode) sppfNode).getValue();
			
			int i = 0;
			for (String v : state) {
				if (!v.equals("_"))
					env = env.declare(v, values[i]);
				i++;
			}
		}
		
		if (variable != null && state != null) {
			Object[] values = (Object[]) ((NonterminalNode) sppfNode).getValue();
			
			env = env.declare(variable, values[0]);
			
			int i = 1;
			for (String v : state) {
				if (!v.equals("_"))
					env = env.declare(v, values[i]);
				i++;
			}
		}
		
		return env;
	}
	
}
