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
import iguana.parsetrees.slot.EndSlot;
import iguana.parsetrees.sppf.NonPackedNode;
import iguana.parsetrees.tree.RuleType;
import org.iguana.datadependent.env.Environment;
import org.iguana.grammar.condition.Conditions;
import org.iguana.grammar.symbol.Position;
import org.iguana.parser.GLLParser;
import org.iguana.parser.gss.GSSNode;

import java.util.Collections;
import java.util.Set;

public class EndGrammarSlot extends BodyGrammarSlot implements EndSlot {

	protected final NonterminalGrammarSlot nonterminal;
	protected final Action action;
	protected final RuleType ruleType;

	public EndGrammarSlot(int id, Position position, NonterminalGrammarSlot nonterminal, String label,
			              String variable, Set<String> state, Conditions conditions, Action action, RuleType ruleType) {
		this(id, position, nonterminal, label, -1, variable, -1, state, conditions, action, ruleType);
	}
	
	public EndGrammarSlot(int id, Position position, NonterminalGrammarSlot nonterminal, String label, int i1, 
            			  String variable, int i2, Set<String> state, Conditions conditions, Action action, RuleType ruleType) {
		super(id, position, label, i1, variable, i2, state, conditions);
		this.nonterminal = nonterminal;
		this.action = action;
        this.ruleType = ruleType;
    }
	
	public NonterminalGrammarSlot getNonterminal() {
		return nonterminal;
	}

	@Override
	public void execute(GLLParser parser, GSSNode u, int i, NonPackedNode node) {
		if (nonterminal.testFollow(parser.getInput().charAt(i)))
			parser.pop(u, i, u.addToPoppedElements(parser, i, this, node));
	}
	
	@Override
	public boolean isEnd() {
		return true;
	}
	
	@Override
	public String getConstructorCode() {
		return null;
	}
	
	public Object getObject() {
		return null;
	}
	
	@Override
	public Set<Transition> getTransitions() {
		return Collections.emptySet();
	}

	@Override
	public boolean addTransition(Transition transition) {
		return false;
	}

    @Override
    public RuleType ruleType() {
        return ruleType;
    }

    @Override
    public Action action() {
        return action;
    }
	
    /**
	 * 
	 * Data-dependent GLL parsing
	 * 
	 */
	@Override
	public void execute(GLLParser parser, GSSNode u, int i, NonPackedNode node, Environment env) {
		if (nonterminal.testFollow(parser.getInput().charAt(i)))
			parser.pop(u, i, u.addToPoppedElements(parser, i, this, node));
	}
	
	public void execute(GLLParser parser, GSSNode u, int i, NonPackedNode node, Object value) {
		if (nonterminal.testFollow(parser.getInput().charAt(i)))
			parser.pop(u, i, u.addToPoppedElements(parser, this, node, value));
	}

}
