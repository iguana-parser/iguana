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

import iguana.parsetrees.sppf.*;
import iguana.utils.input.Input;
import org.iguana.datadependent.ast.Expression;
import org.iguana.datadependent.ast.Statement;
import org.iguana.datadependent.env.Environment;
import org.iguana.datadependent.env.IEvaluatorContext;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.condition.DataDependentCondition;
import org.iguana.grammar.slot.BodyGrammarSlot;
import org.iguana.grammar.slot.GrammarSlot;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.parser.descriptor.Descriptor;
import org.iguana.parser.gss.GSSEdge;
import org.iguana.parser.gss.GSSNode;
import org.iguana.util.Configuration;

import java.util.Collections;
import java.util.Map;

/**
 * 
 * 
 * @author Ali Afroozeh
 * @author Anastasia Izmaylova
 *
 */
public interface GLLParser {
	
	ParseResult parse(Input input, GrammarGraph grammarGraph, Nonterminal startSymbol, Map<String, ? extends Object> map, boolean global);
	
	default ParseResult parse(Input input, GrammarGraph grammarGraph, Nonterminal startSymbol) {
		return parse(input, grammarGraph, startSymbol, Collections.emptyMap(), true);
	}
	
	default ParseResult parse(Input input, Grammar grammar, Nonterminal startSymbol) {
		return parse(input, grammar.toGrammarGraph(input, getConfiguration()), startSymbol);
	}
	
	default ParseResult parse(Input input, Grammar grammar, Nonterminal startSymbol, Map<String, ? extends Object> map) {
		return parse(input, grammar.toGrammarGraph(input, getConfiguration()), startSymbol, map, true);
	}
	
	void pop(GSSNode gssNode, int inputIndex, NonterminalNode node);
	
	void recordParseError(GrammarSlot slot);
	
	Input getInput();
	
	Iterable<GSSNode> getGSSNodes();
	
	Configuration getConfiguration();
	
	/**
	 * 
	 * Data-dependent GLL parsing
	 * 
	 */	
	Object evaluate(Statement[] statements, Environment env);
	
	Object evaluate(DataDependentCondition condition, Environment env);
	
	Object evaluate(Expression expression, Environment env);
	
	Object[] evaluate(Expression[] arguments, Environment env);
	
	IEvaluatorContext getEvaluatorContext();
	
	BodyGrammarSlot getCurrentEndGrammarSlot();
	
	Object getCurrentValue();
	
	boolean hasCurrentValue();
	
	void setCurrentEndGrammarSlot(BodyGrammarSlot slot);
	
	void setCurrentValue(Object value);
	
	void resetCurrentValue();
	
	Environment getEnvironment();
	
	void setEnvironment(Environment env);
	
	Environment getEmptyEnvironment();
		
	GrammarGraph getGrammarGraph();
	
	void scheduleDescriptor(Descriptor descriptor);

	void terminalNodeAdded(TerminalNode node);
	
	void nonterminalNodeAdded(NonterminalNode node);
	
	void intermediateNodeAdded(IntermediateNode node);

	void packedNodeAdded(Object slot, int pivot);

	void ambiguousNodeAdded(NonterminalOrIntermediateNode node);

	void gssNodeAdded(GSSNode node);
	
	void gssEdgeAdded(GSSEdge edge);

}
