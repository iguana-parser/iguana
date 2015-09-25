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

package org.iguana.grammar.condition;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import iguana.utils.input.Input;
import org.iguana.datadependent.env.GLLEvaluator;
import org.iguana.datadependent.env.IEvaluatorContext;
import org.iguana.parser.gss.GSSNode;
import org.iguana.regex.matcher.MatcherFactory;
import org.iguana.traversal.ToSlotActionConditionVisitor;
import org.iguana.util.generator.GeneratorUtil;

public class ConditionsFactory {
	
	public static Conditions DEFAULT = new Conditions() {
		
		@Override
		public boolean execute(Input input, GSSNode u, int i) {
			return false;
		}
		
		@Override
		public String toString() {
			return "";
		}
	};
	
	public static Conditions getConditions(Set<Condition> conditions, MatcherFactory factory) {
		
		List<Condition> list = new ArrayList<>(conditions);
		
		boolean requiresEnvironment = false;
		for (Condition c : list) {
			if (c.isDataDependent()) {
				requiresEnvironment = true;
				break;
			}
		}

		ToSlotActionConditionVisitor visitor = new ToSlotActionConditionVisitor(factory);
		List<SlotAction> actions = list.stream().map(c -> c.accept(visitor)).collect(Collectors.toList());
		
		if (requiresEnvironment) {
			return new Conditions() {
				
				@Override
				public boolean execute(Input input, GSSNode u, int i) {
					return execute(input, u, i, GLLEvaluator.getDefaultEvaluatorContext(input));
				}
				
				@Override
				public boolean execute(Input input, GSSNode u, int i, IEvaluatorContext ctx) {
					for (SlotAction c : actions) {
					    if (c.execute(input, u, i, ctx)) {
//			                log.trace("Condition %s executed with %s", c, ctx.getEnvironment());
			                return true;
			            }
			        }
			        return false;
				}
				
				@Override
				public String toString() {
					return conditions.isEmpty()? "" : "[" + GeneratorUtil.listToString(conditions, ";") + "]";
				}
				
			};
		}
		
		return new Conditions() {
			
			@Override
			public boolean execute(Input input, GSSNode u, int i) {
		        for (SlotAction c : actions) {
		            if (c.execute(input, u, i)) {
//		                log.trace("Condition %s executed", c);
		                return true;
		            }
		        }
				return false;
			}
			
			@Override
			public String toString() {
				return conditions.isEmpty()? "" : "[" + GeneratorUtil.listToString(conditions, ";") + "]";
			}
		};

	}

}
