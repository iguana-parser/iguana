package org.jgll.grammar;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
import org.jgll.regex.Matcher;
import org.jgll.regex.RegularExpression;
import org.jgll.util.logging.LoggerWrapper;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public class Grammar implements Serializable {
	
	private static final LoggerWrapper log = LoggerWrapper.getLogger(Grammar.class);
	
	private static final long serialVersionUID = 1L;
	
	private List<HeadGrammarSlot> nonterminals;
	
	private List<BodyGrammarSlot> slots;
	
	/**
	 * A map from nonterminal names to their corresponding head ‚slots.
	 * This map is used to locate head grammar slots by name for parsing
	 * from any arbitrary nonterminal.
	 */
	Map<Nonterminal, HeadGrammarSlot> nameToNonterminals;
	
	private transient Map<String, BodyGrammarSlot> nameToSlots;
	
	private Map<Nonterminal, List<HeadGrammarSlot>> newNonterminalsMap;
	
	private Set<HeadGrammarSlot> newNonterminals;
	
	private String name;
	
	private int longestTerminalChain;
	
	private int maximumNumAlternates;
	
	private int maxDescriptorsAtInput;
	
	private int averageDescriptorsAtInput;
	
	private int stDevDescriptors;
	
	private Map<HeadGrammarSlot, Set<HeadGrammarSlot>> reachabilityGraph;
	
	private Map<RegularExpression, Integer> tokenIDMap;
	
	private List<RegularExpression> tokens;
	
	private List<Matcher> matchers;
	
	private Matcher[] dfas;
	
	private Map<Nonterminal, Set<RegularExpression>> firstSets;
	
	private Map<Nonterminal, Set<RegularExpression>> followSets;
	
	public Grammar(GrammarBuilder builder) {
		this.name = builder.name;
		this.nonterminals = builder.nonterminals;
		this.slots = builder.slots;
		this.nameToNonterminals = new HashMap<>();
		this.nameToNonterminals = builder.nonterminalsMap;
		
		this.newNonterminalsMap = builder.newNonterminalsMap;
		
		this.newNonterminals = new HashSet<>();
		for(List<HeadGrammarSlot> newNonterminals : builder.newNonterminalsMap.values()) {
			this.newNonterminals.addAll(newNonterminals);
		}

		this.maximumNumAlternates = builder.maximumNumAlternates;
		this.maxDescriptorsAtInput = builder.maxDescriptors;
		this.averageDescriptorsAtInput = builder.averageDescriptors;
		this.stDevDescriptors = (int) builder.stDevDescriptors;
		this.reachabilityGraph = builder.directReachabilityGraph;
		this.tokenIDMap = builder.tokenIDMap;
		this.tokens = builder.tokens;
		this.dfas = builder.dfas;
		this.firstSets = builder.firstSets;
		this.followSets = builder.followSets;
		
		this.matchers = new ArrayList<>();
		for(RegularExpression regex : tokens) {
			matchers.add(regex.toAutomaton().getMatcher());
		}
		
		printGrammarStatistics();
	}
	
	public Grammar init() {
		this.nameToSlots = new HashMap<>();

		for(BodyGrammarSlot slot : slots) {
			String label = grammarSlotToString(slot);
			slot.setLabel(label);
			nameToSlots.put(label, slot);
		}		
		
		return this;
	}

	public void printGrammarStatistics() {
		log.info("Grammar information:");
		log.info("Nonterminals: %d", nonterminals.size());
		log.info("Production rules: %d", numProductions());
		log.info("Grammar slots: %d", slots.size());
		log.debug("Longest terminal Chain: %d", longestTerminalChain);
		log.debug("Maximum number alternates: %d", maximumNumAlternates);
		log.debug("Maximum descriptors: %d", maxDescriptorsAtInput);
		log.trace("Average descriptors: %d", averageDescriptorsAtInput);
		log.trace("Standard Deviation descriptors: %d", stDevDescriptors);
	}
	
	public Set<HeadGrammarSlot> getReachableNonterminals(HeadGrammarSlot nonterminal) {
		return reachabilityGraph.get(nonterminal);
	}
	
	private int numProductions() {
		int num = 0;
		for(HeadGrammarSlot head : nonterminals) {
			num += head.getCountAlternates();
		}
		return num;
	}
	
	public void code(Writer writer, String packageName) throws IOException {
	}
	
	public String getName() {
		return name;
	}
	
	public HeadGrammarSlot getNonterminal(int id) {
		return nonterminals.get(id);
	}
		
	public BodyGrammarSlot getGrammarSlot(int id) {
		return slots.get(id);
	}
		
	public List<HeadGrammarSlot> getNonterminals() {
		return nonterminals;
	}
	
	public List<BodyGrammarSlot> getGrammarSlots() {
		return slots;
	}
	
	public HeadGrammarSlot getHeadGrammarSlot(String name) {
		return nameToNonterminals.get(new Nonterminal(name));
	}
	
	public HeadGrammarSlot getNonterminalByNameAndIndex(String name, int index) {
		return newNonterminalsMap.get(new Nonterminal(name)).get(index - 1);
	}
	
	public boolean isNewNonterminal(HeadGrammarSlot head) {
		return newNonterminals.contains(head);
	}
	
	public int getIndex(HeadGrammarSlot head) {
		List<HeadGrammarSlot> list = newNonterminalsMap.get(head.getNonterminal());
		if(list == null) {
			return -1;
		}
		return newNonterminalsMap.get(head.getNonterminal()).indexOf(head) + 1;
	}
	
	private String grammarSlotToString(BodyGrammarSlot slot) {
		
		StringBuilder sb = new StringBuilder();
		
		BodyGrammarSlot current = slot;
		sb.append(" . ");
		sb.append(getSlotName(current)).append(" ");
		
		current = slot.previous();

		while(current != null) {
			sb.insert(0, " " + getSlotName(current));
			current = current.previous();
		}
		
		current = slot.next();

		while(current != null) {
			sb.append(getSlotName(current)).append(" ");
			current = current.next();
		}
		
		sb.delete(sb.length() - 2, sb.length());

		sb.insert(0, " ::=");
		sb.insert(0, getNonterminalName(slot.getHead()));
		return sb.toString();
	}
	
	private String getSlotName(BodyGrammarSlot slot) {
		if(slot instanceof TokenGrammarSlot) {
			return ((TokenGrammarSlot) slot).getSymbol().getName();
		}
		else if (slot instanceof NonterminalGrammarSlot) {
			return getNonterminalName(((NonterminalGrammarSlot) slot).getNonterminal());
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
				sb.append(getNonterminalName(head)).append(" ::= ");
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
	
	/**
	 * If the nonterminal is introduced while rewriting, adds its index to it. 
	 */
	public String getNonterminalName(HeadGrammarSlot head) {
		String name = head.getNonterminal().getName();
		return newNonterminals.contains(head) ? name + (newNonterminalsMap.get(head.getNonterminal()).indexOf(head) + 1) : name;			
	}
	
	public int getTokenID(Symbol s) {
		return tokenIDMap.get(s);
	}
	
	public Matcher getMatcher(int index) {
		return matchers.get(index);
	}
	
	public int getCountTokens() {
		return tokenIDMap.size() + 2;
	}
	
	public Iterable<RegularExpression> getTokens() {
		return tokenIDMap.keySet();
	}
	
	public RegularExpression getRegularExpressionById(int index) {
		return tokens.get(index);
	}
	
	public int getCountLL1Nonterminals() {
		int count = 0;
		for(HeadGrammarSlot head : nonterminals) {
			if(head.isLL1SubGrammar()) {
				count++;
			}
		}
		return count;
	}
	
	public Matcher getDFA(int id) {
		return dfas[id];
	}
	
	public Set<RegularExpression> getFirstSet(Nonterminal nonterminal) {
		return firstSets.get(nonterminal);
	}
	
	public Set<RegularExpression> getFollowSet(Nonterminal nonterminal) {
		return followSets.get(nonterminal);
	}

	
}
