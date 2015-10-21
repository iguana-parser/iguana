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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import iguana.parsetrees.slot.NonterminalSlot;
import iguana.parsetrees.sppf.DummyNode;
import iguana.parsetrees.sppf.NonPackedNode;
import iguana.parsetrees.sppf.NonterminalNode;
import iguana.utils.input.Input;
import org.iguana.datadependent.ast.Expression;
import org.iguana.datadependent.env.Environment;
import org.iguana.grammar.slot.lookahead.FollowTest;
import org.iguana.grammar.slot.lookahead.LookAheadTest;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.parser.ParserRuntime;
import org.iguana.parser.descriptor.Descriptor;
import org.iguana.parser.gss.GSSNode;
import org.iguana.parser.gss.GSSNodeData;
import org.iguana.parser.gss.lookup.GSSNodeLookup;
import org.iguana.parser.gss.lookup.GSSNodeLookup.GSSNodeCreator;
import org.iguana.util.Configuration.EnvironmentImpl;
import iguana.utils.collections.key.Key;


/**
 * 
 * @author Ali Afroozeh
 *
 */
public class NonterminalGrammarSlot extends AbstractGrammarSlot implements NonterminalSlot {
	
	private final Nonterminal nonterminal;
	
	private final List<BodyGrammarSlot> firstSlots;
	
	private final GSSNodeLookup nodeLookup;

	private LookAheadTest lookAheadTest;
	
	private FollowTest followTest;

	private final int nodeType;

	public NonterminalGrammarSlot(int id, Nonterminal nonterminal, GSSNodeLookup nodeLookup, int nodeType, ParserRuntime runtime) {
		super(id, runtime);
		this.nonterminal = nonterminal;
		this.nodeLookup = nodeLookup;
		this.firstSlots = new ArrayList<>();
		this.nodeType = nodeType;
	}
	
	public void addFirstSlot(BodyGrammarSlot slot) {
		firstSlots.add(slot);
	}
	
	public List<BodyGrammarSlot> getFirstSlots() {
		return firstSlots;
	}
	
	public List<BodyGrammarSlot> getFirstSlots(int v) {
		return lookAheadTest.get(v);
	}
	
	public void setLookAheadTest(LookAheadTest lookAheadTest) {
		this.lookAheadTest = lookAheadTest;
	}

	public void setFollowTest(FollowTest followTest) {
		this.followTest = followTest;
	}
	
//	public boolean testPredict(int v)  {
//		return lookAheadTest.test(v);
//	}
	
	public boolean testFollow(int v) {
		return followTest.test(v);
	}
	
	public Nonterminal getNonterminal() {
		return nonterminal;
	}

	public int getNodeType() {
		return nodeType;
	}

	public String[] getParameters() {
		return nonterminal.getParameters();
	}
	
	@Override
	public String toString() {
		return nonterminal.toString();
	}
	
	@Override
	public String getConstructorCode() {
		return new StringBuilder()
		           .append("new NonterminalGrammarSlot(")
		           .append(nonterminal.getConstructorCode())
		           .append(")").toString();
	}
	
	public void create(Input input, BodyGrammarSlot returnSlot, GSSNode u, NonPackedNode node) {

        int i = node.getRightExtent();

		GSSNodeCreator creator = gssNode -> {
			// No GSS node labelled (slot, k) exits
			if (gssNode == null) {
				
				gssNode = new GSSNode(this, i);
				runtime.gssNodeAdded(gssNode);

				gssNode.createGSSEdge(input, returnSlot, u, node);
				
				final GSSNode __gssNode = gssNode;
				
				List<BodyGrammarSlot> firstSlots = getFirstSlots(input.charAt(i));
				if (firstSlots != null)
					for (BodyGrammarSlot s : firstSlots) {
						if (!s.getConditions().execute(input, __gssNode, i))
							runtime.scheduleDescriptor(new Descriptor(s, __gssNode, new DummyNode(i), input));
					}
				// nonterminal.getFirstSlots().forEach(s -> scheduleDescriptor(new Descriptor(s, __gssNode, i, DummyNode.getInstance())));
			} else {
				runtime.log("GSSNode found: %s", gssNode);
				gssNode.createGSSEdge(input, returnSlot, u, node);
			}
			return gssNode;
		};
		
		nodeLookup.get(i, creator);
	}
	
	@Override
	public boolean isFirst() {
		return true;
	}
	
	public NonterminalNode getNonterminalNode(Key key, Supplier<NonterminalNode> s, Consumer<NonterminalNode> c) {
		throw new RuntimeException("Will be removed soon!");
	}
	
	public NonterminalNode findNonterminalNode(Key key) {
		throw new RuntimeException("Will be removed soon!");
	}
	
	public GSSNode getGSSNode(int i) {
		return nodeLookup.get(this, i);
	}
	
	public Iterable<GSSNode> getGSSNodes() {
		return nodeLookup.getNodes();
	}

	@Override
	public void reset(Input input) {
		nodeLookup.reset(input);
	}
	
	/**
	 * 
	 * Data-dependent GLL parsing
	 * 
	 */
	public void create(Input input, BodyGrammarSlot returnSlot, GSSNode u, NonPackedNode node, Expression[] arguments, Environment env) {
		assert !(env.isEmpty() && arguments == null);

        int i = node.getRightExtent();
		
		if (arguments == null) {
			
			GSSNodeCreator creator = gssNode -> {
				// No GSS node labelled (slot, k) exits
				if (gssNode == null) {
					
					gssNode = new GSSNode(this, i);
					runtime.gssNodeAdded(gssNode);
					
					gssNode.createGSSEdge(input, returnSlot, u, node, env); // Record environment on the edge;
					
					final GSSNode __gssNode = gssNode;
					
					List<BodyGrammarSlot> firstSlots = getFirstSlots(input.charAt(i));
					if (firstSlots != null)
						for (BodyGrammarSlot s : firstSlots) {
							if (!s.getConditions().execute(input, __gssNode, i))
								runtime.scheduleDescriptor(new Descriptor(s, __gssNode, new DummyNode(i), input));
						}
					// nonterminal.getFirstSlots().forEach(s -> scheduleDescriptor(new Descriptor(s, __gssNode, i, DummyNode.getInstance())));
				} else {
					runtime.log("GSSNode found: %s", gssNode);
					gssNode.createGSSEdge(input, returnSlot, u, node, env); // Record environment on the edge
				}
				return gssNode;
			};	
			
			nodeLookup.get(i, creator);
			return;
		}
		
		GSSNodeData<Object> data = new GSSNodeData<>(runtime.evaluate(arguments, env));
		
		GSSNodeCreator creator = gssNode -> {
			if (gssNode == null) {
				
				gssNode = new org.iguana.datadependent.gss.GSSNode<>(this, i, data);
				 
				runtime.gssNodeAdded(gssNode);
				runtime.log("GSSNode created: %s(%s)", gssNode, data);
				
				if (env.isEmpty()) gssNode.createGSSEdge(input, returnSlot, u, node);
				else gssNode.createGSSEdge(input, returnSlot, u, node, env);
				
				Environment newEnv;
				
				if (runtime.getConfiguration().getEnvImpl() == EnvironmentImpl.ARRAY)
					newEnv = runtime.getEmptyEnvironment().declare(data.getValues());
				else
					newEnv = runtime.getEmptyEnvironment().declare(nonterminal.getParameters(), data.getValues());
				
				final GSSNode __gssNode = gssNode;
				
				for (BodyGrammarSlot s : getFirstSlots(input.charAt(i))) {
					
					runtime.setEnvironment(newEnv);

					if (s.getLabel() != null)
						runtime.getEvaluatorContext().declareVariable(String.format(Expression.LeftExtent.format, s.getLabel()), i);

					if (!s.getConditions().execute(input, __gssNode, i, runtime.getEvaluatorContext()))
						runtime.scheduleDescriptor(new org.iguana.datadependent.descriptor.Descriptor(s, __gssNode, new DummyNode(i), input, runtime.getEnvironment()));
				}
				
				// nonterminal.getFirstSlots().forEach(s -> scheduleDescriptor(new org.jgll.datadependent.descriptor.Descriptor(s, __gssNode, i, DummyNode.getInstance(), newEnv)));
				
			} else {
//				log.trace("GSSNode found: %s",  gssNode);
				if (env.isEmpty()) gssNode.createGSSEdge(input, returnSlot, u, node);
				else gssNode.createGSSEdge(input, returnSlot, u, node, env);
			}
			return gssNode;
		};
		nodeLookup.get(i, data, creator);
	}
	
	public <T> GSSNode getGSSNode(int i, GSSNodeData<T> data) {
		return nodeLookup.get(this, i, data);
	}

    @Override
    public int nodeType() {
        return nodeType;
    }
}
