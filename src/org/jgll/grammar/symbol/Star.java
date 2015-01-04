package org.jgll.grammar.symbol;

import org.jgll.grammar.GrammarRegistry;


/**
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class Star extends AbstractSymbol {

	private static final long serialVersionUID = 1L;
	
	private final Symbol s;
	
	private Star(Builder builder) {
		super(builder);
		this.s = builder.s;
	}
	
	public Star from(Symbol s) {
		return new Builder(s).build();
	}
	
	public Symbol getSymbol() {
		return s;
	}
	
	@Override
	public String getConstructorCode(GrammarRegistry registry) {
		return new StringBuilder()
		  .append("new Star.builder(" + s.getConstructorCode(registry) + ")")
		  .append(".setLabel(" + label + ")")
		  .append(".setObject(" + object + ")")
		  .append(".setPreConditions(" + getConstructorCode(preConditions, registry) + ")")
		  .append(".setPostConditions(" + getConstructorCode(postConditions, registry) + ")")
		  .append(".build()").toString();
	}
	
	public static Builder builder(Symbol s) {
		return new Builder(s);
	}

	public static class Builder extends SymbolBuilder<Star> {

		private Symbol s;

		public Builder(Symbol s) {
			super(s.getName() + "*");
			this.s = s;
		}
		
		public Builder(Star star) {
			super(star);
			this.s = star.s;
		}
		
		@Override
		public Star build() {
			return new Star(this);
		}
	}
	
}
