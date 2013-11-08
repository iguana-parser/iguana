package org.jgll.grammar.slot;

import java.io.IOException;
import java.io.Writer;

import org.jgll.grammar.symbol.Epsilon;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.parser.GLLParserInternals;
import org.jgll.sppf.NonPackedNode;
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
	
	public EpsilonGrammarSlot(int position, HeadGrammarSlot head, Object object) {
		super(position, null, head, object);
	}
	
	@Override
	public GrammarSlot parse(GLLParserInternals parser, Input input) {
		
		if(executePreConditions(parser, input)) {
			return null;
		}
		
		// A ::= ε
		// Do not create epsilon nodes
		int ci = parser.getCurrentInputIndex();
		NonPackedNode node = parser.getLookupTable().getNonPackedNode(this.getHead(), ci, ci);
		node.addFirstPackedNode(this, ci);
		parser.setCurrentSPPFNode(node);
		parser.pop();
		return null;
	}
	
	@Override
	public Symbol getSymbol() {
		return Epsilon.getInstance();
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
}
