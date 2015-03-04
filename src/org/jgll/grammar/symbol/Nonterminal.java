package org.jgll.grammar.symbol;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jgll.datadependent.ast.Expression;
import org.jgll.grammar.condition.Condition;
import org.jgll.parser.HashFunctions;
import org.jgll.traversal.ISymbolVisitor;
import org.jgll.util.generator.GeneratorUtil;

public class Nonterminal extends AbstractSymbol {

	private static final long serialVersionUID = 1L;
	
	private final boolean ebnfList;
	
	private final int index;
	
	private final String variable;
	
	private final String[] parameters; // Only head
	
	private final Expression[] arguments;
	
	private Map<String, Integer> labels; // Only head
	
	private Set<String> excepts;
	
	public static Nonterminal withName(String name) {
		return builder(name).build();
	}
	
	protected Nonterminal(Builder builder) {
		super(builder);
		this.ebnfList = builder.ebnfList;
		this.index = builder.index;
		this.variable = builder.variable;
		this.parameters = builder.parameters;
		this.arguments = builder.arguments;
		this.labels = builder.labels;
		this.excepts = builder.excepts;
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
	
	public String[] getParameters() {
		return parameters;
	}
	
	public Expression[] getArguments() {
		return arguments;
	}
	
	public void addLabel(String label) {
		if (labels == null) labels = new HashMap<>();
		
		if (labels.containsKey(label)) return;
		
		labels.put(label, labels.size());
	}
	
	public int getLabel(String label) {
		if (labels == null || !labels.containsKey(label))
			throw new RuntimeException("Production label has not been found: " + label);
		return labels.get(label);
	}
	
	public Set<String> getExcepts() {
		return excepts;
	}
	
	@Override
	public String toString() {
		return (variable != null? variable + " = " : "")
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
		return Nonterminal.class.getSimpleName() + ".builder(\"" + name + "\")"
													+ super.getConstructorCode() 
													+ (index > 0 ?  ".setIndex(" + index + ")" : "")
													+ (ebnfList == true ? ".setEbnfList(" + ebnfList + ")" : "")
													+ ".build()";
	}

	public static class Builder extends SymbolBuilder<Nonterminal> {

		private boolean ebnfList;
		
		private int index;
		
		private String variable;
		
		private String[] parameters; // Only head
		
		private Expression[] arguments;
		
		private Map<String, Integer> labels; // Only head
		
		private Set<String> excepts;
		
		public Builder(Nonterminal nonterminal) {
			super(nonterminal);
			this.ebnfList = nonterminal.ebnfList;
			this.index = nonterminal.index;
			this.parameters = nonterminal.parameters;
			this.arguments = nonterminal.arguments;
			this.labels = nonterminal.labels;
			this.excepts = nonterminal.excepts;
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
		
		public Builder setEbnfList(boolean ebnfList) {
			this.ebnfList = ebnfList;
			return this;
		}
		
		public Builder addParameters(String... parameters) {
			if (parameters.length != 0) {
				this.parameters = parameters;
			} // otherwise, keep null
			return this;
		}
		
		public Builder apply(Expression... arguments) {
			if (arguments.length != 0) {
				this.arguments = arguments;
			} // otherwise, keep null
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
		
		public Builder addExcepts(String... labels) {
			if (excepts == null) excepts = new HashSet<>();
			for (String label : labels) excepts.add(label);
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
