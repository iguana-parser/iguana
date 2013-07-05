package org.jgll.grammar;

import java.util.List;

public class Condition {
	
	private List<? extends Symbol> symbols;

	public Condition(List<? extends Symbol> symbols) {
		this.symbols = symbols;
	}
	
}