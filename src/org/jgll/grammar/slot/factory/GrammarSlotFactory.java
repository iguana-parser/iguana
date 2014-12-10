package org.jgll.grammar.slot.factory;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgll.grammar.condition.Condition;
import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.EpsilonGrammarSlot;
import org.jgll.grammar.slot.HeadGrammarSlot;
import org.jgll.grammar.slot.LastGrammarSlot;
import org.jgll.grammar.slot.NonterminalGrammarSlot;
import org.jgll.grammar.slot.TerminalGrammarSlot;
import org.jgll.grammar.slot.TokenGrammarSlot;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.regex.RegularExpression;

public interface GrammarSlotFactory {

	public HeadGrammarSlot createHeadGrammarSlot(Nonterminal nonterminal,
												 List<List<Symbol>> alternates,
												 Map<Nonterminal, Set<RegularExpression>> firstSets,
												 Map<Nonterminal, Set<RegularExpression>> followSets,
												 Map<Nonterminal, List<Set<RegularExpression>>> predictionSets);
	
	public NonterminalGrammarSlot createNonterminalGrammarSlot(Rule rule, 
															   int symbolIndex,
															   BodyGrammarSlot previous, 
															   HeadGrammarSlot nonterminal,
															   Set<Condition> preConditions,
															   Set<Condition> popConditions);

	public LastGrammarSlot createLastGrammarSlot(Rule rule,
											     int symbolIndex, 
												 BodyGrammarSlot previous, 
												 HeadGrammarSlot head, 
												 Set<Condition> popConditions);
	
	
	public EpsilonGrammarSlot createEpsilonGrammarSlot(HeadGrammarSlot head);
	
	
	public TokenGrammarSlot createTokenGrammarSlot(Rule rule,
												   int symbolIndex, 
												   BodyGrammarSlot previous, 
												   TerminalGrammarSlot terminalSlot, 
												   Set<Condition> preConditions,
												   Set<Condition> postConditions,
												   Set<Condition> popConditions);
}
