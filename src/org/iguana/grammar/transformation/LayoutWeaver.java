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

package org.iguana.grammar.transformation;

import org.iguana.grammar.runtime.RuntimeGrammar;
import org.iguana.grammar.condition.Condition;
import org.iguana.grammar.condition.ConditionType;
import org.iguana.grammar.slot.NonterminalNodeType;
import org.iguana.grammar.symbol.Return;
import org.iguana.grammar.runtime.RuntimeRule;
import org.iguana.grammar.symbol.Symbol;

import java.util.Set;
import java.util.stream.Collectors;

import static org.iguana.grammar.symbol.LayoutStrategy.INHERITED;

public class LayoutWeaver implements GrammarTransformation {

	@Override
	public RuntimeGrammar transform(RuntimeGrammar grammar) {
		Symbol layout = grammar.getLayout();
		
		RuntimeGrammar.Builder builder = RuntimeGrammar.builder().setLayout(layout).setStartSymbol(grammar.getStartSymbol());
		
		for (RuntimeRule rule : grammar.getRules()) {
			RuntimeRule.Builder ruleBuilder = RuntimeRule.withHead(rule.getHead())
												.setRecursion(rule.getRecursion())
												.setAssociativity(rule.getAssociativity())
												.setAssociativityGroup(rule.getAssociativityGroup())
												.setPrecedence(rule.getPrecedence())
												.setPrecedenceLevel(rule.getPrecedenceLevel())
												.setLabel(rule.getLabel())
												.setDefinition(rule.getDefinition());

			if (rule.size() == 0) {
				builder.addRule(ruleBuilder.build());
				continue;
			}

			if (rule.getHead().getNodeType() == NonterminalNodeType.Start) {
				if (layout != null) {
					builder.addRule(ruleBuilder.addSymbol(layout).addSymbol(rule.symbolAt(0)).addSymbol(layout).build());
				}
				continue;
			}
			
			for (int i = 0; i < rule.size() - 1; i++) {
				Symbol s = rule.symbolAt(i);
				Set<Condition> ignoreLayoutConditions = getIgnoreLayoutConditions(s);
				
				if (i == rule.size() - 2 && rule.symbolAt(rule.size() - 1) instanceof Return
						&& ignoreLayoutConditions.isEmpty()) {
					ruleBuilder.addSymbol(s);
					continue;
				}
				
				if (ignoreLayoutConditions.isEmpty())
					ruleBuilder.addSymbol(s);
				else
					ruleBuilder.addSymbol(s.copy().removePostConditions(ignoreLayoutConditions).build());
				
				addLayout(layout, rule, ruleBuilder, s);
			}
			
			Symbol last = rule.symbolAt(rule.size() - 1);
			Set<Condition> ignoreLayoutConditions = getIgnoreLayoutConditions(last);

			if (ignoreLayoutConditions.isEmpty())
				ruleBuilder.addSymbol(last);
			else 
				ruleBuilder.addSymbol(last.copy().removePostConditions(ignoreLayoutConditions).build());
			
			if (!ignoreLayoutConditions.isEmpty()) {
				addLayout(layout, rule, ruleBuilder, last);
			}

			if (rule.getLayoutStrategy() == INHERITED) {
				ruleBuilder.setLayout(layout);
			}
			builder.addRule(ruleBuilder.build());
		}

		builder.setGlobals(grammar.getGlobals());
		builder.setEbnfLefts(grammar.getEBNFLefts());
		builder.setEbnfRights(grammar.getEBNFRights());
		
		return builder.build();
	}

	private void addLayout(Symbol layout, RuntimeRule rule, RuntimeRule.Builder ruleBuilder, Symbol s) {
		switch (rule.getLayoutStrategy()) {
			case NO_LAYOUT:
				// do nothing
				break;
				
			case INHERITED:
                if (layout != null)
				    ruleBuilder.addSymbol(layout.copy().addPostConditions(getIgnoreLayoutConditions(s)).build());
				break;
				
			case FIXED:
				ruleBuilder.addSymbol(rule.getLayout().copy().addPostConditions(getIgnoreLayoutConditions(s)).build());
				break;
		}
	}
	
	private Set<Condition> getIgnoreLayoutConditions(Symbol s) {
		return s.getPostConditions().stream()
				.filter(c -> c.getType() == ConditionType.NOT_FOLLOW_IGNORE_LAYOUT || c.getType() == ConditionType.FOLLOW_IGNORE_LAYOUT)
				.collect(Collectors.toSet());
	}
	
}
