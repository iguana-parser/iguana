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

import iguana.parsetrees.slot.Action;
import iguana.parsetrees.sppf.NonPackedNode;
import iguana.parsetrees.sppf.TerminalNode;
import iguana.parsetrees.tree.RuleType;
import iguana.utils.input.Input;
import org.iguana.datadependent.env.Environment;
import org.iguana.grammar.condition.Conditions;
import org.iguana.grammar.symbol.Position;
import org.iguana.parser.GLLParser;
import org.iguana.parser.gss.GSSNode;

public class EpsilonGrammarSlot extends EndGrammarSlot {

	private TerminalGrammarSlot epsilonSlot;

	public EpsilonGrammarSlot(int id, Position position, NonterminalGrammarSlot nonterminal, TerminalGrammarSlot epsilonSlot, 
			                  Conditions conditions, Action action, RuleType ruleType) {
		super(id, position, nonterminal, null, null, null, conditions, action, ruleType);
		this.epsilonSlot = epsilonSlot;
	}
	
	@Override
	public void execute(GLLParser parser, Input input, GSSNode u, int i, NonPackedNode node) {
		if (getNonterminal().testFollow(input.charAt(i))) {
			TerminalNode epsilonNode = epsilonSlot.getTerminalNode(parser, input, i);
			parser.pop(u, input, i, u.addToPoppedElements(parser, input, i, this, epsilonNode));
		}
	}
	
	@Override
	public String getConstructorCode() {
		return "new EpsilonGrammarSlot(slot" + getNonterminal().getId() + ")";
	}
	
	/**
	 *
	 * 
	 * Data-dependent GLL parsing
	 * 
	 */
	@Override
	public void execute(GLLParser parser, Input input, GSSNode u, int i, NonPackedNode node, Environment env) {
		if (getNonterminal().testFollow(input.charAt(i))) {
			TerminalNode epsilonNode = epsilonSlot.getTerminalNode(parser, input, i);
			parser.pop(u, input, i, u.addToPoppedElements(parser, input, i, this, epsilonNode));
		}
		
	}
	
	@Override
	public void execute(GLLParser parser, Input input, GSSNode u, int i, NonPackedNode node, Object value) {
		if (getNonterminal().testFollow(input.charAt(i))) {
			TerminalNode epsilonNode = epsilonSlot.getTerminalNode(parser, input, i);
			parser.pop(u, input, i, u.addToPoppedElements(parser, input, this, epsilonNode, value));
		}
	}

}
