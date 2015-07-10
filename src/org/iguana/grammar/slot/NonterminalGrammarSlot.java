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

import org.iguana.datadependent.ast.Expression;
import org.iguana.datadependent.env.Environment;
import org.iguana.grammar.slot.lookahead.FollowTest;
import org.iguana.grammar.slot.lookahead.LookAheadTest;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.parser.GLLParser;
import org.iguana.parser.descriptor.Descriptor;
import org.iguana.parser.gss.GSSNode;
import org.iguana.parser.gss.GSSNodeData;
import org.iguana.parser.gss.lookup.GSSNodeLookup;
import org.iguana.parser.gss.lookup.GSSNodeLookup.GSSNodeCreator;
import org.iguana.sppf.DummyNode;
import org.iguana.sppf.NonPackedNode;
import org.iguana.sppf.NonterminalNode;
import org.iguana.util.Input;
import org.iguana.util.collections.Key;


/**
 * 
 * @author Ali Afroozeh
 *
 */
public class NonterminalGrammarSlot extends AbstractGrammarSlot {
	
	private final Nonterminal nonterminal;
	
	private final List<BodyGrammarSlot> firstSlots;
	
	private final GSSNodeLookup nodeLookup;

	private LookAheadTest lookAheadTest;
	
	private FollowTest followTest;

	public NonterminalGrammarSlot(int id, Nonterminal nonterminal, GSSNodeLookup nodeLookup) {
		super(id);
		this.nonterminal = nonterminal;
		this.nodeLookup = nodeLookup;
		this.firstSlots = new ArrayList<>();
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
	
	public boolean testPredict(int v)  {
		return lookAheadTest.test(v);
	}
	
	public boolean testFollow(int v) {
		return followTest.test(v);
	}
	
	public Nonterminal getNonterminal() {
		return nonterminal;
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
	
	public void create(GLLParser parser, BodyGrammarSlot returnSlot, GSSNode u, int i, NonPackedNode node) {
		
		GSSNodeCreator creator = gssNode -> {
			// No GSS node labelled (slot, k) exits
			if (gssNode == null) {
//				log.trace("GSSNode created: (%s, %d)",  nonterminal, i);
//				countGSSNodes++;
				gssNode = new GSSNode(this, i);
				gssNode.createGSSEdge(parser, returnSlot, u, node);
				
				final GSSNode __gssNode = gssNode;
				
				Input input = parser.getInput();
				
				List<BodyGrammarSlot> firstSlots = getFirstSlots(input.charAt(i));
				if (firstSlots != null)
					for (BodyGrammarSlot s : firstSlots) {
						if (!s.getConditions().execute(input, __gssNode, i))
							parser.scheduleDescriptor(new Descriptor(s, __gssNode, i, DummyNode.getInstance()));
					}
				// nonterminal.getFirstSlots().forEach(s -> scheduleDescriptor(new Descriptor(s, __gssNode, i, DummyNode.getInstance())));
			} else {
//				log.trace("GSSNode found: %s",  gssNode);
				gssNode.createGSSEdge(parser, returnSlot, u, node);			
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
	public void create(GLLParser parser, BodyGrammarSlot returnSlot, GSSNode u, int i, NonPackedNode node, Expression[] arguments, Environment env) {
		assert !(env.isEmpty() && arguments == null);
		
		if (arguments == null) {
			
			GSSNodeCreator creator = gssNode -> {
				// No GSS node labelled (slot, k) exits
				if (gssNode == null) {
//					log.trace("GSSNode created: (%s, %d)",  nonterminal, i);
//					countGSSNodes++;
					gssNode = new GSSNode(this, i);
					gssNode.createGSSEdge(parser, returnSlot, u, node, env); // Record environment on the edge;
					
					final GSSNode __gssNode = gssNode;
					
					Input input = parser.getInput();
					
					List<BodyGrammarSlot> firstSlots = getFirstSlots(input.charAt(i));
					if (firstSlots != null)
						for (BodyGrammarSlot s : firstSlots) {
							if (!s.getConditions().execute(input, __gssNode, i))
								parser.scheduleDescriptor(new Descriptor(s, __gssNode, i, DummyNode.getInstance()));
						}
					// nonterminal.getFirstSlots().forEach(s -> scheduleDescriptor(new Descriptor(s, __gssNode, i, DummyNode.getInstance())));
				} else {
//					log.trace("GSSNode found: %s",  gssNode);
					gssNode.createGSSEdge(parser, returnSlot, u, node, env); // Record environment on the edge			
				}
				return gssNode;
			};	
			
			nodeLookup.get(i, creator);
			return;
		}
		
		GSSNodeData<Object> data = new GSSNodeData<>(parser.evaluate(arguments, env));
		
		GSSNodeCreator creator = gssNode -> {
			if (gssNode == null) {
				
				gssNode = new org.iguana.datadependent.gss.GSSNode<>(this, i, data);
				 
//				log.trace("GSSNode created: %s(%s)",  gssNode, data);
				
				if (env.isEmpty()) gssNode.createGSSEdge(parser, returnSlot, u, node);
				else gssNode.createGSSEdge(parser, returnSlot, u, node, env);
				
				Environment newEnv = parser.getEmptyEnvironment().declare(nonterminal.getParameters(), data.getValues());
				
				final GSSNode __gssNode = gssNode;
				
				Input input = parser.getInput();
				for (BodyGrammarSlot s : getFirstSlots(input.charAt(i))) {
					
					parser.setEnvironment(newEnv);
					
					if (s.getLabel() != null)
						parser.getEvaluatorContext().declareVariable(String.format(Expression.LeftExtent.format, s.getLabel()), i);
					
					if (!s.getConditions().execute(input, __gssNode, i, parser.getEvaluatorContext()))
						parser.scheduleDescriptor(new org.iguana.datadependent.descriptor.Descriptor(s, __gssNode, i, DummyNode.getInstance(), parser.getEnvironment()));
				}
				
				// nonterminal.getFirstSlots().forEach(s -> scheduleDescriptor(new org.jgll.datadependent.descriptor.Descriptor(s, __gssNode, i, DummyNode.getInstance(), newEnv)));
				
			} else {
//				log.trace("GSSNode found: %s",  gssNode);
				if (env.isEmpty()) gssNode.createGSSEdge(parser, returnSlot, u, node);
				else gssNode.createGSSEdge(parser, returnSlot, u, node, env);		
			}
			return gssNode;
		};
		nodeLookup.get(i, data, creator);
	}
}
