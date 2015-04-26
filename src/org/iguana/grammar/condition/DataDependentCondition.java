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

import org.iguana.datadependent.env.IEvaluatorContext;
import org.iguana.parser.gss.GSSNode;
import org.iguana.traversal.IConditionVisitor;
import org.iguana.util.Input;

public class DataDependentCondition extends Condition {
	
	private static final long serialVersionUID = 1L;
	
	private final org.iguana.datadependent.ast.Expression expression;
	
	private transient final SlotAction action;

	DataDependentCondition(ConditionType type, org.iguana.datadependent.ast.Expression expression) {
		super(type);
		this.expression = expression;
		this.action = new SlotAction() {
			
			@Override
			public boolean execute(Input input, GSSNode gssNode, int inputIndex) {
				throw new UnsupportedOperationException();
			}
			
			@Override
			public boolean execute(Input input, GSSNode gssNode, int inputIndex, IEvaluatorContext ctx) {
				Object value = expression.interpret(ctx);
				if (!(value instanceof Boolean)) 
					throw new RuntimeException("Data dependent condition should evaluate to a boolean value."); 
				return (!(Boolean) value);
			}
			
		};
	}
	
	public org.iguana.datadependent.ast.Expression getExpression() {
		return expression;
	}
	
	@Override
	public boolean isDataDependent() {
		return true;
	}

	@Override
	public String getConstructorCode() {
		return null;
	}

	@Override
	public SlotAction getSlotAction() {
		return action;
	}
	
	static public DataDependentCondition predicate(org.iguana.datadependent.ast.Expression expression) {
		return new DataDependentCondition(ConditionType.DATA_DEPENDENT, expression);
	}
	
	@Override
	public String toString() {
		return String.format("[%s]", expression);
	}

	@Override
	public <T> T accept(IConditionVisitor<T> visitor) {
		return visitor.visit(this);
	}

}
