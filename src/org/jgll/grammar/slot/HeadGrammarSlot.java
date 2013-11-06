package org.jgll.grammar.slot;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgll.grammar.symbol.Alternate;
import org.jgll.grammar.symbol.Epsilon;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.grammar.symbol.Terminal;
import org.jgll.parser.GLLParserInternals;
import org.jgll.recognizer.GLLRecognizer;
import org.jgll.sppf.DummyNode;
import org.jgll.util.Input;

/**
 * 
 * The grammar slot corresponding to the head of a rule.
 *
 * 
 * @author Ali Afroozeh
 * 
 */
public class HeadGrammarSlot extends GrammarSlot {
	
	private static final long serialVersionUID = 1L;

	private List<Alternate> alternates;
	
	private final Nonterminal nonterminal;
	
	private boolean nullable;
	
	private boolean directNullable;
	
	private transient final Set<Terminal> firstSet;
	
	private transient final Set<Terminal> followSet;
	
	private BitSet firstSetBitSet;
	
	private BitSet followSetBitSet;
	
	private BitSet predictionSet;
	
	private Alternate epsilonAlternate;
	
	private boolean ll1;
	
	private Map<Integer, Alternate> ll1Map = new HashMap<>();
	
	public HeadGrammarSlot(Nonterminal nonterminal) {
		this.nonterminal = nonterminal;
		this.alternates = new ArrayList<>();
		this.firstSet = new HashSet<>();
		this.followSet = new HashSet<>();
	}
	
	public void addAlternate(Alternate alternate) {		
		alternates.add(alternate);
	}
	
	public void setAlternates(List<Alternate> alternates) {
		this.alternates = alternates;
	}
	
	public void removeAlternate(Alternate alternate) {
		alternates.remove(alternate);
	}
	
	public void removeAlternate(int index) {
		alternates.remove(index);
	}
	
	public Set<Alternate> without(List<Symbol> list) {
		Set<Alternate> set = new HashSet<>(alternates);
		for(Alternate alternate : alternates) {
			if(alternate.match(list)) {
				set.remove(alternate);
			}
		}
		return set;
	}
	
	public Set<Alternate> without(Set<List<Symbol>> withoutSet) {
		Set<Alternate> set = new HashSet<>(alternates);
		for(Alternate alternate : alternates) {
			for(List<Symbol> list : withoutSet) {
				if(alternate.match(list)) {
					set.remove(alternate);
				}
			}
		}
		return set;
	}
	
	public void remove(List<Symbol> list) {
		Iterator<Alternate> it = alternates.iterator();
		while(it.hasNext()) {
			Alternate alternate = it.next();
			if(alternate.match(list)) {
				it.remove();
			}
		}
	}
		
	public void removeAllAlternates() {
		alternates.clear();
	}
		
	public boolean isNullable() {
		return nullable;
	}
	
	public boolean isDirectNullable() {
		return directNullable;
	}
	
	public void setNullable(boolean nullable, boolean directNullable) {
		this.nullable = nullable;
		this.directNullable = directNullable;
	}
	
	public void setEpsilonAlternate(Alternate epsilonAlternate) {
		this.epsilonAlternate = epsilonAlternate;
	}
	
	public Alternate getEpsilonAlternate() {
		return epsilonAlternate;
	}
	
	@Override
	public GrammarSlot parse(GLLParserInternals parser, Input input) {
		if(ll1) {
			Alternate alternate = ll1Map.get(input.charAt(parser.getCurrentInputIndex()));
			parser.setCurrentSPPFNode(DummyNode.getInstance());
			return alternate.getFirstSlot().parse(parser, input);
		} else {
			for(Alternate alternate : alternates) {
				int ci = parser.getCurrentInputIndex();
				BodyGrammarSlot slot = alternate.getFirstSlot();
				if(slot.test(ci, input)) {
					parser.addDescriptor(slot);
				}
			}			
		}
		return null;
	}

	public void setLL1Properties() {
		calculateLL1();
		calculateLL1Map();
		ll1 = isLL1();
		ll1Map = getLL1Map();
	}
	
	private void calculateLL1() {
		for(Alternate alt1 : alternates) {
			for(Alternate alt2 : alternates) {
				if(!alt1.equals(alt2)) {
					if(alt1.getFirstSlot().getPredictionSet().intersects(alt2.getFirstSlot().getPredictionSet())) {
						ll1 = false;
						return;
					}
				}
			}
		}
		ll1 = true;
	}
	
	private void calculateLL1Map() {
		ll1Map = new HashMap<>();
		
		if(!ll1) {
			return;
		}
		
		for(Alternate alt : alternates) {
			BitSet bs = alt.getFirstSlot().getPredictionSet();
			for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i+1)) {
				ll1Map.put(i, alt);
			}
		}
	}
	
	public boolean isLL1() {
		return ll1;
	}
	
	public boolean isLL(int k) {
		if(k == 1) {
			return isLL1();
		} else {
			throw new UnsupportedOperationException();
		}
	}
	
	public Map<Integer, Alternate> getLL1Map() {
		return ll1Map;
	}
	
	@Override
	public GrammarSlot recognize(GLLRecognizer recognizer, Input input) {
		for(Alternate alternate : alternates) {
			int ci = recognizer.getCi();
			BodyGrammarSlot slot = alternate.getFirstSlot();
			if(slot.test(ci, input)) {
				org.jgll.recognizer.GSSNode cu = recognizer.getCu();
				recognizer.add(alternate.getFirstSlot(), cu, ci);
			}
		}
		return null;
	}
	
	@Override
	public void codeParser(Writer writer) throws IOException {
		writer.append("// " + nonterminal.getName() + "\n");
		writer.append("private void parse_" + id + "() {\n");
		for (Alternate alternate : alternates) {
			writer.append("   //" + alternate.getFirstSlot() + "\n");
			alternate.getFirstSlot().codeIfTestSetCheck(writer);			
			writer.append("   add(grammar.getGrammarSlot(" + alternate.getFirstSlot().getId() + "), cu, ci, DummyNode.getInstance());\n");
			writer.append("}\n");
		}
		writer.append("   label = L0;\n");
		writer.append("}\n");

		for (Alternate alternate : alternates) {
			writer.append("// " + alternate + "\n");
			writer.append("private void parse_" + alternate.getFirstSlot().getId() + "() {\n");
			alternate.getFirstSlot().codeParser(writer);
		}
	}
	
	public Alternate getAlternateAt(int index) {
		return alternates.get(index);
	}
	
	public List<Alternate> getAlternates() {
		return new ArrayList<>(alternates);
	}
	
	public Set<Alternate> getAlternatesAsSet() {
		return new HashSet<>(alternates);
	}
	
	public Nonterminal getNonterminal() {
		return nonterminal;
	}
		
	public Set<Terminal> getFirstSet() {
		return firstSet;
	}
	
	public Set<Terminal> getFirstSetWithoutEpsilon() {
		Set<Terminal> set = new HashSet<>(firstSet);
		set.remove(Epsilon.getInstance());
		return set;
	}
	
	public Set<Terminal> getFollowSet() {
		return followSet;
	}
	
	public BitSet getFollowSetAsBitSet() {
		return followSetBitSet;
	}
	
	public BitSet getFirstSetBitSet() {
		return firstSetBitSet;
	}
	
	public int getCountAlternates() {
		return alternates.size();
	}
	
	public void setPredictionSet() {
		
		firstSetBitSet = new BitSet();
		for(Terminal t : firstSet) {
			firstSetBitSet.or(t.asBitSet());
		}
		
		followSetBitSet = new BitSet();
		for(Terminal t : followSet) {
			followSetBitSet.or(t.asBitSet());
		}
		
		predictionSet = new BitSet();
		predictionSet.or(firstSetBitSet);
		predictionSet.or(followSetBitSet);
	}
	
	public BitSet getPredictionSet() {
		return predictionSet;
	}
	
	public boolean contains(List<Symbol> list) {
		for(Alternate alternate : alternates) {
			if(alternate.match(list)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean contains(Set<List<Symbol>> set) {
		for(List<Symbol> list : set) {
			if(contains(list)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return nonterminal.toString();
	}

}
