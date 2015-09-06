package org.iguana.grammar.transformation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.iguana.grammar.Grammar;
import org.iguana.grammar.symbol.Align;
import org.iguana.grammar.symbol.Block;
import org.iguana.grammar.symbol.Character;
import org.iguana.grammar.symbol.CharacterRange;
import org.iguana.grammar.symbol.Code;
import org.iguana.grammar.symbol.Conditional;
import org.iguana.grammar.symbol.EOF;
import org.iguana.grammar.symbol.Epsilon;
import org.iguana.grammar.symbol.IfThen;
import org.iguana.grammar.symbol.IfThenElse;
import org.iguana.grammar.symbol.Ignore;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Offside;
import org.iguana.grammar.symbol.Return;
import org.iguana.grammar.symbol.Rule;
import org.iguana.grammar.symbol.Symbol;
import org.iguana.grammar.symbol.Terminal;
import org.iguana.grammar.symbol.While;
import org.iguana.regex.Alt;
import org.iguana.regex.Opt;
import org.iguana.regex.Plus;
import org.iguana.regex.Sequence;
import org.iguana.regex.Star;
import org.iguana.traversal.ISymbolVisitor;

public class FindLabelsUsedInExcepts implements ISymbolVisitor<Void> {
	
	private final Map<String, Set<String>> labels = new HashMap<>();
	
	public Map<String, Set<String>> getLables() {
		return labels;
	}
	
	public void compute(Grammar grammar) {
		for (Rule rule : grammar.getRules())
			compute(rule);
	}
	
	public void compute(Rule rule) {
		
		if (rule.getBody() == null)
			return;
		
		for (Symbol symbol : rule.getBody())
			symbol.accept(this);
	}

	@Override
	public Void visit(Align symbol) {
		symbol.getSymbol().accept(this);
		return null;
	}

	@Override
	public Void visit(Block symbol) { // Currently, also expected to be desugared as part of data-dependent EBNF constructs
		throw new RuntimeException("Unexpected symbol: " + symbol);
	}

	@Override
	public Void visit(Character symbol) {
		return null;
	}

	@Override
	public Void visit(CharacterRange symbol) {
		return null;
	}

	@Override
	public Void visit(Code symbol) {
		symbol.getSymbol().accept(this);
		return null;
	}

	@Override
	public Void visit(Conditional symbol) {
		symbol.getSymbol().accept(this);
		return null;
	}

	@Override
	public Void visit(EOF symbol) {
		return null;
	}

	@Override
	public Void visit(Epsilon symbol) {
		return null;
	}

	@Override
	public Void visit(IfThen symbol) { // Currently, also expected to be desugared as part of data-dependent EBNF constructs
		throw new RuntimeException("Unexpected symbol: " + symbol);
	}

	@Override
	public Void visit(IfThenElse symbol) { // Currently, also expected to be desugared as part of data-dependent EBNF constructs
		throw new RuntimeException("Unexpected symbol: " + symbol);
	}

	@Override
	public Void visit(Ignore symbol) {
		symbol.getSymbol().accept(this);
		return null;
	}

	@Override
	public Void visit(Nonterminal symbol) {
		Set<String> excepts = symbol.getExcepts();
		if (excepts != null && !excepts.isEmpty()) {
			Set<String> ls = labels.get(symbol.getName());
			if (ls == null) {
				ls = new HashSet<>();
				labels.put(symbol.getName(), ls);
			}
			ls.addAll(excepts);
		}
		return null;
	}

	@Override
	public Void visit(Offside symbol) {
		symbol.getSymbol().accept(this);
		return null;
	}

	@Override
	public Void visit(Terminal symbol) {
		return null;
	}

	@Override
	public Void visit(While symbol) { // Currently, also expected to be desugared as part of data-dependent EBNF constructs
		throw new RuntimeException("Unexpected symbol: " + symbol);
	}

	@Override
	public Void visit(Return symbol) {
		return null;
	}

	@Override
	public <E extends Symbol> Void visit(Alt<E> symbol) {
		throw new RuntimeException("Unexpected symbol: " + symbol);
	}

	@Override
	public Void visit(Opt symbol) {
		throw new RuntimeException("Unexpected symbol: " + symbol);
	}

	@Override
	public Void visit(Plus symbol) {
		throw new RuntimeException("Unexpected symbol: " + symbol);
	}

	@Override
	public <E extends Symbol> Void visit(Sequence<E> symbol) {
		throw new RuntimeException("Unexpected symbol: " + symbol);
	}

	@Override
	public Void visit(Star symbol) {
		throw new RuntimeException("Unexpected symbol: " + symbol);
	}

}
