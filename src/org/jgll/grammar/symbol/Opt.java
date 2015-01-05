package org.jgll.grammar.symbol;

import org.jgll.grammar.GrammarRegistry;


public class Opt extends AbstractSymbol {

	private static final long serialVersionUID = 1L;
	
	private final Symbol s;
	
	public static Opt from(Symbol s) {
		return new Builder(s).build();
	}
	
	private Opt(Builder builder) {
		super(builder);
		this.s = builder.s;
	}
	
	public Symbol getSymbol() {
		return s;
	}
	
	@Override
	public String getConstructorCode(GrammarRegistry registry) {
		return new StringBuilder()
		  .append("new Alt.builder(" + s.getConstructorCode(registry) + ")")
		  .append(".setLabel(" + label + ")")
		  .append(".setObject(" + object + ")")
		  .append(".setPreConditions(" + getConstructorCode(preConditions, registry) + ")")
		  .append(".setPostConditions(" + getConstructorCode(postConditions, registry) + ")")
		  .append(".build()").toString();
	}
	
	public static Builder builder(Symbol s) {
		return new Builder(s);
	}
	
	public static class Builder extends SymbolBuilder<Opt> {

		private Symbol s;

		public Builder(Symbol s) {
			super(s.getName() + "?");
			this.s = s;
		}
		
		public Builder(Opt opt) {
			super(opt);
			this.s = opt.s;
		}
		
		@Override
		public Opt build() {
			return new Opt(this);
		}
	}

}
