package org.jgll.grammar.transformation;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.symbol.LayoutStrategy;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.grammar.symbol.Symbol;

public class LayoutWeaver implements GrammarTransformation {

	@Override
	public Grammar transform(Grammar grammar) {
		Nonterminal layout = grammar.getLayout();
		
		Grammar.Builder builder = Grammar.builder();
		
		for (Rule rule : grammar.getRules()) {
			Rule.Builder ruleBuilder = Rule.withHead(rule.getHead());
			
			for (int i = 0; i < rule.size() - 1; i++) {
				Symbol s = rule.symbolAt(i);
				ruleBuilder.addSymbol(s);
				if (rule.getLayoutStrategy() == LayoutStrategy.NO_LAYOUT) {
					// do nothing
				} else if (rule.getLayoutStrategy() == LayoutStrategy.INHERITED) {
					ruleBuilder.addSymbol(layout);
				}  else {
					ruleBuilder.addSymbol(rule.getLayout());
				}
			}
			builder.addRule(ruleBuilder.addSymbol(rule.symbolAt(rule.size() - 1)).build());
		}
		
		return builder.build();
	}
	
}
