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

package org.iguana.traversal;

import iguana.regex.matcher.Matcher;
import iguana.regex.matcher.MatcherFactory;
import iguana.utils.input.Input;
import org.iguana.datadependent.env.IEvaluatorContext;
import org.iguana.grammar.condition.DataDependentCondition;
import org.iguana.grammar.condition.PositionalCondition;
import org.iguana.grammar.condition.RegularExpressionCondition;
import org.iguana.grammar.condition.SlotAction;
import org.iguana.gss.GSSNode;
import org.iguana.result.Result;

import java.util.HashMap;
import java.util.Map;

public class ToSlotActionConditionVisitor implements IConditionVisitor<SlotAction> {

	private final MatcherFactory factory;
	
	private Map<PositionalCondition, SlotAction> cachePositional = new HashMap<>();
	
	private Map<RegularExpressionCondition, SlotAction> cacheRegular = new HashMap<>();

	public ToSlotActionConditionVisitor(MatcherFactory factory) {
		this.factory = factory;
	}
	
	@Override
	public SlotAction visit(DataDependentCondition condition) {
		return new SlotAction() {
			
			@Override
			public <T extends Result> boolean execute(Input input, GSSNode<T> gssNode, T result) {
				throw new UnsupportedOperationException();
			}
			
			@Override
			public <T extends Result> boolean execute(Input input, GSSNode<T> gssNode, T result, IEvaluatorContext ctx) {
				Object value = condition.getExpression().interpret(ctx, input);
				if (!(value instanceof Boolean)) 
					throw new RuntimeException("Data dependent condition should evaluate to a boolean value."); 
				return (!(Boolean) value);
			}
			
			@Override
			public String toString() {
				return condition.toString();
			}
		};
	}

	@Override
	public SlotAction visit(PositionalCondition condition) {
		return cachePositional.computeIfAbsent(condition, ToSlotActionConditionVisitor::create);
	}
	
	private static SlotAction create(PositionalCondition condition) {
		switch (condition.getType()) {				
			case START_OF_LINE:
				return new SlotAction() {
					@Override
					public <T extends Result> boolean execute(Input input, GSSNode<T> gssNode, T result) {
					    int inputIndex = result.isDummy() ? gssNode.getInputIndex() : result.getIndex();
						return !input.isStartOfLine(inputIndex);
					}
				};


			case END_OF_LINE:
				return new SlotAction() {
					@Override
					public <T extends Result> boolean execute(Input input, GSSNode<T> gssNode, T result) {
                        int inputIndex = result.isDummy() ? gssNode.getInputIndex() : result.getIndex();
						return !input.isEndOfLine(inputIndex);
					}
				};

			case END_OF_FILE:
				return new SlotAction() {
					@Override
					public <T extends Result> boolean execute(Input input, GSSNode<T> gssNode, T result) {
                        int inputIndex = result.isDummy() ? gssNode.getInputIndex() : result.getIndex();
						return !input.isEndOfFile(inputIndex);
					}
				};


		    default: 
		    	throw new RuntimeException();
		}
	}

	@Override
	public SlotAction visit(RegularExpressionCondition condition) {
		return cacheRegular.computeIfAbsent(condition, c -> create(c, factory));
	}
	
	private static SlotAction create(RegularExpressionCondition condition, MatcherFactory factory) {
		
		switch (condition.getType()) {		
		    case FOLLOW:
		    case FOLLOW_IGNORE_LAYOUT:		    	
		    	return new SlotAction() {
		    		Matcher matcher = factory.getMatcher(condition.getRegularExpression());
					
					@Override
					public <T extends Result> boolean execute(Input input, GSSNode<T> gssNode, T result) {
                        int inputIndex = result.isDummy() ? gssNode.getInputIndex() : result.getIndex();
					    return matcher.match(input, inputIndex) == -1;
					}
					
					@Override
					public String toString() {
					    return condition.toString();
					}
				};
		    	
		    case NOT_FOLLOW:
		    case NOT_FOLLOW_IGNORE_LAYOUT: 
			    return new SlotAction() {
			    	Matcher matcher = factory.getMatcher(condition.getRegularExpression());
			    	
					@Override
					public <T extends Result> boolean execute(Input input, GSSNode<T> gssNode, T result) {
                        int inputIndex = result.isDummy() ? gssNode.getInputIndex() : result.getIndex();
					    return matcher.match(input, inputIndex) >= 0;
					}
					
					@Override
					public String toString() { return condition.toString(); }
				};
		    	
		    case MATCH:
		    	throw new RuntimeException("Unsupported");
		
			case NOT_MATCH: 
				return new SlotAction() {
					Matcher matcher = factory.getMatcher(condition.getRegularExpression());
					
					@Override
					public <T extends Result> boolean execute(Input input, GSSNode<T> node, T result) {
					    return matcher.match(input, result.getLeftExtent(), result.getIndex());
					}
					
					@Override
					public String toString() { return condition.toString(); }
				};
				
			case NOT_PRECEDE: 
				return new SlotAction() {
					Matcher matcher = factory.getBackwardsMatcher(condition.getRegularExpression());
					
					@Override
					public <T extends Result> boolean execute(Input input, GSSNode<T> gssNode, T result) {
                        int inputIndex = result.isDummy() ? gssNode.getInputIndex() : result.getIndex();
					    return matcher.match(input, inputIndex) >= 0;
					}
					
					@Override
					public String toString() { return condition.toString(); }
				};
				
			case PRECEDE:
				return new SlotAction() {
					Matcher matcher = factory.getBackwardsMatcher(condition.getRegularExpression());
					
					@Override
					public <T extends Result> boolean execute(Input input, GSSNode<T> gssNode, T result) {
                        int inputIndex = result.isDummy() ? gssNode.getInputIndex() : result.getIndex();
					    return matcher.match(input, inputIndex) == -1;
					}
					
					@Override
					public String toString() { return condition.toString(); }
				};
				
			default:
				throw new RuntimeException("Unexpected error occurred.");
		}
	}

}
