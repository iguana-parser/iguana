package org.iguana.grammar.patterns;

import java.util.List;

import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Rule;
import org.iguana.grammar.symbol.Symbol;
import org.iguana.util.generator.ConstructorCode;

public class ExceptPattern extends AbstractPattern implements ConstructorCode {

	private static final long serialVersionUID = 1L;

	public ExceptPattern(Nonterminal nonteriminal, List<Symbol> parent, int position, List<Symbol> child) {
		super(nonteriminal, parent, position, child);
	}
	
	public static ExceptPattern from(Rule parent, int position, Rule child) {
		return new ExceptPattern(parent.getHead(), parent.getBody(), position, child.getBody());
	}
	
	@Override
	public String getConstructorCode() {
		return "new " + ExceptPattern.class.getSimpleName() + "(" + super.getConstructorCode() + ")";
	}
}
