package org.jgll.grammar.slot;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jgll.grammar.symbol.Alternate;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.lexer.GLLLexer;
import org.jgll.parser.GLLParser;
import org.jgll.recognizer.GLLRecognizer;
import org.jgll.regex.Automaton;
import org.jgll.regex.RegularExpression;
import org.jgll.regex.StateAction;
import org.jgll.regex.Matcher;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.PackedNode;
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
	
	private boolean ll1;
	
	private BitSet firstSet;
	
	private BitSet followSet;
	
	private Matcher matcher;
	
	private BitSet predictionSet;
		
	private boolean ll1SubGrammar;
	
	private Alternate[] ll1Map;
	
	private transient GLLParser parser;
	
	private transient GLLLexer lexer;
	
	private transient int ci;
	
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

	@Override
	public GrammarSlot parse(GLLParser parser, GLLLexer lexer) {
		
		this.parser = parser;
		this.lexer = lexer;
		ci = parser.getCurrentInputIndex();
		
		// Don't create the descriptor and jump to the beginning of the slot
//		if(isLL1()) {
//			BodyGrammarSlot slot = alternatesMap[tokens.get(0)][0];
//			parser.setCurrentSPPFNode(DummyNode.getInstance());
//			return slot.parse(parser, lexer);				
//		}
		
//		for (Alternate alt : alternates) {
//			if(lexer.match(ci, alt.getMatcher())) {
//				parser.addDescriptor(alt.getFirstSlot());
//			}
//		}

		matcher.match(lexer.getInput(), ci);
		
		return null;
	}
	
	
	@Override
	public SPPFNode parseLL1(GLLParser parser, GLLLexer lexer) {
		int ci = parser.getCurrentInputIndex();
		
		List<SPPFNode> children = new ArrayList<>();
		
		int t = lexer.tokenAt(ci, this.predictionSet);
		
		if(t == -1) {
			return null;
		}
		
		BodyGrammarSlot currentSlot = ll1Map[t].getFirstSlot();
		
		LastGrammarSlot lastSlot = null;
		
		while(!(currentSlot instanceof LastGrammarSlot)) {
			SPPFNode node = currentSlot.parseLL1(parser, lexer);
			if(node == null) {
				return null;
			}
			children.add(node);
			currentSlot = currentSlot.next();
		}
		
		lastSlot = (LastGrammarSlot) currentSlot;

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
			
			ntNode.addFirstPackedNode(new PackedNode(lastSlot, ci, ntNode));
		}
		
		return ntNode;
	}
	
	public boolean isLL1SubGrammar() {
		return ll1SubGrammar;
	}
	
	public void setLL1SubGrammar(boolean ll1SubGrammar) {
		this.ll1SubGrammar = ll1SubGrammar;
	}
	
	public boolean isLL1() {
		return ll1;
	}
	
	public void setLL1(boolean ll1) {
		this.ll1 = ll1;
	}
	
	public void setLL1Map(int countTokens) {
		ll1Map = new Alternate[countTokens];
		
		outer:
		for(int i = 0; i < countTokens; i++) {
			for(Alternate alternate : alternates) {
				if(alternate.getPredictionSet().get(i)) {
					if(ll1Map[i] != null) {
						throw new RuntimeException("Something is not right here!");
					} else {
						ll1Map[i] = alternate;
						continue outer;
					}
				}
			}			
		}
	}
	
	@Override
	public GrammarSlot recognize(GLLRecognizer recognizer, GLLLexer lexer) {
		int ci = recognizer.getCi();
		
		for(Alternate alternate : alternates) {
			// TODO: put the check for recognizers here.
//			if(lexer.match(ci, alternate.getMatcher())) {
				org.jgll.recognizer.GSSNode cu = recognizer.getCu();
				recognizer.add(alternate.getFirstSlot(), cu, ci);
//			}
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
	
	public Matcher getPredictionSetAutomaton() {
		return matcher;
	}
	
	public BitSet getPredictionSet() {
		return predictionSet;
	}
	
	public void setPredictionSet(Automaton a, List<RegularExpression> regularExpressions) {
		
		for(final Alternate alternate : alternates) {

			BitSet bs = alternate.getPredictionSet();

			final int[] index = new int[1];
			
			for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i+1)) {
				
				index[0] = i;

				a.getState(regularExpressions.get(i)).addAction(new StateAction() {
					
					private static final long serialVersionUID = 1L;

					@Override
					public void execute(int length, int state) {
						parser.addDescriptor(alternate.getFirstSlot());
						lexer.setTokenAt(ci, index[0], length);
					}
				});
			}
		}
		
		matcher = a.getMatcher().setMode(Matcher.SHORTEST_MATCH);
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
