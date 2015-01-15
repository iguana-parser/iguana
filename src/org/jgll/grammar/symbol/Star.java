package org.jgll.grammar.symbol;



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
	public String getConstructorCode() {
		return new StringBuilder()
		  .append("new Star.builder(" + s.getConstructorCode() + ")")
		  .append(".setLabel(" + label + ")")
		  .append(".setObject(" + object + ")")
		  .append(".setPreConditions(" + getConstructorCode(preConditions) + ")")
		  .append(".setPostConditions(" + getConstructorCode(postConditions) + ")")
		  .append(".build()").toString();
	}
	
	public static Builder builder(Symbol s) {
		return new Builder(s);
	}
	
	@Override
	public SymbolBuilder<? extends Symbol> copyBuilder() {
		return new Builder(this);
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
