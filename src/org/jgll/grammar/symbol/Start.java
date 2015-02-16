package org.jgll.grammar.symbol;

public class Start extends Nonterminal {

	private static final long serialVersionUID = 1L;
	
	private final Nonterminal nonterminal;
	
	public static Start from(Nonterminal nonterminal) {
		return builder(nonterminal).build();
	}
	
	public Start(Builder builder) {
		super(builder);
		this.nonterminal = builder.nonterminal;
	}

	@Override
	public Builder copyBuilder() {
		return builder(nonterminal);
	}
	
	public static Builder builder(Nonterminal nonterminal) {
		return new Builder(nonterminal);
	}
	
	public static class Builder extends Nonterminal.Builder {

		private Nonterminal nonterminal;

		public Builder(Nonterminal nonterminal) {
			super("start[" + nonterminal.getName() + "]");
			this.nonterminal = nonterminal;
		}

		@Override
		public Start build() {
			return new Start(this);
		}
	}
	
}
