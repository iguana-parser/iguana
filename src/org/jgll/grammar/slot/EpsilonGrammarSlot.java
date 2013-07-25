package org.jgll.grammar.slot;

import java.io.IOException;
import java.io.Writer;

import org.jgll.grammar.HeadGrammarSlot;
import org.jgll.parser.GLLParser;
import org.jgll.parser.GSSNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TerminalSymbolNode;
import org.jgll.util.Input;

/**
 * The grammar slot representing an empty body.
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class EpsilonGrammarSlot extends LastGrammarSlot {

	private static final long serialVersionUID = 1L;
	
	public EpsilonGrammarSlot(String label, int position, HeadGrammarSlot head, Object object) {
		super(label, position, null, head, object);
	}
	
	@Override
	public GrammarSlot parse(GLLParser parser, Input intput) {
		// A ::= ε
		int ci = parser.getCi();
		SPPFNode cn = parser.getCn();
		GSSNode cu = parser.getCu();
		TerminalSymbolNode cr = parser.getNodeT(TerminalSymbolNode.EPSILON, ci);
		cn = parser.getNodeP(this, cn, cr);
		parser.update(cu, cn, ci);
		parser.pop(cu, ci, cn);
		return null;
	}
	
	@Override
	public void codeParser(Writer writer) throws IOException {
		/**
		 * code(A ::= ε) =
		 * 				  cR ::= getNodeT(ε,cI);
		 * 				  cN ::= getNodeP(A ::= ·,cN,cR)
		 * 				  pop(cU , cI , cN );
		 * 				  goto L0;
		 */
		writer.append("   cr = getNodeT(-2, ci);\n");
		writer.append("   cn = getNodeP(grammar.getGrammarSlot(" + id + "), cn, cr);\n");
		writer.append("   pop(cu, ci, cn);\n");
		writer.append("   label = L0;\n}\n");
	}
	
	@Override
	public boolean checkAgainstTestSet(int index, Input input) {
		// TODO: add the check against the follow set here.
		return true;
	}
}
