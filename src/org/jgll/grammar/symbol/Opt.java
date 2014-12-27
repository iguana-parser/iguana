package org.jgll.grammar.symbol;


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

	public static Builder builder(Symbol s) {
		return new Builder(s);
	}
}
