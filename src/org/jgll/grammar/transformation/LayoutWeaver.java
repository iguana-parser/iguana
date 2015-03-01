package org.jgll.grammar.transformation;

import java.util.Set;
import java.util.stream.Collectors;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.condition.Condition;
import org.jgll.grammar.condition.ConditionType;
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
				addLayout(layout, rule, ruleBuilder, s);
			}
			
			Symbol last = rule.symbolAt(rule.size() - 1);
			builder.addRule(ruleBuilder.addSymbol(last).build());
			if (!getNotFollowIgnoreLayout(last).isEmpty()) {
				addLayout(layout, rule, ruleBuilder, last);
			}
		}
		
		return builder.build();
	}

	private void addLayout(Nonterminal layout, Rule rule, Rule.Builder ruleBuilder, Symbol s) {
		switch (rule.getLayoutStrategy()) {
			
			case NO_LAYOUT:
				// do nothing
				break;
				
			case INHERITED:
				ruleBuilder.addSymbol(layout);
//				ruleBuilder.addSymbol(layout.copyBuilder().addPostConditions(getNotFollowIgnoreLayout(s)).build());
				break;
				
			case FIXED:
				ruleBuilder.addSymbol(rule.getLayout());
//				ruleBuilder.addSymbol(rule.getLayout().copyBuilder().addPostConditions(getNotFollowIgnoreLayout(s)).build());
				break;
		}
	}
	
	private Set<Condition> getNotFollowIgnoreLayout(Symbol s) {
		return s.getPostConditions().stream().filter(c -> c.getType() == ConditionType.NOT_FOLLOW_IGNORE_LAYOUT).collect(Collectors.toSet());
	}
	
}
