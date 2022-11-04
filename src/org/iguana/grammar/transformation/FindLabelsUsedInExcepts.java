package org.iguana.grammar.transformation;

import org.iguana.grammar.runtime.RuntimeGrammar;
import org.iguana.grammar.runtime.RuntimeRule;
import org.iguana.grammar.symbol.*;
import org.iguana.grammar.symbol.Error;
import org.iguana.traversal.ISymbolVisitor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FindLabelsUsedInExcepts implements ISymbolVisitor<Void> {
	
	private final Map<String, Set<String>> labels = new HashMap<>();
	
	public Map<String, Set<String>> getLables() {
		return labels;
	}
	
	public void compute(RuntimeGrammar grammar) {
		for (RuntimeRule rule : grammar.getRules())
			compute(rule);
	}
	
	public void compute(RuntimeRule rule) {
		
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
	// Currently, also expected to be desugared as part of data-dependent EBNF constructs
	public Void visit(Block symbol) {
		throw new RuntimeException("Unexpected symbol: " + symbol);
	}

	@Override
	public Void visit(Code symbol) {
		symbol.getSymbol().accept(this);
		return null;
	}

	@Override
	public Void visit(Error error) {
		return null;
	}

	@Override
	public Void visit(Conditional symbol) {
		symbol.getSymbol().accept(this);
		return null;
	}

	@Override
	// Currently, also expected to be desugared as part of data-dependent EBNF constructs
	public Void visit(IfThen symbol) {
		throw new RuntimeException("Unexpected symbol: " + symbol);
	}

	@Override
	// Currently, also expected to be desugared as part of data-dependent EBNF constructs
	public Void visit(IfThenElse symbol) {
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
	// Currently, also expected to be desugared as part of data-dependent EBNF constructs
	public Void visit(While symbol) {
		throw new RuntimeException("Unexpected symbol: " + symbol);
	}

	@Override
	public Void visit(Return symbol) {
		return null;
	}

	@Override
	public Void visit(Alt symbol) {
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
	public Void visit(Group symbol) {
		throw new RuntimeException("Unexpected symbol: " + symbol);
	}

	@Override
	public Void visit(Star symbol) {
		throw new RuntimeException("Unexpected symbol: " + symbol);
	}

    @Override
    public Void visit(Start symbol) {
        throw new RuntimeException("Unexpected symbol: " + symbol);
    }

}
