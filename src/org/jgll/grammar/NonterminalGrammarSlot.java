package org.jgll.grammar;

import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
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
	
	private Set<Terminal> testSet;
	
	public NonterminalGrammarSlot(int id, int position, BodyGrammarSlot previous, HeadGrammarSlot nonterminal, HeadGrammarSlot head) {
		super(id, position, previous, head);
		if(nonterminal == null) {
			throw new IllegalArgumentException("Nonterminal cannot be null.");
		}
		this.nonterminal = nonterminal;
		testSet = new HashSet<>();
	}
	
	public HeadGrammarSlot getNonterminal() {
		return nonterminal;
	}
	
	public void setNonterminal(HeadGrammarSlot nonterminal) {
		this.nonterminal = nonterminal;
	}
	
	
	@Override
	public void parse(GLLParser parser, Input input, GSSNode cu, SPPFNode cn, int ci) {
		if(checkAgainstTestSet(input.get(ci))) {
			cu = parser.create(next, cu, ci, cn);
			nonterminal.parse(parser, input, cu, cn, ci);
		} else {
			parser.newParseError(this, ci);
		}		
	}
	
	@Override
	public void recognize(GLLRecognizer recognizer, Input input, org.jgll.recognizer.GSSNode cu, int ci) {
		
	}
	
	@Override
	public void code(Writer writer) throws IOException {
		
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
				slot.code(writer);
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
		int i = 0;
		for(Terminal terminal : testSet) {
			writer.append(terminal.getMatchCode());
			if(++i < testSet.size()) {
				writer.append(" || ");
			}
		}
		writer.append(") {\n");
	}

	@Override
	public boolean checkAgainstTestSet(int i) {
		if(testSet.isEmpty()) {
			return true;
		}
		for(Terminal t : testSet) {
			if(t.match(i)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Iterable<Terminal> getTestSet() {
		return testSet;
	}
	
	public void setTestSet(Set<Terminal> testSet) {
		if(testSet == null || testSet.isEmpty()) {
			throw new IllegalArgumentException("Test set cannot be null or empty");
		}
		this.testSet = testSet;
	}
	
	@Override
	public String getName() {
		return nonterminal.getName();
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
	
}