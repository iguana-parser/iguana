package org.jgll.grammar.symbol;

import org.jgll.regex.RegularExpression;

public class Terminal extends AbstractSymbol {

	private static final long serialVersionUID = 1L;
	
	private final RegularExpression regex;

	public Terminal(Builder builder) {
		super(builder);
		this.regex = builder.regex;
	}

	@Override
	public Builder copyBuilder() {
		return new Builder(regex);
	}
	
	public static Builder builder(RegularExpression regex) {
		return new Builder(regex);
	}
	
	public static class Builder extends SymbolBuilder<Terminal> {
		
		private RegularExpression regex;

		public Builder(RegularExpression regex) {
			super(regex.getName());
			this.regex = regex;
		}
		
		@Override
		public Terminal build() {
			return null;
		}
	}

}
