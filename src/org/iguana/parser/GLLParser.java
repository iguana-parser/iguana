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

package org.iguana.parser;

import java.util.Collections;
import java.util.Map;

import org.iguana.datadependent.ast.Expression;
import org.iguana.datadependent.ast.Statement;
import org.iguana.datadependent.env.Environment;
import org.iguana.datadependent.env.IEvaluatorContext;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.condition.DataDependentCondition;
import org.iguana.grammar.slot.BodyGrammarSlot;
import org.iguana.grammar.slot.GrammarSlot;
import org.iguana.grammar.slot.LastSymbolGrammarSlot;
import org.iguana.grammar.slot.NonterminalGrammarSlot;
import org.iguana.grammar.slot.TerminalGrammarSlot;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.parser.gss.GSSNode;
import org.iguana.parser.gss.GSSNodeData;
import org.iguana.sppf.IntermediateNode;
import org.iguana.sppf.NonPackedNode;
import org.iguana.sppf.NonterminalNode;
import org.iguana.sppf.TerminalNode;
import org.iguana.util.Configuration;
import org.iguana.util.Input;

/**
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public interface GLLParser {
	
	public ParseResult parse(Input input, GrammarGraph grammarGraph, Nonterminal startSymbol, Map<String, ? extends Object> map);
	
	default ParseResult parse(Input input, GrammarGraph grammarGraph, Nonterminal startSymbol) {
		return parse(input, grammarGraph, startSymbol, Collections.emptyMap());
	}
	
	default ParseResult parse(Input input, Grammar grammar, Nonterminal startSymbol) {
		return parse(input, grammar.toGrammarGraph(input, getConfiguration()), startSymbol);
	}
	
	default ParseResult parse(Input input, Grammar grammar, Nonterminal startSymbol, Map<String, ? extends Object> map) {
		return parse(input, grammar.toGrammarGraph(input, getConfiguration()), startSymbol, map);
	}
	
	public void pop(GSSNode gssNode, int inputIndex, NonPackedNode node);
	
	public GSSNode create(BodyGrammarSlot returnSlot, NonterminalGrammarSlot nonterminal, GSSNode gssNode, int i, NonPackedNode node);
	
	public TerminalNode getTerminalNode(TerminalGrammarSlot slot, int leftExtent, int rightExtent);

	public TerminalNode getEpsilonNode(TerminalGrammarSlot slot, int inputIndex);
	
	public NonterminalNode hasNonterminalNode(LastSymbolGrammarSlot slot, NonPackedNode leftChild, NonPackedNode rightChild);
	
	public NonterminalNode getNonterminalNode(LastSymbolGrammarSlot slot, NonPackedNode leftChild, NonPackedNode rightChild);
	
	public NonterminalNode hasNonterminalNode(LastSymbolGrammarSlot slot, NonPackedNode child);
	
	public NonterminalNode getNonterminalNode(LastSymbolGrammarSlot slot, NonPackedNode child);
	
	public IntermediateNode hasIntermediateNode(BodyGrammarSlot slot, NonPackedNode leftChild, NonPackedNode rightChild);
	
	public IntermediateNode getIntermediateNode(BodyGrammarSlot slot, NonPackedNode leftChild, NonPackedNode rightChild);
	
	public NonPackedNode getNode(GrammarSlot slot, NonPackedNode leftChild, NonPackedNode rightChild);
	
	public NonPackedNode hasNode(GrammarSlot slot, NonPackedNode leftChild, NonPackedNode rightChild);
	
	public boolean hasDescriptor(GrammarSlot slot, GSSNode gssNode, int inputIndex, NonPackedNode sppfNode);
	
	public void recordParseError(GrammarSlot slot);
	
	public Input getInput();
	
	public Iterable<GSSNode> getGSSNodes();
	
	public Configuration getConfiguration();
	
	/**
	 * 
	 * Data-dependent GLL parsing
	 * 
	 */	
	public Object evaluate(Statement[] statements, Environment env);
	
	public Object evaluate(DataDependentCondition condition, Environment env);
	
	public Object evaluate(Expression expression, Environment env);
	
	public Object[] evaluate(Expression[] arguments, Environment env);
	
	public IEvaluatorContext getEvaluatorContext();
	
	public BodyGrammarSlot getCurrentEndGrammarSlot();
	
	public Object getCurrentValue();
	
	public boolean hasCurrentValue();
	
	public void setCurrentEndGrammarSlot(BodyGrammarSlot slot);
	
	public void setCurrentValue(Object value);
	
	public void resetCurrentValue();
	
	public Environment getEnvironment();
	
	public void setEnvironment(Environment env);
	
	public Environment getEmptyEnvironment();
	
	public GSSNode create(BodyGrammarSlot returnSlot, NonterminalGrammarSlot nonterminal, GSSNode gssNode, int i, NonPackedNode node, Expression[] arguments, Environment env);
	
	public boolean hasDescriptor(GrammarSlot slot, GSSNode gssNode, int inputIndex, NonPackedNode sppfNode, Environment env);
	
	public <T> NonterminalNode getNonterminalNode(LastSymbolGrammarSlot slot, NonPackedNode leftChild, NonPackedNode rightChild, GSSNodeData<T> data, Object value);
	
	public <T> NonterminalNode hasNonterminalNode(LastSymbolGrammarSlot slot, NonPackedNode leftChild, NonPackedNode rightChild, GSSNodeData<T> data, Object value);
	
	public <T> NonterminalNode getNonterminalNode(LastSymbolGrammarSlot slot, NonPackedNode child, GSSNodeData<T> data, Object value);
	
	public <T> NonterminalNode hasNonterminalNode(LastSymbolGrammarSlot slot, NonPackedNode child, GSSNodeData<T> data, Object value);
	
	public IntermediateNode getIntermediateNode(BodyGrammarSlot slot, NonPackedNode leftChild, NonPackedNode rightChild, Environment env);
	
	public IntermediateNode hasIntermediateNode(BodyGrammarSlot slot, NonPackedNode leftChild, NonPackedNode rightChild, Environment env);
	
	public <T> NonPackedNode getNode(GrammarSlot slot, NonPackedNode leftChild, NonPackedNode rightChild, Environment env, GSSNodeData<T> data, Object value);
	
	public <T> NonPackedNode hasNode(GrammarSlot slot, NonPackedNode leftChild, NonPackedNode rightChild, Environment env, GSSNodeData<T> data, Object value);

	public GrammarGraph getGrammarGraph();
	
}
