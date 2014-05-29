package org.jgll.grammar;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.EpsilonGrammarSlot;
import org.jgll.grammar.slot.HeadGrammarSlot;
import org.jgll.grammar.slot.IntermediateNodeIds;
import org.jgll.grammar.slot.LastGrammarSlot;
import org.jgll.grammar.slot.NonterminalGrammarSlot;
import org.jgll.grammar.slot.TokenGrammarSlot;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.regex.RegularExpression;
import org.jgll.regex.automaton.RunnableAutomaton;
import org.jgll.util.Tuple;
import org.jgll.util.logging.LoggerWrapper;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public class GrammarGraph implements Serializable {
	
	private static final LoggerWrapper log = LoggerWrapper.getLogger(GrammarGraph.class);
	
	private static final long serialVersionUID = 1L;
	
	private List<HeadGrammarSlot> headGrammarSlots;
	
	private List<BodyGrammarSlot> slots;
	
	/**
	 * A map from nonterminal names to their corresponding head ‚slots.
	 * This map is used to locate head grammar slots by name for parsing
	 * from any arbitrary nonterminal.
	 */
	Map<Nonterminal, HeadGrammarSlot> nameToNonterminals;
	
	private transient Map<String, BodyGrammarSlot> nameToSlots;
	
	private String name;
	
	private int longestTerminalChain;
	
	private int maximumNumAlternates;
	
	private int maxDescriptorsAtInput;
	
	private int averageDescriptorsAtInput;
	
	private int stDevDescriptors;
	
	private Map<RegularExpression, Integer> tokenIDMap;
	
	private List<RegularExpression> tokens;
	
	private RunnableAutomaton[] dfas;
	
	private Map<Nonterminal, Set<RegularExpression>> firstSets;
	
	private Map<Nonterminal, Set<RegularExpression>> followSets;
	
	private Set<Nonterminal> ll1SubGrammarNonterminals;
	
	private Map<Nonterminal, List<Set<RegularExpression>>> predictionSets;
	
	private Map<String, Integer> nonterminalIds;
	
	private Map<Tuple<Nonterminal, List<Symbol>>, Integer> packedNodeIds;
	
	private List<Nonterminal> nonterminals;
	
	private Object[][] objects;
	
	private Grammar grammar;
	
	private IntermediateNodeIds intermediateNodeIds;
	
	public GrammarGraph(GrammarGraphBuilder builder) {
		this.name = builder.name;
		this.headGrammarSlots = builder.headGrammarSlots;
		this.slots = builder.slots;
		this.nameToNonterminals = new HashMap<>();
		this.nameToNonterminals = builder.nonterminalsMap;
		
		this.maximumNumAlternates = builder.maximumNumAlternates;
		this.maxDescriptorsAtInput = builder.maxDescriptors;
		this.averageDescriptorsAtInput = builder.averageDescriptors;
		this.stDevDescriptors = (int) builder.stDevDescriptors;
		this.tokens = builder.tokens;
		this.dfas = builder.dfas;
		this.firstSets = builder.firstSets;
		this.followSets = builder.followSets;
		this.ll1SubGrammarNonterminals = builder.ll1SubGrammarNonterminals;
		
		this.objects = builder.objects;
		
		this.predictionSets = builder.predictionSets;
		this.nonterminalIds = builder.nonterminalIds;
		this.nonterminals = builder.nonterminals;

		this.tokenIDMap = builder.tokenIDMap;
		
		this.nameToSlots = new HashMap<>();
		for(BodyGrammarSlot slot : slots) {
			nameToSlots.put(slot.getLabel(), slot);
		}
		
		grammar = builder.grammar;
		intermediateNodeIds = builder.intermediateNodeIds;
		packedNodeIds = builder.packedNodeIds;
		
		printGrammarStatistics();
	}
	
	public void printGrammarStatistics() {
		log.info("Grammar information:");
		log.info("Nonterminals: %d", headGrammarSlots.size());
		log.info("Production rules: %d", grammar.sizeRules());
		log.info("Grammar slots: %d", slots.size());
		log.debug("Longest terminal Chain: %d", longestTerminalChain);
		log.debug("Maximum number alternates: %d", maximumNumAlternates);
		log.debug("Maximum descriptors: %d", maxDescriptorsAtInput);
	}
	
	public void code(Writer writer, String packageName) throws IOException {
	}
	
	public String getName() {
		return name;
	}
	
	public HeadGrammarSlot getHeadGrammarSlot(int id) {
		return headGrammarSlots.get(id);
	}
	
	public BodyGrammarSlot getGrammarSlot(int id) {
		return slots.get(id);
	}
		
	public List<HeadGrammarSlot> getNonterminals() {
		return headGrammarSlots;
	}
	
	public List<BodyGrammarSlot> getGrammarSlots() {
		return slots;
	}
	
	public HeadGrammarSlot getHeadGrammarSlot(String name) {
		return nameToNonterminals.get(Nonterminal.withName(name));
	}
	
	private String getSlotName(BodyGrammarSlot slot) {
		if(slot instanceof TokenGrammarSlot) {
			return ((TokenGrammarSlot) slot).getSymbol().getName();
		}
		else if (slot instanceof NonterminalGrammarSlot) {
			return ((NonterminalGrammarSlot) slot).getNonterminal().getNonterminal().toString();
		} 
		else {
			return "";
		}
	}
	
	public BodyGrammarSlot getGrammarSlotByName(String name) {
		return nameToSlots.get(name);
	}
	
	public int getLongestTerminalChain() {
		return longestTerminalChain;
	}
	
	public int getMaximumNumAlternates() {
		return maximumNumAlternates;
	}
	
	public int getMaxDescriptorsAtInput() {
		return maxDescriptorsAtInput;
	}
	
	public int getAverageDescriptorsAtInput() {
		return averageDescriptorsAtInput;
	}
	
	public int getStDevDescriptors() {
		return stDevDescriptors;
	}
	
	@Override
	public String toString() {
		
		final StringBuilder sb = new StringBuilder();
		
		GrammarVisitAction action = new GrammarVisitAction() {
			
			@Override
			public void visit(LastGrammarSlot slot) {
				if(slot instanceof EpsilonGrammarSlot) {
					sb.append(" epsilon\n");
				} else {
					sb.append("\n");
				}
			}
			
			@Override
			public void visit(NonterminalGrammarSlot slot) {
				sb.append(" ").append(getSlotName(slot));
			}
			
			@Override
			public void visit(HeadGrammarSlot head) {
				sb.append(head.getNonterminal()).append(" ::= ");
			}

			@Override
			public void visit(TokenGrammarSlot slot) {
				sb.append(" ").append(getSlotName(slot));
			}

		};
		
		// The first nonterminal is the starting point
		// TODO: allow the user to specify the root of a grammar
		GrammarVisitor.visit(this, action);
		return sb.toString();
	}
	
	public RunnableAutomaton getAutomaton(int index) {
		return dfas[index];
	}
	
	public int getCountTokens() {
		return tokenIDMap.size() + 2;
	}
	
	public Iterable<RegularExpression> getTokens() {
		return tokenIDMap.keySet();
	}
	
	public Nonterminal getNonterminalById(int index) {
		return nonterminals.get(index);
	}
	
	public int getNonterminalId(Nonterminal nonterminal) {
		return nonterminalIds.get(nonterminal.getName());
	}
	
	public RegularExpression getRegularExpressionById(int index) {
		return tokens.get(index);
	}
	
	public int getRegularExpressionId(RegularExpression regex) {
		return tokenIDMap.get(regex);
	}
	
	public int getIntermediateNodeId(List<? extends Symbol> symbols) {
		return intermediateNodeIds.getSlotId(new Rule(Nonterminal.withName("n"), symbols));
	}
	
	public int getIntermediateNodeId(Symbol...symbols) {
		return intermediateNodeIds.getSlotId(new Rule(Nonterminal.withName("n"), Arrays.asList(symbols)));
	}
	
	public String getIntermediateNodeLabel(int id) {
		return intermediateNodeIds.getSlotName(id);
	}
	
	public int getPackedNodeId(Nonterminal nonterminal, List<Symbol> symbols) {
		return packedNodeIds.get(Tuple.of(nonterminal, symbols));
	}
	
	public int getPackedNodeId(Nonterminal nonterminal, Symbol...symbols) {
		return packedNodeIds.get(Tuple.of(nonterminal, Arrays.asList(symbols)));
	}
	
	public int getCountLL1Nonterminals() {
		int count = 0;
		for(HeadGrammarSlot head : headGrammarSlots) {
			if(isLL1SubGrammar(head.getNonterminal())) {
				count++;
			}
		}
		return count;
	}
	
	public boolean isLL1SubGrammar(Nonterminal nonterminal) {
		return ll1SubGrammarNonterminals.contains(nonterminal);
	}
	
	public Set<RegularExpression> getFirstSet(Nonterminal nonterminal) {
		return firstSets.get(nonterminal);
	}
	
	public Set<RegularExpression> getPredictionSetForAlternate(Nonterminal nonterminal, int index) {
		return predictionSets.get(nonterminal).get(index);
	}
	 
	public Set<RegularExpression> getFollowSet(Nonterminal nonterminal) {
		return followSets.get(nonterminal);
	}
	
	public Object getObject(int nonterminalId, int alternateId) {
		return objects[nonterminalId][alternateId];
	}
	
	public int getCountAlternates(Nonterminal nonterminal) {
		return grammar.getAlternatives(nonterminal).size();
	}
	
	public List<Symbol> getDefinition(Nonterminal nonterminal, int alternateIndex) {
		List<Symbol> list = null;
		Iterator<List<Symbol>> it = grammar.getAlternatives(nonterminal).iterator();
		for(int i = 0; i <= alternateIndex; i++) {
			list = it.next();
		}
		return list;
	}
}
