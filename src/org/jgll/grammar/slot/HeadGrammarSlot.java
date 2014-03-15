package org.jgll.grammar.slot;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Set;

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

	protected final Nonterminal nonterminal;
	
	private boolean nullable;
	
	private Set<Integer> predictionSet;

	protected BodyGrammarSlot firstSlots[];

	private final int nonterminalId;
	
	public HeadGrammarSlot(Nonterminal nonterminal, int nonterminalId, List<List<Symbol>> alts, boolean nullable) {
		this.nonterminal = nonterminal;
		this.nonterminalId = nonterminalId;
		this.firstSlots = new BodyGrammarSlot[alts.size()];
		this.nullable = nullable;
	}
		
	public boolean isNullable() {
		return nullable;
	}
		
	public boolean test(int v) {
		return true;
	}
	
	public void setFirstGrammarSlotForAlternate(BodyGrammarSlot slot, int index) {
		firstSlots[index] = slot;
	}
	
	public BodyGrammarSlot[] getFirstSlots() {
		return firstSlots;
	}
	
	@Override
	public GrammarSlot parse(GLLParser parser, GLLLexer lexer) {
		
		for(BodyGrammarSlot slot : firstSlots) {
			parser.addDescriptor(slot);
		}
		
		return null;
	}
	
	@Override
	public GrammarSlot recognize(GLLRecognizer recognizer, GLLLexer lexer) {
		int ci = recognizer.getCi();
		
		for(BodyGrammarSlot slot : firstSlots) {
			// TODO: put the check for recognizers here.
//			if(lexer.match(ci, alternate.getMatcher())) {
				org.jgll.recognizer.GSSNode cu = recognizer.getCu();
				recognizer.add(slot, cu, ci);
//			}
		}
		return null;
	}
	
	@Override
	public void codeParser(Writer writer) throws IOException {
		writer.append("// " + nonterminal.getName() + "\n");
		writer.append("private void parse_" + id + "() {\n");
		for(BodyGrammarSlot slot : firstSlots) {
			writer.append("   //" + slot + "\n");
			slot.codeIfTestSetCheck(writer);			
			writer.append("   add(grammar.getGrammarSlot(" + slot.getId() + "), cu, ci, DummyNode.getInstance());\n");
			writer.append("}\n");
		}
		writer.append("   label = L0;\n");
		writer.append("}\n");

		for(BodyGrammarSlot slot : firstSlots) {
			writer.append("// " + slot + "\n");
			writer.append("private void parse_" + slot.getId() + "() {\n");
			slot.codeParser(writer);
		}
	}
	
	public Nonterminal getNonterminal() {
		return nonterminal;
	}
		
	public Set<Integer> getPredictionSet() {
		return predictionSet;
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
