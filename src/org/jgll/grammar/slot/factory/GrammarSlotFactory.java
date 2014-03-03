package org.jgll.grammar.slot.factory;

import java.util.Map;
import java.util.Set;

import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.HeadGrammarSlot;
import org.jgll.grammar.slot.TokenGrammarSlot;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.regex.RegularExpression;

public interface GrammarSlotFactory {

	public HeadGrammarSlot createHeadGrammarSlot(Nonterminal nonterminal,
												 Map<Nonterminal, Set<RegularExpression>> firstSets,
												 Map<Nonterminal, Set<RegularExpression>> followSets);

	public TokenGrammarSlot createTokenGrammarSlot(int position, 
												   BodyGrammarSlot previous, 
												   RegularExpression regularExpression, 
												   HeadGrammarSlot head, 
												   int tokenID);
}
