package org.jgll.grammar.slot;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgll.grammar.symbol.Alternate;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.lexer.GLLLexer;
import org.jgll.parser.GLLParser;
import org.jgll.recognizer.GLLRecognizer;
import org.jgll.sppf.ListSymbolNode;
import org.jgll.sppf.NonterminalSymbolNode;

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

	protected List<Alternate> alternates;
	
	protected final Nonterminal nonterminal;
	
	private boolean nullable;
	
	private Set<Integer> predictionSet;

	protected List<List<Symbol>> alts;

	private final int nonterminalId;
	
	public HeadGrammarSlot(Nonterminal nonterminal, int nonterminalId, List<List<Symbol>> alts, boolean nullable) {
		this.nonterminal = nonterminal;
		this.nonterminalId = nonterminalId;
		this.alts = alts;
		this.nullable = nullable;
		this.alternates = new ArrayList<>();
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
		
	public void removeAllAlternates() {
		alternates.clear();
	}
		
	public boolean isNullable() {
		return nullable;
	}
		
	public boolean test(int v) {
		return true;
	}
	
	@Override
	public GrammarSlot parse(GLLParser parser, GLLLexer lexer) {
		
		for(Alternate alt : alternates) {
			parser.addDescriptor(alt.getFirstSlot());
		}
		
		return null;
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
	
	public int getCountAlternates() {
		return alternates.size();
	}
	
	public Set<Integer> getPredictionSet() {
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
	
	public NonterminalSymbolNode createSPPFNode(int nonterminalId, int numberOfAlternatives, int leftExtent, int rightExtent) {
		if(nonterminal.isEbnfList()) {
			return new ListSymbolNode(nonterminalId, numberOfAlternatives, leftExtent, rightExtent);
		} else {
			return new NonterminalSymbolNode(nonterminalId, numberOfAlternatives, leftExtent, rightExtent);
		}
	}

	@Override
	public int getNodeId() {
		return nonterminalId;
	}
	
}
