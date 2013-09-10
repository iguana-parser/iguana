package org.jgll.grammar.patterns;

import java.util.List;

import org.jgll.grammar.Symbol;

public class ExceptPattern extends AbstractPattern {

	private static final long serialVersionUID = 1L;

	public ExceptPattern(String nonteriminal, List<Symbol> parent, int position, List<Symbol> child) {
		super(nonteriminal, parent, position, child);
	}
}
