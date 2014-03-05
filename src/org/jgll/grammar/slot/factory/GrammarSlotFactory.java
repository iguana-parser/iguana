package org.jgll.grammar.slot.factory;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.EpsilonGrammarSlot;
import org.jgll.grammar.slot.HeadGrammarSlot;
import org.jgll.grammar.slot.LastGrammarSlot;
import org.jgll.grammar.slot.NonterminalGrammarSlot;
import org.jgll.grammar.slot.TokenGrammarSlot;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.regex.RegularExpression;

public interface GrammarSlotFactory {

	public HeadGrammarSlot createHeadGrammarSlot(Nonterminal nonterminal,
												 Map<Nonterminal, Set<RegularExpression>> firstSets,
												 Map<Nonterminal, Set<RegularExpression>> followSets);
	
	public NonterminalGrammarSlot createNonterminalGrammarSlot(Rule rule, int position, int slotId,
															   String label,
															   BodyGrammarSlot previous, 
															   HeadGrammarSlot nonterminal, 
															   HeadGrammarSlot head);

	public LastGrammarSlot createLastGrammarSlot(Rule rule, int position, int slotId, String label, BodyGrammarSlot previous, HeadGrammarSlot head, Serializable object);
	
	
	public EpsilonGrammarSlot createEpsilonGrammarSlot(Rule rule, int position, int slotId, String label, HeadGrammarSlot head, Serializable object);
	
	
	public TokenGrammarSlot createTokenGrammarSlot(Rule rule, 
												   int position, 
												   int slotId,
												   String label,
												   BodyGrammarSlot previous, 
												   RegularExpression regularExpression, 
												   HeadGrammarSlot head, 
												   int tokenID);
}
