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

import java.io.IOException;
import java.io.ObjectInputStream;

import org.iguana.traversal.IConditionVisitor;




/**
 *  
 * @author Ali Afroozeh
 *
 */
public class PositionalCondition extends Condition {
	
	private static final long serialVersionUID = 1L;
	
	private transient SlotAction action;
	
	public PositionalCondition(ConditionType type) {
		super(type);
		action = createSlotAction();
	}
	
	@Override
	public String toString() {
		return type.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(this == obj) return true;
		
		if(!(obj instanceof PositionalCondition)) return false;
		
		PositionalCondition other = (PositionalCondition) obj;
		
		return type == other.type;
	}
	
	@Override
	public String getConstructorCode() {
		return "new PositionalCondition(" + type.name() + ")";
	}
	
	// Reading the transiet action field
	private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
		in.defaultReadObject();
		action = createSlotAction();
	}

	private SlotAction createSlotAction() {

		switch (type) {
		
			case START_OF_LINE:
		    	return (input, node, i) -> !input.isStartOfLine(i);
		    
			case END_OF_LINE:
				return (input, node, i) -> !input.isEndOfLine(i);
				
			case END_OF_FILE:
				return (input, node, i) -> !input.isEndOfFile(i);
		
		    default: 
		    	throw new RuntimeException();
		}
	}
	
	@Override
	public SlotAction getSlotAction() {
		return action;
	}

	@Override
	public <T> T accept(IConditionVisitor<T> visitor) {
		return visitor.visit(this);
	}

}
