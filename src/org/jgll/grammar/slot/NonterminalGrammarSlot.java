package org.jgll.grammar.slot;

import java.io.IOException;
import java.io.Writer;
import java.util.BitSet;

import org.jgll.grammar.HeadGrammarSlot;
import org.jgll.grammar.SlotAction;
import org.jgll.grammar.Symbol;
import org.jgll.grammar.Terminal;
import org.jgll.parser.GLLParser;
import org.jgll.recognizer.GLLRecognizer;
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
	
	private BitSet firstSet;
	private BitSet followSet;
		
	public NonterminalGrammarSlot(String label, int position, BodyGrammarSlot previous, HeadGrammarSlot nonterminal, HeadGrammarSlot head) {
		super(label, position, previous, head);
		if(nonterminal == null) {
			throw new IllegalArgumentException("Nonterminal cannot be null.");
		}
		this.nonterminal = nonterminal;
		this.firstSet = new BitSet();
		this.followSet = new BitSet();
	}
	
	public HeadGrammarSlot getNonterminal() {
		return nonterminal;
	}
	
	public void setNonterminal(HeadGrammarSlot nonterminal) {
		this.nonterminal = nonterminal;
	}
	
	@Override
	public GrammarSlot parse(GLLParser parser, Input input) {
		
		int ci = parser.getCurrentInputIndex();
		
		if(!testFirstSet(ci, input) && !(isNullable() && testFollowSet(ci, input))) {
			parser.recordParseError(this);
			return null;			
		}
		
		for(SlotAction<Boolean> preCondition : preConditions) {
			if(!preCondition.execute(parser, input)) {
				return null;
			}
		}
		
		parser.createGSSNode(next);
		return nonterminal;
	}
	
	@Override
	public GrammarSlot recognize(GLLRecognizer recognizer, Input input) {
		int ci = recognizer.getCi();
		org.jgll.recognizer.GSSNode cu = recognizer.getCu();
		
		if(testFirstSet(ci, input)) {
			recognizer.update(recognizer.create(next, cu, ci), ci);
			return nonterminal;
		} 
		else if (isNullable() && testFollowSet(ci, input)) {
			// We can always recognize an epsilon, so move on to the next step.
			// This is much more tricky to implement for a parser, as we need to explore
			// nullable paths.
			return next;
		
		} else {
			recognizer.recognitionError(cu, ci);
			return null;
		}		

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
	
	public void setTestSet() {
		for(Terminal t : nonterminal.getFirstSet()) {
			firstSet.or(t.asBitSet());
		}
		
		for(Terminal t : nonterminal.getFollowSet()) {
			followSet.or(t.asBitSet());
		}
	}

	@Override
	public boolean testFirstSet(int index, Input input) {
		return firstSet.get(input.charAt(index));
	}

	@Override
	public boolean testFollowSet(int index, Input input) {
		return followSet.get(input.charAt(index));
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