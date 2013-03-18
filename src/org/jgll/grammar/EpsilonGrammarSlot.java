package org.jgll.grammar;

import java.io.IOException;
import java.io.Writer;
import java.util.Set;

import org.jgll.parser.GLLParser;
import org.jgll.parser.GSSNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TerminalSymbolNode;
import org.jgll.util.Input;

/**
 * The grammar slot repersing an empty body.
 * 
 * @author Ali Afroozeh
 *
 */
public class EpsilonGrammarSlot extends LastGrammarSlot {

	private static final long serialVersionUID = 1L;
	private final Set<Terminal> testSet;
	
	public EpsilonGrammarSlot(int id, int position, Set<Terminal> testSet, HeadGrammarSlot head, Object object) {
		super(id, position, null, head, object);
		this.testSet = testSet;
	}
	
	@Override
	public void parse(GLLParser parser, Input intput, GSSNode cu, SPPFNode cn, int ci) {
		// A ::= ε
		TerminalSymbolNode cr = parser.getNodeT(TerminalSymbolNode.EPSILON, ci);
		SPPFNode newNode = parser.getNodeP(this, cn, cr);
		parser.pop(cu, ci, newNode);
	}
	
	@Override
	public void code(Writer writer) throws IOException {
		// code(A ::= ε) = 
		// 					cR := getNodeT(ε,cI); 
		// 					cN := getNodeP(A ::= ·,cN,cR)
		// 					pop(cU , cI , cN ); 
		// 					goto L0
		writer.append("   cr = getNodeT(-2, ci);\n");
		writer.append("   cn = getNodeP(grammar.getGrammarSlot(" + id + "), cn, cr);\n");
		writer.append("   pop(cu, ci, cn);\n");
		writer.append("   label = L0;\n}\n");
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
	public String toString() {
		return head.getName() + " ::= .";
	}
	
}
