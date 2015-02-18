package org.jgll.grammar.transformation;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.grammar.symbol.Symbol;

public class LayoutWeaver implements GrammarTransformation {

	@Override
	public Grammar transform(Grammar grammar) {
		Nonterminal layout = grammar.getLayout();
		
		Grammar.Builder builder = Grammar.builder().setLayout(layout);
		
		for (Rule rule : grammar.getRules()) {
			
			Rule.Builder ruleBuilder = Rule.withHead(rule.getHead());

			if (rule.size() == 0) {
				builder.addRule(ruleBuilder.build());
				continue;
			}
			
			for (int i = 0; i < rule.size() - 1; i++) {
				Symbol s = rule.symbolAt(i);
				ruleBuilder.addSymbol(s);
				
				switch (rule.getLayoutStrategy()) {
					
					case NO_LAYOUT:
						// do nothing
						break;
						
					case INHERITED:
						ruleBuilder.addSymbol(layout);
						break;
						
					case FIXED:
						ruleBuilder.addSymbol(rule.getLayout());
						break;
				}
			}
			
			builder.addRule(ruleBuilder.addSymbol(rule.symbolAt(rule.size() - 1)).build());
		}
		
		return builder.build();
	}
	
}
