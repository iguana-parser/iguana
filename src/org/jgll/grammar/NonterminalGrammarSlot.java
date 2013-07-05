package org.jgll.grammar;

import java.io.IOException;
import java.io.Writer;
import java.util.BitSet;
import java.util.Set;

import org.jgll.parser.GLLParser;
import org.jgll.parser.GSSNode;
import org.jgll.recognizer.GLLRecognizer;
import org.jgll.sppf.SPPFNode;
import org.jgll.util.Input;


/**
 * A grammar slot immediately before a nonterminal.
 *
 * @author Ali Afroozeh
 *
 */
public class NonterminalGrammarSlot extends BodyGrammarSlot {
	
	private static final long serialVersionUID = 1L;

	private HeadGrammarSlot nonterminal;
	
	private BitSet testSet;
	
	private int minInputVal = Integer.MAX_VALUE;
	
	private int maxInputVal;
	
	public NonterminalGrammarSlot(String label, int position, BodyGrammarSlot previous, HeadGrammarSlot nonterminal, HeadGrammarSlot head) {
		super(label, position, previous, head);
		if(nonterminal == null) {
			throw new IllegalArgumentException("Nonterminal cannot be null.");
		}
		this.nonterminal = nonterminal;
		testSet = new BitSet();
	}
	
	public HeadGrammarSlot getNonterminal() {
		return nonterminal;
	}
	
	public void setNonterminal(HeadGrammarSlot nonterminal) {
		this.nonterminal = nonterminal;
	}
	
	@Override
	public GrammarSlot parse(GLLParser parser, Input input) {
		
		for(SlotAction<Boolean> preCondition : preConditions) {
			if(!preCondition.execute(parser, input)) {
				return null;
			}
		}
		
		int ci = parser.getCi();
		GSSNode cu = parser.getCu();
		SPPFNode cn = parser.getCn();
		if(checkAgainstTestSet(input.charAt(ci))) {
			parser.update(parser.create(next, cu, ci, cn), cn, ci);
			return nonterminal;
		} else {
			parser.newParseError(this, ci, parser.getCu());
			return null;
		}		
	}
	
	@Override
	public GrammarSlot recognize(GLLRecognizer recognizer, Input input) {
		return null;
	}
	
	@Override
	public void codeParser(Writer writer) throws IOException {
		
		if(previous == null) {
			codeIfTestSetCheck(writer);
			writer.append("   cu = create(grammar.getGrammarSlot(" + next.id + "), cu, ci, cn);\n");
			writer.append("   label = " + nonterminal.getId() + ";\n");
			codeElseTestSetCheck(writer);
			writer.append("}\n");
			
			writer.append("// " + next + "\n");
			writer.append("private void parse_" + next.id + "() {\n");
			
			BodyGrammarSlot slot = next;
			while(slot != null) {
				slot.codeParser(writer);
				slot = slot.next;
			}
		} 
		
		else { 
		
			// TODO: add the testSet check
			// code(A ::= α · Xl β) = 
			//						if(test(I[cI ], A, Xβ) {
			// 							cU :=create(RXl,cU,cI,cN); 
			//							gotoLX 
			//						}
			// 						else goto L0
			// RXl:
			codeIfTestSetCheck(writer);
			writer.append("   cu = create(grammar.getGrammarSlot(" + next.id + "), cu, ci, cn);\n");
			writer.append("   label = " + nonterminal.getId() + ";\n");
			codeElseTestSetCheck(writer);
			writer.append("}\n");
			
			writer.append("// " + next + "\n");
			writer.append("private void parse_" + next.id + "(){\n");
		}
	}
	
	@Override
	public void codeIfTestSetCheck(Writer writer) throws IOException {
		writer.append("if (");
//		int i = 0;
//		for(Terminal terminal : testSet) {
//			writer.append(terminal.getMatchCode());
//			if(++i < testSet.size()) {
//				writer.append(" || ");
//			}
//		}
		writer.append(") {\n");
	}

	@Override
	public boolean checkAgainstTestSet(int i) {
		if(i < minInputVal || i > maxInputVal) {
			return false;
		}
		return testSet.get(i);
	}

	public void setTestSet(Set<Terminal> testSet) {
		if(testSet == null || testSet.isEmpty()) {
			throw new IllegalArgumentException("Test set for the nontermianl " + nonterminal.getNonterminal().getName() + " is empty");
		}
		
		for(Terminal t : testSet) {
			if(minInputVal > t.getMinimumValue()) {
				minInputVal = t.getMinimumValue();
			}
			if(maxInputVal < t.getMaximumValue()) {
				maxInputVal = t.getMaximumValue();
			}
			this.testSet.or(t.getTestSet());
		}
	}
	
	@Override
	public boolean isTerminalSlot() {
		return false;
	}

	@Override
	public boolean isNonterminalSlot() {
		return true;
	}

	@Override
	public boolean isLastSlot() {
		return false;
	}

	@Override
	public boolean isNullable() {
		return nonterminal.isNullable();
	}

	@Override
	public Symbol getSymbol() {
		return nonterminal.getNonterminal();
	}

	
}