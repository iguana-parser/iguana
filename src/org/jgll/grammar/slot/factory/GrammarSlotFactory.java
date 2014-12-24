package org.jgll.grammar.slot.factory;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgll.grammar.condition.Condition;
import org.jgll.grammar.slot.EndGrammarSlot;
import org.jgll.grammar.slot.EpsilonGrammarSlot;
import org.jgll.grammar.slot.NonterminalGrammarSlot;
import org.jgll.grammar.slot.TerminalGrammarSlot;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.regex.RegularExpression;

public interface GrammarSlotFactory {

	public NonterminalGrammarSlot createHeadGrammarSlot(Nonterminal nonterminal,
												 List<List<Symbol>> alternates,
												 Map<Nonterminal, Set<RegularExpression>> firstSets,
												 Map<Nonterminal, Set<RegularExpression>> followSets,
												 Map<Nonterminal, List<Set<RegularExpression>>> predictionSets);
	
	public NonterminalGrammarSlot createNonterminalGrammarSlot(Rule rule, 
															   int symbolIndex,
															   Set<Condition> preConditions,
															   Set<Condition> popConditions);

	public EndGrammarSlot createLastGrammarSlot(Rule rule,
											     int symbolIndex, 
												 List<NonterminalGrammarSlot> nonterminals, 
												 Set<Condition> popConditions);
	
	
	public EpsilonGrammarSlot createEpsilonGrammarSlot(NonterminalGrammarSlot head);
	
	
	public TerminalGrammarSlot createTokenGrammarSlot(RegularExpression regex, 
													  Set<Condition> preConditions, 
													  Set<Condition> postConditions);
}
