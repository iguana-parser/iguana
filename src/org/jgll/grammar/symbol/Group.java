package org.jgll.grammar.symbol;

import java.util.Arrays;
import java.util.List;

import org.jgll.grammar.GrammarSlotRegistry;
import org.jgll.util.generator.GeneratorUtil;

import com.google.common.collect.ImmutableList;

public class Group extends AbstractSymbol {

	private static final long serialVersionUID = 1L;

	private final List<? extends Symbol> symbols;

	private Group(Builder builder) {
		super(builder);
		this.symbols = ImmutableList.copyOf(builder.symbols);
	}
	
	@SafeVarargs
	public static <T extends Symbol> Group of(T...symbols) {
		return new Builder(Arrays.asList(symbols)).build();
	}
	
	public static <T extends Symbol> Group of(List<Symbol> symbols) {
		return new Builder(symbols).build();
	}

	public List<? extends Symbol> getSymbols() {
		return symbols;
	}

	@Override
	public String getConstructorCode(GrammarSlotRegistry registry) {
		return new StringBuilder()
		  .append("new Group.builder(" + getConstructorCode(symbols, registry) + ")")
		  .append(".setLabel(" + label + ")")
		  .append(".setObject(" + object + ")")
		  .append(".setPreConditions(" + getConstructorCode(preConditions, registry) + ")")
		  .append(".setPostConditions(" + getConstructorCode(postConditions, registry) + ")")
		  .append(".build()")
		  .toString();
	}
	
	public static Builder builder(List<? extends Symbol> symbols) {
		return new Builder(symbols);
	}

	@SafeVarargs
	public static <T extends Symbol> Builder builder(T...symbols) {
		return new Builder(Arrays.asList(symbols));
	}	
	
	public static class Builder extends SymbolBuilder<Group> {
		
		private List<? extends Symbol> symbols;
		
		public Builder(List<? extends Symbol> symbols) {
			super("(" + GeneratorUtil.listToString(symbols) + ")");
			this.symbols = symbols;
		}
		
		public Builder(Group group) {
			super(group);
			this.symbols = group.symbols;
		}

		@Override
		public Group build() {
			return new Group(this);
		}
	}
	
}
