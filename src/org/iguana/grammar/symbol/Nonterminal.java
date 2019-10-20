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

package org.iguana.grammar.symbol;

import org.iguana.datadependent.ast.Expression;
import org.iguana.grammar.condition.Condition;
import org.iguana.grammar.slot.NonterminalNodeType;
import org.iguana.traversal.ISymbolVisitor;

import java.util.*;

import static iguana.utils.string.StringUtil.listToString;

public class Nonterminal extends AbstractSymbol {

	private static final long serialVersionUID = 1L;
	
	private final boolean ebnfList;
	
	private final int index;
	
	private final String variable;
	
	private final Set<String> state;
	
	private final List<String> parameters; // Only head
	
	private final Expression[] arguments;
	
	private final Set<String> excepts;
	
	private final Map<String, Object> attributes;

	/**
	 * The type of this nonterminal. This field is used to track EBNF to BNF conversion
	 * information for each nonterminal. See NonterminalNodeType.
	 */
	private final NonterminalNodeType nodeType;
	
	public static Nonterminal withName(String name) {
		return new Builder(name).build();
	}

	protected Nonterminal(Builder builder) {
		super(builder);
		this.ebnfList = builder.ebnfList;
		this.index = builder.index;
		this.variable = builder.variable;
		this.state = builder.state;
		this.parameters = builder.parameters;
		this.arguments = builder.arguments;
		this.excepts = builder.excepts;
		this.nodeType = builder.nodeType;
		this.attributes = builder.attributes;
	}
	
	public boolean isEbnfList() {
		if (ebnfList) {
			return true;
		} else {
			return name.startsWith("List");
		}
	}
	
	public int getIndex() {
		return index;
	}
	
	public String getVariable() {
		return variable;
	}
	
	public Set<String> getState() {
		return state;
	}
	
	public List<String> getParameters() {
		return parameters;
	}
	
	public Expression[] getArguments() {
		return arguments;
	}
	
	public Set<String> getExcepts() {
		return excepts;
	}

	public NonterminalNodeType getNodeType() {
		return nodeType;
	}

    public Map<String, Object> getAttributes() {
        return attributes;
    }

	@Override
	public String toString() {
		return (variable != null? variable + (state == null || state.isEmpty()? "=" : ":") : "")
				+ (state != null && !state.isEmpty()? listToString(state, ":") + "=" : "")
				+ (getPreConditions().isEmpty()? "" : listToString(getPreConditions(), ","))
			    + (label != null? label + ":" : "")
			    + name + (index > 0 ? index : "")
			    + (arguments == null && parameters != null? "(" + listToString(parameters, ",") + ")" : "")
		        + (arguments != null? "(" + listToString(arguments, ",") + ")" : "")
		        + (getPostConditions().isEmpty()? "" : listToString(getPostConditions(), ","));
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		
		if(!(obj instanceof Nonterminal))
			return false;
		
		Nonterminal other = (Nonterminal) obj;

		return getEffectiveName().equals(other.getEffectiveName());
	}

    private String getEffectiveName() {
        return name + (index > 0 ? index : "");
    }

	@Override
	public int hashCode() {
		return getEffectiveName().hashCode();
	}
	
	@Override
	public Builder copy() {
		return new Builder(this);
	}

	public static class Builder extends SymbolBuilder<Nonterminal> {

		private boolean ebnfList;
		
		private int index;
		
		private String variable;
		
		private Set<String> state;
		
		private List<String> parameters; // Only head
		
		private Expression[] arguments;
		
		private Set<String> excepts;

		private NonterminalNodeType nodeType = NonterminalNodeType.Basic;
		
		private Map<String, Object> attributes = new HashMap<>();

		public Builder(Nonterminal nonterminal) {
			super(nonterminal);
			this.ebnfList = nonterminal.ebnfList;
			this.index = nonterminal.index;
			this.variable = nonterminal.variable;
			this.state = nonterminal.state;
			this.parameters = nonterminal.parameters;
			this.arguments = nonterminal.arguments;
			this.excepts = nonterminal.excepts;
			this.nodeType = nonterminal.nodeType;
			this.attributes = nonterminal.attributes;
		}

		public Builder(String name) {
			this.name = name;
		}

		public Builder() { }

		public Builder setIndex(int index) {
			this.index = index;
			return this;
		}
		
		public Builder setVariable(String variable) {
			this.variable = variable;
			return this;
		}
		
		public Builder setState(Set<String> state) {
			this.state = state;
			return this;
		}
		
		public Builder setEbnfList(boolean ebnfList) {
			this.ebnfList = ebnfList;
			return this;
		}

		public Builder addParameters(String...parameters) {
			addParameters(Arrays.asList(parameters));
			return this;
		}

		public Builder addParameters(List<String> parameters) {
			if (parameters.isEmpty()) return this;
			if (this.parameters == null) {
				this.parameters = parameters;
 			} else {
				this.parameters.addAll(parameters);
			}
			return this;
		}

		public Builder apply(Expression... arguments) {
			if (arguments.length == 0)
				return this;
			
			if (this.arguments == null) {
				this.arguments = arguments;
				return this;
			}
			
			Expression[] args = new Expression[this.arguments.length + arguments.length];
			int i = 0;
			for (Expression argument : this.arguments)
				args[i++] = argument;
			
			for (Expression argument : arguments)
				args[i++] = argument;

			this.arguments = args;			
			return this;
		}
		
		@Override
		public Builder setLabel(String label) {
			super.setLabel(label);
			return this;
		}
		
		@Override
		public Builder addPreCondition(Condition condition) {
			preConditions.add(condition);
			return this;
		}
		
		@Override
		public Builder addPostCondition(Condition condition) {
			postConditions.add(condition);
			return this;
		}	
		
		@Override
		public Builder setObject(Object object) {
			this.object = object;
			return this;
		}
		
		@Override
	 	public Builder addPreConditions(Iterable<Condition> conditions) {
	 		conditions.forEach(c -> preConditions.add(c));
			return this;
		}
	 	
		@Override
	 	public Builder addPostConditions(Iterable<Condition> conditions) {
	 		conditions.forEach(c -> postConditions.add(c));
			return this;
		}
		
		public Builder addExcept(String label) {
			if (excepts == null) excepts = new HashSet<>();
			excepts.add(label);
			return this;
		}
		
		public Builder addExcepts(Set<String> labels) {
			if (labels == null || labels.isEmpty()) 
				return this;
			
			if (excepts == null) 
				excepts = new HashSet<>();
			
			excepts.addAll(labels);			
			return this;
		}

		public Builder setNodeType(NonterminalNodeType nodeType) {
			this.nodeType = nodeType;
			return this;
		}
		
		public Builder setAttributes(Map<String, Object> attributes) {
			this.attributes = attributes;
			return this;
		}
		
		public Builder addAttribute(String key, Object value) {
			this.attributes.put(key, value);
			return this;
		}
		
		public Builder addAttributes(Map<String, Object> attributes) {
			this.attributes.putAll(attributes);
			return this;
		}

		@Override
		public Nonterminal build() {
			return new Nonterminal(this);
		}
	}

	@Override
	public <T> T accept(ISymbolVisitor<T> visitor) {
		return visitor.visit(this);
	}

}
