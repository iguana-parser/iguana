package org.jgll.grammar.symbol;

import java.util.Arrays;
import java.util.List;

import org.jgll.util.generator.GeneratorUtil;

import com.google.common.collect.ImmutableList;

public class Alt extends AbstractSymbol {

	private static final long serialVersionUID = 1L;
	
	private final List<Symbol> symbols;
	
	public static Alt from(Symbol...symbols) {
		return new Builder(Arrays.asList(symbols)).build();
	}
	
	public static Alt from(List<Symbol> symbols) {
		return new Builder(symbols).build();
	}
	
	private Alt(Builder builder) {
		super(builder);
		this.symbols = ImmutableList.copyOf(builder.symbols);
	}
	
	public List<Symbol> getSymbols() {
		return symbols;
	}
	
	public static class Builder extends SymbolBuilder<Alt> {

		private List<Symbol> symbols;
		
		public Builder(List<Symbol> symbols) {
			super(GeneratorUtil.listToString(symbols, "|"));
			this.symbols = symbols;
		}
		
		public Builder(Alt alt) {
			super(alt);
			this.symbols = alt.symbols;
		}
		
		@Override
		public Alt build() {
			return new Alt(this);
		}
	}

	public static Builder builder(Symbol...symbols) {
		return new Builder(Arrays.asList(symbols));
	}
	
	public static Builder builder(List<Symbol> symbols) {
		return new Builder(symbols);
	}
	
	@Override
	public SymbolBuilder<? extends Symbol> copyBuilder() {
		return new Builder(this);
	}
	
	@Override
	public String getConstructorCode() {
		return new StringBuilder()
		  .append("new Alt.builder(" + getConstructorCode(symbols) + ")")
		  .append(".setLabel(" + label + ")")
		  .append(".setObject(" + object + ")")
		  .append(".setPreConditions(" + getConstructorCode(preConditions) + ")")
		  .append(".setPostConditions(" + getConstructorCode(postConditions) + ")")
		  .append(".build()").toString();
	}

}
