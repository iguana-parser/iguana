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
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.lexer.GLLLexer;
import org.jgll.parser.GLLParser;
import org.jgll.recognizer.GLLRecognizer;
import org.jgll.sppf.DummyNode;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.SPPFNode;

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
	
	private BitSet firstSet;
	
	private BitSet followSet;
	
	private BitSet predictionSet;
	
	/**
	 * A map from each token to the set of alternates that can start with that token
	 */
	private BodyGrammarSlot[][] alternatesMap;
	
	private boolean ll1SubGrammar;
	
	public HeadGrammarSlot(Nonterminal nonterminal) {
		this.nonterminal = nonterminal;
		this.alternates = new ArrayList<>();
		this.firstSet = new BitSet();
		this.followSet = new BitSet();
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
	
	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	public void setPredictionSet(BitSet predictionSet) {
		this.predictionSet = predictionSet;
	}
	
	public void createAlternateMaps(int tokensCount) {
		Map<Integer, Set<BodyGrammarSlot>> map = new HashMap<>();
		for(Alternate alternate : alternates) {
			BitSet bs = alternate.getFirstSlot().getPredictionSet();
			for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i+1)) {
				Set<BodyGrammarSlot> set = map.get(i);
				if(set == null) {
					set = new HashSet<>();
					map.put(i, set);
				}
				set.add(alternate.getFirstSlot());
				map.put(i, set);
			}
		}
		
		alternatesMap = new BodyGrammarSlot[tokensCount][];
		for(int i = 0; i < alternatesMap.length; i++) {
			Set<BodyGrammarSlot> set = map.get(i);

			if(set == null) {
				alternatesMap[i] = new BodyGrammarSlot[0];
			} else {
				int j = 0;
				alternatesMap[i] = new BodyGrammarSlot[set.size()];
				for(BodyGrammarSlot slot : set) {
					alternatesMap[i][j++] = slot;
				}
			}
			
		}
	}
	
	@Override
	public GrammarSlot parse(GLLParser parser, GLLLexer lexer) {
		
		int ci = parser.getCurrentInputIndex();
				
		List<Integer> tokens = lexer.tokensAt(ci, this.predictionSet);
		
		if(tokens.size() == 0) {
			return null;
		} 
		else if(tokens.size() == 1) {
			BodyGrammarSlot[] slots = alternatesMap[tokens.get(0)];
			if(slots.length == 1) {
				BodyGrammarSlot slot = slots[0];
				parser.setCurrentSPPFNode(DummyNode.getInstance());
				return slot.parse(parser, lexer);				
			}
		}
		else {
			for(Integer i : tokens) {
				for(BodyGrammarSlot slot : alternatesMap[i]) {
					parser.addDescriptor(slot);
				}
			}
		}
		
		return null;
	}
	
	@Override
	public SPPFNode parseLL1(GLLParser parser, GLLLexer lexer) {
		int ci = parser.getCurrentInputIndex();
		List<Integer> tokensAt = lexer.tokensAt(ci, this.predictionSet);
		Alternate alternate = null;
		
		assert alternate != null;
		
		List<SPPFNode> children = new ArrayList<>();
		
		BodyGrammarSlot currentSlot = alternate.getFirstSlot();
		
		while(!(currentSlot instanceof LastGrammarSlot)) {
			SPPFNode node = currentSlot.parseLL1(parser, lexer);
			if(node == null) {
				return null;
			}
			children.add(node);
			currentSlot = currentSlot.next();
		}

		int leftExtent;
		int rightExtent;
		
		if(children.size() == 0) {
			leftExtent = parser.getCurrentInputIndex();
			rightExtent = leftExtent;
		}
		else if(children.size() == 1) {
			leftExtent = children.get(0).getLeftExtent();
			rightExtent = children.get(0).getRightExtent();
		} else {
			leftExtent = children.get(0).getLeftExtent();
			rightExtent = children.get(children.size() - 1).getRightExtent();
		}

		NonPackedNode ntNode = parser.getLookupTable().hasNonPackedNode(this, leftExtent, rightExtent);
		
		if(ntNode == null) {
			ntNode = parser.getLookupTable().getNonPackedNode(this, leftExtent, rightExtent); 
			
			for(SPPFNode node : children) {
				ntNode.addChild(node);
			}
		}
		
		return ntNode;
	}
	
	public boolean isLl1SubGrammar() {
		return ll1SubGrammar;
	}
	
	public void setLl1SubGrammar(boolean ll1SubGrammar) {
		this.ll1SubGrammar = ll1SubGrammar;
	}
	
	@Override
	public GrammarSlot recognize(GLLRecognizer recognizer, GLLLexer lexer) {
		for(Alternate alternate : alternates) {
			int ci = recognizer.getCi();
			BodyGrammarSlot slot = alternate.getFirstSlot();
			if(slot.test(ci, lexer)) {
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
		
	public BitSet getFirstSet() {
		return firstSet;
	}
	
	public BitSet getFirstSetWithoutEpsilon() {
		BitSet set = new BitSet();
		set.or(firstSet);
		set.clear(0);
		return set;
	}
	
	public BitSet getFollowSet() {
		return followSet;
	}
	
	public BitSet getFollowSetAsBitSet() {
		return followSet;
	}
	
	public BitSet getFirstSetBitSet() {
		return firstSet;
	}
	
	public int getCountAlternates() {
		return alternates.size();
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
