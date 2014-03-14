package org.jgll.grammar.slot.factory;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.EpsilonGrammarSlot;
import org.jgll.grammar.slot.HeadGrammarSlot;
import org.jgll.grammar.slot.LastGrammarSlot;
import org.jgll.grammar.slot.NonterminalGrammarSlot;
import org.jgll.grammar.slot.TokenGrammarSlot;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.regex.RegularExpression;

public interface GrammarSlotFactory {

	public HeadGrammarSlot createHeadGrammarSlot(Nonterminal nonterminal,
												 int nonterminalId,
												 List<List<Symbol>> alternates,
												 Map<Nonterminal, Set<RegularExpression>> firstSets,
												 Map<Nonterminal, Set<RegularExpression>> followSets,
												 Map<Nonterminal, List<Set<RegularExpression>>> predictionSets);
	
	public NonterminalGrammarSlot createNonterminalGrammarSlot(int slotId,
															   String label,
															   BodyGrammarSlot previous, 
															   HeadGrammarSlot nonterminal, 
															   HeadGrammarSlot head);

	public LastGrammarSlot createLastGrammarSlot(int slotId, String label, BodyGrammarSlot previous, HeadGrammarSlot head);
	
	
	public EpsilonGrammarSlot createEpsilonGrammarSlot(int slotId, String label, HeadGrammarSlot head);
	
	
	public TokenGrammarSlot createTokenGrammarSlot(int slotId,
												   String label,
												   BodyGrammarSlot previous, 
												   RegularExpression regularExpression, 
												   HeadGrammarSlot head, 
												   int tokenID);
}
