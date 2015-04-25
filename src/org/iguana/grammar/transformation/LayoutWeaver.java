package org.iguana.grammar.transformation;

import java.util.Set;
import java.util.stream.Collectors;

import org.iguana.grammar.Grammar;
import org.iguana.grammar.condition.Condition;
import org.iguana.grammar.condition.ConditionType;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Rule;
import org.iguana.grammar.symbol.Symbol;

public class LayoutWeaver implements GrammarTransformation {

	@Override
	public Grammar transform(Grammar grammar) {
		Nonterminal layout = grammar.getLayout();
		
		Grammar.Builder builder = Grammar.builder().setLayout(layout);
		
		for (Rule rule : grammar.getRules()) {
			
			Rule.Builder ruleBuilder = Rule.withHead(rule.getHead())
												.setRecursion(rule.getRecursion())
												.setAssociativity(rule.getAssociativity())
												.setAssociativityGroup(rule.getAssociativityGroup())
												.setPrecedence(rule.getPrecedence())
												.setPrecedenceLevel(rule.getPrecedenceLevel())
												.setLabel(rule.getLabel());

			if (rule.size() == 0) {
				builder.addRule(ruleBuilder.build());
				continue;
			}
			
			for (int i = 0; i < rule.size() - 1; i++) {
				Symbol s = rule.symbolAt(i);
				Set<Condition> ignoreLayoutConditions = getIgnoreLayoutConditions(s);
				
				if (ignoreLayoutConditions.isEmpty())
					ruleBuilder.addSymbol(s);
				else 
					ruleBuilder.addSymbol(s.copyBuilder().removePostConditions(ignoreLayoutConditions).build());
				
				addLayout(layout, rule, ruleBuilder, s);
			}
			
			Symbol last = rule.symbolAt(rule.size() - 1);
			Set<Condition> ignoreLayoutConditions = getIgnoreLayoutConditions(last);

			if (ignoreLayoutConditions.isEmpty())
				ruleBuilder.addSymbol(last);
			else 
				ruleBuilder.addSymbol(last.copyBuilder().removePostConditions(ignoreLayoutConditions).build());
			
			if (!ignoreLayoutConditions.isEmpty()) {
				addLayout(layout, rule, ruleBuilder, last);
			}
			
			builder.addRule(ruleBuilder.build());
		}
		
		return builder.build();
	}

	private void addLayout(Nonterminal layout, Rule rule, Rule.Builder ruleBuilder, Symbol s) {
		switch (rule.getLayoutStrategy()) {
			
			case NO_LAYOUT:
				// do nothing
				break;
				
			case INHERITED:
				ruleBuilder.addSymbol(layout.copyBuilder().addPostConditions(getIgnoreLayoutConditions(s)).build());
				break;
				
			case FIXED:
				ruleBuilder.addSymbol(rule.getLayout().copyBuilder().addPostConditions(getIgnoreLayoutConditions(s)).build());
				break;
		}
	}
	
	private Set<Condition> getIgnoreLayoutConditions(Symbol s) {
		return s.getPostConditions().stream()
				.filter(c -> c.getType() == ConditionType.NOT_FOLLOW_IGNORE_LAYOUT || c.getType() == ConditionType.FOLLOW_IGNORE_LAYOUT)
				.collect(Collectors.toSet());
	}
	
}
