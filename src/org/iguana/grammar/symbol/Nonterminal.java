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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.iguana.datadependent.ast.Expression;
import org.iguana.grammar.condition.Condition;
import org.iguana.parser.HashFunctions;
import org.iguana.traversal.ISymbolVisitor;
import org.iguana.util.generator.GeneratorUtil;

public class Nonterminal extends AbstractSymbol {

	private static final long serialVersionUID = 1L;
	
	private final boolean ebnfList;
	
	private final int index;
	
	private final String variable;
	
	private final Set<String> state;
	
	private final String[] parameters; // Only head
	
	private final Expression[] arguments;
	
	private final Set<String> excepts;

	/**
	 * The type of this nonterminal. This field is used to track EBNF to BNF conversion
	 * information for each nonterminal. See NonterminalNodeType.
	 */
	private final int nodeType;
	
	public static Nonterminal withName(String name) {
		return builder(name).build();
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
	}
	
	public boolean isEbnfList() {
		if (ebnfList == true) {
			return true;
		} else {
			if(name.startsWith("List")) {
				return true;
			}
		}

		return false;
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
	
	public String[] getParameters() {
		return parameters;
	}
	
	public Expression[] getArguments() {
		return arguments;
	}
	
	public Set<String> getExcepts() {
		return excepts;
	}

	public int getNodeType() {
		return nodeType;
	}

	@Override
	public String toString() {
		return (variable != null? variable + (state == null || state.isEmpty()? "=" : ":") : "")
				+ (state != null && !state.isEmpty()? GeneratorUtil.listToString(state, ":") + "=" : "")
				+ (getPreConditions().isEmpty()? "" : GeneratorUtil.listToString(getPreConditions(), ","))
			    + (label != null? label + ":" : "")
			    + name + (index > 0 ? index : "")
			    + (arguments == null && parameters != null? "(" + GeneratorUtil.listToString(parameters, ",") + ")" : "")
		        + (arguments != null? "(" + GeneratorUtil.listToString(arguments, ",") + ")" : "")
		        + (getPostConditions().isEmpty()? "" : GeneratorUtil.listToString(getPostConditions(), ","));
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		
		if(!(obj instanceof Nonterminal))
			return false;
		
		Nonterminal other = (Nonterminal) obj;
		
		return name.equals(other.name) && index == other.index;
	}
	
	@Override
	public int hashCode() {
		return HashFunctions.defaulFunction.hash(name.hashCode(), index);
	}
	
	public static Builder builder(String name) {
		return new Builder(name);
	}
	
	public static Builder builder(Nonterminal nonterminal) {
		return new Builder(nonterminal);
	}
	
	@Override
	public Builder copyBuilder() {
		return new Builder(this);
	}
	
	@Override
	public String getConstructorCode() {	
		
		String excepts = "";
		if (this.excepts != null)
			excepts = GeneratorUtil.listToString(this.excepts.stream().map(l -> ".addExcept(\"" + l + "\")").collect(Collectors.toSet()));
		
		return Nonterminal.class.getSimpleName() + ".builder(\"" + name + "\")"
				+ (parameters != null? ".addParameters(" 
						+ GeneratorUtil.listToString(Arrays.asList(parameters).stream().map(param -> "\"" + param + "\"").collect(Collectors.toList()), ",") + ")" : "")
				+ (arguments != null? ".apply(" 
						+ GeneratorUtil.listToString(Arrays.asList(arguments).stream().map(arg -> arg.getConstructorCode()).collect(Collectors.toList()), ",") + ")" : "")
				+ super.getConstructorCode() 
				+ (index > 0 ?  ".setIndex(" + index + ")" : "")
				+ (ebnfList == true ? ".setEbnfList(" + ebnfList + ")" : "")
				+ excepts
				+ ".build()";
	}

	public static class Builder extends SymbolBuilder<Nonterminal> {

		private boolean ebnfList;
		
		private int index;
		
		private String variable;
		
		private Set<String> state;
		
		private String[] parameters; // Only head
		
		private Expression[] arguments;
		
		private Set<String> excepts;

		public int nodeType;

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
		}

		public Builder(String name) {
			super(name);
		}
		
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
		
		public Builder addParameters(String... parameters) {
			if (parameters.length == 0)
				return this;
			
			if (this.parameters == null) {
				this.parameters = parameters;
				return this;
			}
			
			String[] params = new String[this.parameters.length + parameters.length];
			int i = 0;
			for (String parameter : this.parameters)
				params[i++] = parameter;
			
			for (String parameter : parameters)
				params[i++] = parameter;
			
			this.parameters = params;
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

		public Builder setType(int nodeType) {
			this.nodeType = nodeType;
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
