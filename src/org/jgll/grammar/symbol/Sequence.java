package org.jgll.grammar.symbol;

import java.util.Arrays;
import java.util.List;

import org.jgll.grammar.condition.Condition;
import org.jgll.util.CollectionsUtil;

public class Sequence extends Nonterminal {

	private static final long serialVersionUID = 1L;
	
	private List<Symbol> symbols;

	public Sequence(Symbol...symbols) {
		this(Arrays.asList(symbols));
	}
	
	public Sequence(List<Symbol> symbols) {
		super(CollectionsUtil.listToString(symbols));
		this.symbols = symbols;
	}
	
	public List<Symbol> getSymbols() {
		return symbols;
	}
	
	@Override
	public Sequence addCondition(Condition condition) {
		Sequence seq = new Sequence(this.symbols);
		seq.conditions.addAll(this.conditions);
		seq.conditions.add(condition);
		return seq;
	}
	
	public Symbol symbolAt(int index) {
		return symbols.get(index);
	}

}
